package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ArchetypeValidatingVisitor;

/**
 * TODO: check that enumeration type constraints use valid literal values (VCORMENV, VCORMENU, VCORMEN);
 * TODO: VCORM primtive types
 * Created by pieter.bos on 31/03/2017.
 */
public class ValidateAgainstReferenceModel extends ArchetypeValidatingVisitor {

    public ValidateAgainstReferenceModel() {
        super();
    }
    
    @Override
    protected void validate(CComplexObject cObject) {

        if (!combinedModels.typeNameExists(cObject.getRmTypeName())) {
            addMessageWithPath(ErrorType.VCORM, cObject.getPath(), cObject.getRmTypeName());
        } else {
            CAttribute owningAttribute = cObject.getParent();

            if (owningAttribute != null) { //at path "/" there will be no owning attribute
                CObject owningObject = owningAttribute.getParent();
                if(owningAttribute.getDifferentialPath() != null && flatParent != null) {
                    CAttribute differentialPathFromParent = (CAttribute) AOMUtils.getDifferentialPathFromParent(flatParent, owningAttribute);
                    owningObject =  differentialPathFromParent == null ? null : differentialPathFromParent.getParent();
                }
                if (owningObject != null) {
                    if(!combinedModels.typeConformant(owningObject.getRmTypeName(), owningAttribute.getRmAttributeName(), cObject.getRmTypeName())) {
                        addMessageWithPath(ErrorType.VCORMT, cObject.getPath(),
                                owningObject.getRmTypeName()  + "." + owningAttribute.getRmAttributeName()+ " cannot contain type " + cObject.getRmTypeName());
                    }

                }

            }
        }
    }

    @Override
    protected void validate(CPrimitiveObject cObject) {
        CAttribute attribute = cObject.getParent();
        if(attribute.getDifferentialPath() == null) {
            CObject parentConstraint = attribute.getParent();
            if(!combinedModels.validatePrimitiveType(parentConstraint.getRmTypeName(), attribute.getRmAttributeName(), cObject)) {
                addMessage(ErrorType.VCORMT, cObject.path());
            }

            //TODO: we need AOM_PROFILE here instead
        } else {
            if (flatParent != null) {
                ArchetypeModelObject differentialPathFromParent = AOMUtils.getDifferentialPathFromParent(flatParent, attribute);
                if(differentialPathFromParent instanceof CAttribute) {
                    CAttribute parentAttribute = (CAttribute) differentialPathFromParent;
                    CObject parentConstraint = parentAttribute.getParent();
                    if(!combinedModels.validatePrimitiveType(parentConstraint.getRmTypeName(), parentAttribute.getRmAttributeName(), cObject)) {
                        addMessage(ErrorType.VCORMT, cObject.path());
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
            if (!combinedModels.attributeExists(owningObject.getRmTypeName(), cAttribute.getRmAttributeName())) {
                addMessageWithPath(ErrorType.VCARM, cAttribute.getPath(), cAttribute.getRmAttributeName() + " is not a known attribute of " + owningObject.getRmTypeName() + " or it is has not been implemented in Archie");
            } else {
                CAttribute defaultAttribute = new ReflectionConstraintImposer(combinedModels).getDefaultAttribute(owningObject.getRmTypeName(), cAttribute.getRmAttributeName());
                if(defaultAttribute != null) {
                    if(cAttribute.getExistence() != null) {
                        if(!defaultAttribute.getExistence().contains(cAttribute.getExistence())) {
                            if(!archetype.isSpecialized() && defaultAttribute.getExistence().equals(cAttribute.getExistence())) {
                                if(settings.isStrictMultiplicitiesSpecializationValidation()) {
                                    addMessageWithPath(ErrorType.VCAEX, cAttribute.path());
                                } else {
                                    //TODO: warn
                                    cAttribute.setExistence(null);
                                }
                            } else {
                                addMessageWithPath(ErrorType.VCAEX, cAttribute.path());
                            }
                        }
                    }
                    if(defaultAttribute.isMultiple()) {
                        if(defaultAttribute.getCardinality() != null && cAttribute.getCardinality() != null && !defaultAttribute.getCardinality().contains(cAttribute.getCardinality())){
                            if(defaultAttribute.getCardinality().equals(cAttribute.getCardinality())) {
                                if(settings.isStrictMultiplicitiesSpecializationValidation()) {
                                    addMessageWithPath(ErrorType.VCACA, cAttribute.path());
                                } else {
                                    //TODO: warning
                                    cAttribute.setCardinality(null);
                                }
                            } else {
                                addMessageWithPath(ErrorType.VCACA, cAttribute.path());
                            }
                        }
                    } else {
                        if(cAttribute.getCardinality() != null) {
                            addMessageWithPath(ErrorType.VCAM, defaultAttribute.path(), "single valued attributes can not have a cardinality");
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
