package com.nedap.archie.rminfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomTypeMapping;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.*;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.util.List;
import java.util.stream.Collectors;

public class MetaModel implements MetaModelInterface {

    private final ModelInfoLookup modelInfoLookup;
    private final BmmModel bmmModel;
    private final AomProfile aomProfile;
    private final ObjectMapper odinInputObjectMapper;
    private final ObjectMapper odinOutputObjectMapper;
    private final ObjectMapper jsonObjectMapper;

    public MetaModel(ModelInfoLookup modelInfoLookup, BmmModel bmmModel) {
        this(modelInfoLookup, bmmModel, null);
    }

    public MetaModel(ModelInfoLookup modelInfoLookup, BmmModel bmmModel, AomProfile aomProfile) {
        this(modelInfoLookup, bmmModel, aomProfile, null);
    }

    public MetaModel(ModelInfoLookup modelInfoLookup, BmmModel bmmModel, AomProfile aomProfile, RMObjectMapperProvider provider) {
        if (modelInfoLookup == null && bmmModel == null) {
            throw new IllegalArgumentException("Either a ModelInfoLookup or a BMM model must be provided");
        }
        this.modelInfoLookup = modelInfoLookup;
        this.bmmModel = bmmModel;
        this.aomProfile = aomProfile;
        if (provider != null) {
            this.odinInputObjectMapper = provider.getInputOdinObjectMapper();
            this.odinOutputObjectMapper = provider.getOutputOdinObjectMapper();
            this.jsonObjectMapper = provider.getJsonObjectMapper();
        } else {
            this.odinInputObjectMapper = null;
            this.odinOutputObjectMapper = null;
            this.jsonObjectMapper = null;
        }
    }

    /**
     * @deprecated Use {@link #getModelInfoLookup()} instead
     */
    @Deprecated
    public ModelInfoLookup getSelectedModel() {
        return modelInfoLookup;
    }

    public ModelInfoLookup getModelInfoLookup() {
        return modelInfoLookup;
    }

    /**
     * @deprecated Use {@link #getBmmModel()} instead
     */
    @Deprecated
    public BmmModel getSelectedBmmModel() {
        return bmmModel;
    }

    public BmmModel getBmmModel() {
        return bmmModel;
    }

    /**
     * @deprecated Use {@link #getAomProfile()} instead
     */
    @Deprecated
    public AomProfile getSelectedAomProfile() {
        return aomProfile;
    }

    public AomProfile getAomProfile() {
        return aomProfile;
    }

    /**
     * Get the object mapper to use for JSON converted from ODIN to parse this model
     * It would be better if this was one object mapper, together with the odinInputObjectMapper, but unfortunately
     * there is not yet one ObjectMapper that can both input converted json and output ODIN directly. Sorry about that.
     *
     * @return the object mapper to use for JSON converted from ODIN to parse this model
     */
    public ObjectMapper getOdinInputObjectMapper() {
        return odinInputObjectMapper;
    }

    /**
     * Get the object mapper to output ODIN from this model.
     * It would be better if this was one object mapper, together with the odinInputObjectMapper, but unfortunately
     * there is not yet one ObjectMapper that can both input converted json and output ODIN directly. Sorry about that.
     *
     * @return Get the object mapper to output ODIN from this model
     */
    public ObjectMapper getOdinOutputObjectMapper() {
        return odinOutputObjectMapper;
    }

    public ObjectMapper getJsonObjectMapper() {
        return jsonObjectMapper;
    }

    @Override
    public boolean isMultiple(String typeName, String attributeNameOrPath) {
        MultiplicityInterval multiplicityInterval = referenceModelPropMultiplicity(typeName, attributeNameOrPath);
        if (multiplicityInterval == null) {
            return false;// by default, false if unknown property
        }
        return multiplicityInterval.isUpperUnbounded() || multiplicityInterval.getUpper() > 1;
    }

    private boolean isMultiple(BmmProperty<?> bmmProperty) {
        if (bmmProperty == null) {
            return false;
        } else if (bmmProperty instanceof BmmContainerProperty) {
            return ((BmmContainerProperty) bmmProperty).getCardinality().has(2);
        } else {
            return false;
        }
    }

    @Override
    public boolean rmTypesConformant(String childTypeName, String parentTypeName) {
        if (bmmModel != null) {
            String parentClassName = BmmDefinitions.typeNameToClassKey(parentTypeName);//generics stripped
            String childClassName = BmmDefinitions.typeNameToClassKey(childTypeName);//generics stripped
            //TODO: generics as well. get the array and check each type?
            BmmClass childClass = bmmModel.getClassDefinition(childClassName);
            if (childClass == null) {
                return true;//will be checked elsewhere
            }
            List<String> allAncestors = childClass.findAllAncestors();
            allAncestors = allAncestors.stream().map(BmmDefinitions::typeNameToClassKey).collect(Collectors.toList());
            return parentClassName.equalsIgnoreCase(childClassName) || allAncestors.contains(parentClassName);
        } else {
            return modelInfoLookup.rmTypesConformant(childTypeName, parentTypeName);
        }
    }

