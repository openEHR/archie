package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import org.openehr.utils.message.I18n;

/**
 * TODO: check that enumeration type constraints use valid literal values (VCORMENV, VCORMENU, VCORMEN);
 * TODO: VCORM primtive types
 * Created by pieter.bos on 31/03/2017.
 */
public class ValidateAgainstReferenceModel extends ValidatingVisitor {

    public ValidateAgainstReferenceModel() {
        super();
    }
    
    @Override
    protected void validate(CComplexObject cObject) {
        validateTypes(cObject);
    }

    private void validateTypes(CObject cObject) {
        if (!metaModel.typeNameExists(cObject.getRmTypeName())) {
            addMessageWithPath(ErrorType.VCORM, cObject.getPath(), I18n.t("Type name {0} does not exist", cObject.getRmTypeName()));
        } else {
            CAttribute owningAttribute = cObject.getParent();

            if (owningAttribute != null) { //at path "/" there will be no owning attribute
                CObject owningObject = owningAttribute.getParent();
                if(owningAttribute.getDifferentialPath() != null && flatParent != null) {
                    CAttribute differentialPathFromParent = (CAttribute) AOMUtils.getDifferentialPathFromParent(flatParent, owningAttribute);
                    owningObject =  differentialPathFromParent == null ? null : differentialPathFromParent.getParent();
                }
                if (owningObject != null) {
                    if(!metaModel.typeConformant(owningObject.getRmTypeName(), owningAttribute.getRmAttributeName(), cObject.getRmTypeName())) {
                        addMessageWithPath(ErrorType.VCORMT, cObject.getPath(),
                                I18n.t("Attribute {0}.{1} cannot contain type {2}",
                                        owningObject.getRmTypeName(), owningAttribute.getRmAttributeName(), cObject.getRmTypeName()));
                    }

                }

            }
        }
    }

    @Override
    protected void validate(CComplexObjectProxy cObject) {
        validateTypes(cObject);
    }

    @Override
    protected void validate(CPrimitiveObject<?, ?> cObject) {
        CAttribute attribute = cObject.getParent();
        if(attribute.getDifferentialPath() == null) {
            CObject parentConstraint = attribute.getParent();
            if(!metaModel.validatePrimitiveType(parentConstraint.getRmTypeName(), attribute.getRmAttributeName(), cObject)) {
                addMessageWithPath(ErrorType.VCORMT, cObject.path(),
                        I18n.t("Attribute {0}.{1} cannot be constrained by a {2}",
                                parentConstraint.getRmTypeName(), attribute.getRmAttributeName(), cObject == null ? null : cObject.getClass().getSimpleName()));
            }

            //TODO: we need AOM_PROFILE here instead
        } else {
            if (flatParent != null) {
                ArchetypeModelObject differentialPathFromParent = AOMUtils.getDifferentialPathFromParent(flatParent, attribute);
                if(differentialPathFromParent instanceof CAttribute) {
                    CAttribute parentAttribute = (CAttribute) differentialPathFromParent;
                    CObject parentConstraint = parentAttribute.getParent();
                    if(!metaModel.validatePrimitiveType(parentConstraint.getRmTypeName(), parentAttribute.getRmAttributeName(), cObject)) {
                        I18n.t("Attribute {0}.{1} cannot be constrained by a {2}",
                                parentConstraint.getRmTypeName(), parentAttribute.getRmAttributeName(), cObject == null ? null : cObject.getClass().getSimpleName());
                    }
                }
            }
        }


    }


    @Override
    public void validate(CAttribute cAttribute) {
        if(flatParent == null && cAttribute.getDifferentialPath() != null) {
            return;
        }
        CObject owningObject = cAttribute.getParent();
        if(cAttribute.getDifferentialPath() != null) {
            CAttribute differentialPathFromParent = (CAttribute) AOMUtils.getDifferentialPathFromParent(flatParent, cAttribute);
            owningObject =  differentialPathFromParent == null ? null : differentialPathFromParent.getParent();
        }
        if(owningObject != null) {
            if (!metaModel.attributeExists(owningObject.getRmTypeName(), cAttribute.getRmAttributeName())) {
                addMessageWithPath(ErrorType.VCARM, cAttribute.getPath(),
                        I18n.t("{0} is not a known attribute of {1}", cAttribute.getRmAttributeName(), owningObject.getRmTypeName()));
            } else {
                CAttribute defaultAttribute = new ReflectionConstraintImposer(metaModel).getDefaultAttribute(owningObject.getRmTypeName(), cAttribute.getRmAttributeName());
                if(defaultAttribute != null) {
                    if(cAttribute.getExistence() != null) {
                        if(!defaultAttribute.getExistence().contains(cAttribute.getExistence())) {
                            if(!archetype.isSpecialized() && defaultAttribute.getExistence().equals(cAttribute.getExistence())) {
                                if(settings.isStrictMultiplicitiesSpecializationValidation()) {
                                    //TODO: this adds an error if the RM existence is the same as the child existence. WHY!?
                                    addMessageWithPath(ErrorType.VCAEX, cAttribute.path(),
                                            I18n.t("The existence of attribute {0}.{1} is the same as in the reference model - this is not allowed due to strict existence validation being enabled",
                                                    owningObject.getRmTypeName(), cAttribute.getRmAttributeName()));
                                } else {
                                    //TODO: warn
                                    cAttribute.setExistence(null);
                                }
                            } else {
                                addMessageWithPath(ErrorType.VCAEX, cAttribute.path(),
                                        I18n.t("The existence {0} of attribute {2}.{3} does not match existence {1} of the reference model",
                                                cAttribute.getExistence(), defaultAttribute.getExistence(),
                                                owningObject.getRmTypeName(), cAttribute.getRmAttributeName()));
                            }
                        }
                    }
                    if(defaultAttribute.isMultiple()) {
                        if(defaultAttribute.getCardinality() != null && cAttribute.getCardinality() != null && cAttribute.getCardinality().getInterval() != null && !defaultAttribute.getCardinality().contains(cAttribute.getCardinality())){
                            if(defaultAttribute.getCardinality().equals(cAttribute.getCardinality())) {
                                if(settings.isStrictMultiplicitiesSpecializationValidation()) {
                                    addMessageWithPath(ErrorType.VCACA, cAttribute.path(),
                                            I18n.t("The cardinality of Attribute {0}.{1} is the same as in the reference model - this is not allowed due to strict multiplicities validation being enabled",
                                                    owningObject.getRmTypeName(), cAttribute.getRmAttributeName()));
                                } else {
                                    //TODO: warning. Or just don't serialize?
                                    cAttribute.setCardinality(null);
                                }
                            } else {
                                addMessageWithPath(ErrorType.VCACA, cAttribute.path(),
                                        I18n.t("The cardinality {0} of attribute {2}.{3} does not match cardinality {1} of the reference model",
                                                cAttribute.getCardinality(), defaultAttribute.getCardinality(),
                                                owningObject.getRmTypeName(), cAttribute.getRmAttributeName()));
                            }
                        }
                    } else {
                        if(cAttribute.getCardinality() != null) {
                            addMessageWithPath(ErrorType.VCAM, defaultAttribute.path(),
                                    I18n.t("Single valued attributes can not have a cardinality"));
                        }
                        //TODO: single/multiple validation. but this is not set in parsing and not in archetype, so only useful during editing
                        //this is VCAMm and VCAMs in eiffel code
                        //will result in errors with current AOM implementation unless we do constraint imposing first, which would cause trouble with validations. SO let's not now.
                    }
                }
                //if computed. warn. why?
                //TODO: VCAM, multiplicity
            }

        }
    }
}
