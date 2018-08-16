package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ArchetypeValidatingVisitor;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;

/**
 * Phase 0 validator on existence
 */
public class MultiplicitiesValidation extends ArchetypeValidatingVisitor {
    public MultiplicitiesValidation() {
        super();
    }

    @Override
    public void validate(CAttribute attribute) {
        validateExistence(attribute);
    }

    public void validate(CObject cObject) {
        if(cObject.getParent() != null) {
            CAttribute attribute = cObject.getParent();
            if(attribute.getDifferentialPath() == null) {
                //we cannot validate differential paths here because we do not know the type
                //TODO: lookup differential path types to validate as well?
                if(attribute.isSingle()) {
                    if(cObject.getOccurrences() != null &&
                        (cObject.getOccurrences().isUpperUnbounded() || cObject.getOccurrences().getUpper() > 1)) {
                        addMessageWithPath(ErrorType.VACSO, cObject.path());
                    }
                } else {
                    //multiple
                    Cardinality cardinality = attribute.getCardinality();
                    if(cardinality != null && !cardinality.getInterval().isUpperUnbounded() &&
                            cObject.getOccurrences() != null  && !cObject.getOccurrences().isUpperUnbounded() &&
                            cObject.getOccurrences().getUpper() > cardinality.getInterval().getUpper()) {
                        addMessageWithPath(ErrorType.VACMCU, cObject.path());
                    }

                }
            }
        }

    }

    private void validateExistence(CAttribute attribute) {
        MultiplicityInterval existence = attribute.getExistence();
        if(existence != null) {
            if(existence.getLower() == null || existence.getUpper() == null || existence.isUpperUnbounded() ||
                    existence.isLowerUnbounded() || !existence.isLowerIncluded() || !existence.isUpperIncluded()) {
                //not good, can only be a very limited set
                addMessageWithPath(ErrorType.SEXLMG, attribute.path());
            } else {
                if(existence.getLower() == 0) {
                  if(existence.getUpper() != 0 && existence.getUpper() != 1) {
                      addMessageWithPath(ErrorType.SEXLU, attribute.path());
                  }
                } else if(existence.getLower() == 1) {
                    if(existence.getUpper() != 1) {
                        addMessageWithPath(ErrorType.SEXLU, attribute.path());
                    }
                }
            }
        }
    }

}