    @Override
    public boolean typeNameExists(String typeName) {
        if (bmmModel != null) {
            return bmmModel.getClassDefinition(typeName) != null;
        } else {
            return modelInfoLookup.getTypeInfo(typeName) != null;
        }
    }

    @Override
    public boolean attributeExists(String rmTypeName, String propertyName) {
        if (bmmModel != null) {
            BmmClass classDefinition = bmmModel.getClassDefinition(rmTypeName);
            if (classDefinition == null) {
                return false;
            }

            return classDefinition.hasFlatPropertyWithName(propertyName);
        } else {
            return modelInfoLookup.getAttributeInfo(rmTypeName, propertyName) != null;
        }
    }

    @Override
    public boolean isNullable(String typeId, String attributeName) {
        if (bmmModel != null) {
            String className = BmmDefinitions.typeNameToClassKey(typeId);
            BmmClass classDefinition = bmmModel.getClassDefinition(className);
            if (classDefinition == null || !classDefinition.hasFlatPropertyWithName(attributeName)) {
                return false;
            }

            BmmProperty<?> bmmProperty = classDefinition.getFlatProperties().get(attributeName);
            return !bmmProperty.getMandatory() || (bmmProperty.getExistence() != null && !bmmProperty.getExistence().isMandatory());
        } else {
            return modelInfoLookup.getAttributeInfo(typeId, attributeName).isNullable();
        }
    }

    /**
     * return whether the attribute identified by rmTypeName.rmAttributeName can contain the type
     * childConstraintTypeName
     */
    @Override
    public boolean typeConformant(String rmTypeName, String rmAttributeName, String childConstraintTypeName) {
        if (bmmModel != null) {
            BmmClass parentClass = bmmModel.getClassDefinition(rmTypeName);
            BmmClass childClass = bmmModel.getClassDefinition(childConstraintTypeName);
            if (childClass != null && parentClass != null) {
                BmmProperty<?> property = parentClass.getFlatProperties().get(rmAttributeName);
                if (property != null) {
                    String propertyConfTypeName = property.getType().getEffectiveType().getTypeName();
                    // if(BmmDefinitions.validGenericTypeName(propertyConfTypeName) &&
                    //         !BmmDefinitions.validGenericTypeName(childConstraintTypeName)) {

                    //}
                    return rmTypesConformant(childConstraintTypeName, propertyConfTypeName);
                }
            }
            return false;
        } else {
            RMTypeInfo typeInfo = modelInfoLookup.getTypeInfo(childConstraintTypeName);
            RMAttributeInfo owningAttributeInfo = modelInfoLookup.getAttributeInfo(rmTypeName, rmAttributeName);
            if (owningAttributeInfo != null) {//this case is another validation, see the validate(cattribute) method of this class
                Class<?> typeInCollection = owningAttributeInfo.getTypeInCollection();
                return typeInCollection.isAssignableFrom(typeInfo.getJavaClass());
            }
            return true;
        }
    }

    @Override
    public boolean hasReferenceModelPath(String rmTypeName, String path) {
        if (!path.startsWith("/")) {
            return false;
        }

        if (bmmModel != null) {
            return bmmModel.hasPropertyAtPath(rmTypeName, path);
        } else {
            return AOMUtils.getAttributeInfoAtPath(modelInfoLookup, rmTypeName, path) != null;
        }
    }

    @Override
    public MultiplicityInterval referenceModelPropMultiplicity(String rmTypeName, String rmAttributeNameOrPath) {
        if (bmmModel != null) {
            BmmProperty<?> bmmProperty = bmmModel.propertyAtPath(rmTypeName, rmAttributeNameOrPath);
            if (bmmProperty == null) {
                return null;
            }
            if (isMultiple(bmmProperty)) {
                return MultiplicityInterval.createUpperUnbounded(0);
            } else {
                if (!bmmProperty.getMandatory()) {
                    return MultiplicityInterval.createBounded(0, 1);
                } else {
                    return MultiplicityInterval.createBounded(1, 1);
                }
            }
        } else {
            RMAttributeInfo attributeInfo = AOMUtils.getAttributeInfoAtPath(modelInfoLookup, rmTypeName, rmAttributeNameOrPath);
            if (attributeInfo == null) {
                return null;
            }
            if (attributeInfo.isMultipleValued()) {
                return MultiplicityInterval.createUpperUnbounded(0);
            } else {
                if (attributeInfo.isNullable()) {
                    return MultiplicityInterval.createBounded(0, 1);
                } else {
                    return MultiplicityInterval.createBounded(1, 1);
                }
            }
        }
    }

