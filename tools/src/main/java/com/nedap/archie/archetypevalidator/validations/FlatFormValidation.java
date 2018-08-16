package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CComplexObjectProxy;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ArchetypeValidatingVisitor;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.query.ComplexObjectProxyReplacement;

import java.net.URI;
import java.util.Map;

public class FlatFormValidation extends ArchetypeValidatingVisitor {

    @Override
    protected void validate(CComplexObjectProxy cObject) {
        //validate that CComplexObjectProxy nodes have a valid path
        ComplexObjectProxyReplacement complexObjectProxyReplacement = ComplexObjectProxyReplacement.getComplexObjectProxyReplacement(cObject);
        if(complexObjectProxyReplacement == null) {
            addMessageWithPath(ErrorType.VUNP, cObject.path());
        } else {
            CComplexObject replacement = complexObjectProxyReplacement.getReplacement();
            if(!combinedModels.rmTypesConformant(replacement.getRmTypeName(), cObject.getRmTypeName())) {
                addMessageWithPath(ErrorType.VUNT, cObject.path());
            }
        }
    }

    @Override
    protected void validate(CAttribute cAttribute) {
        Cardinality cardinality = cAttribute.getCardinality();
        if(cardinality != null && !cardinality.getInterval().isUpperUnbounded()) {
            if(cAttribute.getAggregateOccurrencesLowerSum() > cardinality.getInterval().getUpper()) {
                addWarningWithPath(ErrorType.WACMCL, cAttribute.path());
            } else if (cAttribute.getMinimumChildCount() > cardinality.getInterval().getUpper()) {
                addMessageWithPath(ErrorType.VACMCO, cAttribute.path());
            }
        }
    }

    @Override
    protected void beginValidation() {
        //validateTerminologyBindings();
    }

    private void validateTerminologyBindings() {
        ArchetypeTerminology terminology = archetype.getTerminology();
        Map<String, Map<String, URI>> termBindings = terminology.getTermBindings();
        for(String terminologyId: termBindings.keySet()) {
            for(String constraintCodeOrPath: termBindings.get(terminologyId).keySet()) {
                boolean archetypeHasPath = false;
                try {
                    archetypeHasPath = archetype.hasPath(constraintCodeOrPath);
                } catch (Exception e) {
                    //if not a valid path, fine
                }
                if(!AOMUtils.isValidCode(constraintCodeOrPath) && !(
                        archetypeHasPath || combinedModels.hasReferenceModelPath(archetype.getDefinition().getRmTypeName(), constraintCodeOrPath)
                )
                        ) {
                    addMessage(ErrorType.VTTBK, String.format("Term binding key %s in path format is not present in archetype", constraintCodeOrPath));
                }
                else if(AOMUtils.isValidCode(constraintCodeOrPath) &&
                        !terminology.hasCode(constraintCodeOrPath) &&
                        !(archetype.isSpecialized() && flatParent != null && !flatParent.getTerminology().hasCode(constraintCodeOrPath))
                        )
                {
                    addMessage(ErrorType.VTTBK, String.format("Term binding key %s is not present in terminology", constraintCodeOrPath));
                } else {
                    //TODO: two warnings
                }
            }
        }
    }
}