    @Override
    public boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject<?, ?> cObject) {
        if (aomProfile == null && modelInfoLookup == null) {
            throw new IllegalStateException("no AOM profile and no selected ModelInfoLookup, cannot validate primitive type");
        } else if (aomProfile == null) {
            return modelInfoLookup.validatePrimitiveType(rmTypeName, rmAttributeName, cObject);
        } else {
            String cRmTypeName = cObject.getRmTypeName();
            AomTypeMapping aomTypeMapping = aomProfile.getAomRmTypeMappings().get(cRmTypeName.toUpperCase());
            if (aomTypeMapping != null) {
                //found a type mapping, replace effective type name
                cRmTypeName = aomTypeMapping.getTargetClassName();
            }
            String modelTypeName = bmmModel.effectivePropertyType(rmTypeName, rmAttributeName);
            BmmClass bmmClass = bmmModel.getClassDefinition(rmTypeName);
            if (bmmClass != null) {
                BmmProperty<?> bmmProperty = bmmClass.getFlatProperties().get(rmAttributeName);
                if (bmmProperty != null) {
                    //check enumerated properties
                    BmmEffectiveType propertyEffectiveType = bmmProperty.getType().getEffectiveType();
                    if (propertyEffectiveType instanceof BmmDefinedType) {
                        BmmClass propertyClass = ((BmmDefinedType) propertyEffectiveType).getBaseClass();
                        if (propertyClass instanceof BmmEnumeration) {
                            //enumeration. This should probably an integer.
                            //TODO: check if we should check the actual type as well as the string values of the enumeration?
                            modelTypeName = ((BmmEnumeration<?>) propertyClass).getUnderlyingTypeName();
                            if (!modelTypeName.equalsIgnoreCase(cRmTypeName)) {
                                return false;//TODO: this should be a different error code/message
                            } else if (cObject instanceof CString && propertyClass instanceof BmmEnumerationString) {
                                BmmEnumerationString enumerationString = (BmmEnumerationString) propertyClass;
                                CString cString = (CString) cObject;
                                if (!enumerationString.getItemValues().containsAll(cString.getConstraint())) {
                                    return false;
                                }
                            } else if (cObject instanceof CInteger && propertyClass instanceof BmmEnumerationInteger) {
                                BmmEnumerationInteger enumerationInteger = (BmmEnumerationInteger) propertyClass;
                                CInteger cInteger = (CInteger) cObject;
                                //TODO: BMM uses Integers instead of long, that could be aproblem as it can be Integer64 in models!
                                if (!cInteger.getConstraintValues().stream().allMatch(item -> enumerationInteger.getItemValues().contains(item.intValue()))) {
                                    return false;
                                }
                            } else {
                                //this isn't right, unless we have some very fancy type substition going on.
                                //TODO: add an error message, not just a boolean
                                return false;
                            }
                        }
                    }
                }
            }
            if (modelTypeName.equalsIgnoreCase(cRmTypeName)) {
                return true;//done :)
            }

            String equivalentType = aomProfile.getRmPrimitiveTypeEquivalences().get(modelTypeName);
            if (equivalentType != null && equivalentType.equalsIgnoreCase(cRmTypeName)) {
                return true;
            }
            String substitutedType = aomProfile.getAomRmTypeSubstitutions().get(cRmTypeName.toUpperCase());
            return substitutedType != null && substitutedType.equalsIgnoreCase(modelTypeName);
        }
    }

    @Override
    public boolean isOrdered(String typeName, String attributeName) {
        if (bmmModel != null) {
            BmmClass classDefinition = bmmModel.getClassDefinition(typeName);
            if (classDefinition != null) {
                //TODO: don't flatten on request, create a flattened properties cache just like the eiffel code for much better performance
                BmmProperty<?> bmmProperty = classDefinition.getFlatProperties().get(attributeName);
                return isOrdered(bmmProperty);
            }
        } else {
            RMAttributeInfo attributeInfo = modelInfoLookup.getAttributeInfo(typeName, attributeName);
            return attributeInfo != null && List.class.isAssignableFrom(attributeInfo.getType());
        }
        return true; //most collections will be ordered, so safe default
    }

    private boolean isOrdered(BmmProperty<?> bmmProperty) {
        if (bmmProperty == null) {
            return false;
        } else if (bmmProperty instanceof BmmContainerProperty) {
            String baseType = BmmDefinitions.typeNameToClassKey(((BmmContainerProperty) bmmProperty).getType().getContainerType().toString());

            return baseType.equalsIgnoreCase("list") || baseType.equalsIgnoreCase("array");//TODO: check Hash
        } else {
            return false;
        }
    }
}
