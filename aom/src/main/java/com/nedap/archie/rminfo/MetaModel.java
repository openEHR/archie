package com.nedap.archie.rminfo;

import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomTypeMapping;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.query.APathQuery;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmContainerProperty;
import org.openehr.bmm.core.BmmEnumeration;
import org.openehr.bmm.core.BmmEnumerationInteger;
import org.openehr.bmm.core.BmmEnumerationString;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.util.List;
import java.util.stream.Collectors;

public class MetaModel implements MetaModelInterface {

    private ModelInfoLookup selectedModel;
    private BmmModel selectedBmmModel;
    private AomProfile selectedAomProfile;

    public MetaModel(ModelInfoLookup selectedModel, BmmModel selectedBmmModel) {
        this(selectedModel, selectedBmmModel, null);
    }

    public MetaModel(ModelInfoLookup selectedModel, BmmModel selectedBmmModel, AomProfile selectedAomProfile) {
        this.selectedModel = selectedModel;
        this.selectedBmmModel = selectedBmmModel;
        this.selectedAomProfile = selectedAomProfile;
    }

    public ModelInfoLookup getSelectedModel() {
        return selectedModel;
    }

    public BmmModel getSelectedBmmModel() {
        return selectedBmmModel;
    }

    public AomProfile getSelectedAomProfile() {
        return selectedAomProfile;
    }

    @Override
    public boolean isMultiple(String typeName, String attributeName) {
        if(getSelectedBmmModel() != null) {
            BmmClass classDefinition = getSelectedBmmModel().getClassDefinition(BmmDefinitions.typeNameToClassKey(typeName));
            if (classDefinition != null) {
                //TODO: don't flatten on request, create a flattened properties cache just like the eiffel code for much better performance
                BmmClass flatClassDefinition = classDefinition.flattenBmmClass();
                BmmProperty bmmProperty = flatClassDefinition.getProperties().get(attributeName);
                return isMultiple(bmmProperty);
            }
        } else {
            RMAttributeInfo attributeInfo = selectedModel.getAttributeInfo(typeName, attributeName);
            return attributeInfo != null && attributeInfo.isMultipleValued();
        }
        return false;
    }

    private boolean isMultiple(BmmProperty bmmProperty) {
        if(bmmProperty == null) {
            return false;
        } else if(bmmProperty instanceof BmmContainerProperty) {
            return bmmProperty != null && ((BmmContainerProperty) bmmProperty).getCardinality().has(2);
        } else {
            return false;
        }
    }

    @Override
    public boolean rmTypesConformant(String childTypeName, String parentTypeName) {
        if(getSelectedBmmModel() != null) {
            BmmModel selectedBmmModel = getSelectedBmmModel();
            String parentClassName = BmmDefinitions.typeNameToClassKey(parentTypeName);//generics stripped
            String childClassName = BmmDefinitions.typeNameToClassKey(childTypeName);//generics stripped
            //TODO: generics as well. get the array and check each type?
            BmmClass childClass = selectedBmmModel.getClassDefinition(childClassName);
            if(childClass == null) {
                return true;//will be checked elsewhere
            }
            List<String> allAncestors = childClass.findAllAncestors();
            allAncestors = allAncestors.stream().map((s) -> s.toUpperCase()).collect(Collectors.toList());
            if(!parentClassName.equalsIgnoreCase(childClassName) && !allAncestors.contains(parentClassName)) {
                return false;
            }
            return true;
        } else {
            return selectedModel.rmTypesConformant(childTypeName, parentTypeName);
        }
    }

    @Override
    public boolean typeNameExists(String typeName) {
        if(getSelectedBmmModel() != null) {
            return selectedBmmModel.getClassDefinition(BmmDefinitions.typeNameToClassKey(typeName)) != null;
        } else {
            return selectedModel.getTypeInfo(typeName) != null;
        }
    }

    @Override
    public boolean attributeExists(String rmTypeName, String propertyName) {
        if(selectedBmmModel != null) {
            String className = BmmDefinitions.typeNameToClassKey(rmTypeName);
            BmmClass classDefinition = selectedBmmModel.getClassDefinition(className);
            if(classDefinition == null) {
                return false;
            }
            return classDefinition.flattenBmmClass().hasPropertyWithName(propertyName);
        } else {
            return selectedModel.getAttributeInfo(rmTypeName, propertyName) != null;
        }
    }

    @Override
    public boolean isNullable(String typeId, String attributeName) {
        if(selectedBmmModel != null) {
            String className = BmmDefinitions.typeNameToClassKey(typeId);
            BmmClass classDefinition = selectedBmmModel.getClassDefinition(className);
            if(classDefinition == null) {
                return false;
            }
            if(!classDefinition.hasPropertyWithName(attributeName)) {
                return false;
            }
            BmmClass bmmClass = classDefinition.flattenBmmClass();
            BmmProperty bmmProperty = bmmClass.getProperties().get(attributeName);
            return !bmmProperty.getMandatory() || (bmmProperty.getExistence() != null && !bmmProperty.getExistence().isMandatory());
        } else {
            return selectedModel.getAttributeInfo(typeId, attributeName).isNullable();
        }
    }

    /**
     * return whether the attribute identified by rmTypeName.rmAttributeName can contain the type childConstraintTypeName
     * @param rmTypeName
     * @param rmAttributeName
     * @param childConstraintTypeName
     * @return
     */
    @Override
    public boolean typeConformant(String rmTypeName, String rmAttributeName, String childConstraintTypeName) {
        if(getSelectedBmmModel() != null) {
            String propertyType = selectedBmmModel.effectivePropertyType(rmTypeName, rmAttributeName);
            BmmClass parentClass = selectedBmmModel.getClassDefinition(BmmDefinitions.typeNameToClassKey(rmTypeName));
            BmmClass childClass = selectedBmmModel.getClassDefinition(BmmDefinitions.typeNameToClassKey(childConstraintTypeName));
            if(childClass != null && parentClass != null) {
                BmmClass flatParentClass = parentClass.flattenBmmClass();
                BmmProperty property = flatParentClass.getProperties().get(rmAttributeName);
                if(property != null) {
                    String propertyConfTypeName = property.getType().getBaseClass().getTypeName();
                   // if(BmmDefinitions.validGenericTypeName(propertyConfTypeName) &&
                   //         !BmmDefinitions.validGenericTypeName(childConstraintTypeName)) {

                    //}
                    return rmTypesConformant(childConstraintTypeName, propertyConfTypeName);
                }


            }
            return false;
        } else {
            RMTypeInfo typeInfo = selectedModel.getTypeInfo(childConstraintTypeName);
            RMAttributeInfo owningAttributeInfo = selectedModel.getAttributeInfo(rmTypeName, rmAttributeName);
            if (owningAttributeInfo != null) {//this case is another validation, see the validate(cattribute) method of this class
                //TODO: make this work with metadata, not directly with classes
                //TODO: generics other than 'typeincollection' might be nice :)
                Class typeInCollection = owningAttributeInfo.getTypeInCollection();
                if (!typeInCollection.isAssignableFrom(typeInfo.getJavaClass())) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public boolean hasReferenceModelPath(String rmTypeName, String path) {
        if (!path.startsWith("/")) {
            return false;
        }
        APathQuery query = new APathQuery(path);

        if(selectedBmmModel != null) {
            BmmClass classDefinition = selectedBmmModel.getClassDefinition(BmmDefinitions.typeNameToClassKey(rmTypeName));
            for (PathSegment segment : query.getPathSegments()) {
                if (classDefinition == null) {
                    return false;
                }
                BmmProperty bmmProperty = classDefinition.flattenBmmClass().getProperties().get(segment.getNodeName());
                if(bmmProperty == null) {
                    return false;
                }
                classDefinition = bmmProperty.getType().getBaseClass();
            }
            return true;
        } else {


            RMTypeInfo typeInfo = selectedModel.getTypeInfo(rmTypeName);

            for (PathSegment segment : query.getPathSegments()) {
                if (typeInfo == null) {
                    return false;
                }
                RMAttributeInfo attribute = typeInfo.getAttribute(segment.getNodeName());
                if (attribute == null) {
                    return false;
                }
                typeInfo = selectedModel.getTypeInfo(attribute.getTypeInCollection());
            }
            return true;
        }
    }

    @Override
    public MultiplicityInterval referenceModelPropMultiplicity(String rmTypeName, String rmAttributeName) {
        if(selectedBmmModel != null) {
            BmmClass classDefinition = selectedBmmModel.getClassDefinition(BmmDefinitions.typeNameToClassKey(rmTypeName));
            BmmProperty bmmProperty = classDefinition.flattenBmmClass().getProperties().get(rmAttributeName);
            if(isMultiple(bmmProperty)) {
                return MultiplicityInterval.createUpperUnbounded(0);
            } else {
                if(!bmmProperty.getMandatory()) {
                    return MultiplicityInterval.createBounded(0, 1);
                } else {
                    return MultiplicityInterval.createBounded(1, 1);
                }
            }
        } else {
            RMAttributeInfo attributeInfo = selectedModel.getAttributeInfo(rmTypeName, rmAttributeName);
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
    public boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject cObject) {
        if(selectedAomProfile == null) {
            return selectedModel.validatePrimitiveType(rmTypeName, rmAttributeName, cObject);
        } else {
            String cRmTypeName = cObject.getRmTypeName();
            AomTypeMapping aomTypeMapping = selectedAomProfile.getAomRmTypeMappings().get(cRmTypeName.toUpperCase());
            if(aomTypeMapping != null) {
                //found a type mapping, replace effective type name
                cRmTypeName = aomTypeMapping.getTargetClassName();
            }
            String modelTypeName = selectedBmmModel.effectivePropertyType(rmTypeName, rmAttributeName);
            BmmClass bmmClass = selectedBmmModel.getClassDefinition(BmmDefinitions.typeNameToClassKey(rmTypeName));
            if(bmmClass != null) {
                BmmProperty bmmProperty = bmmClass.flattenBmmClass().getProperties().get(rmAttributeName);
                if(bmmProperty != null) {
                    //check enumerated properties
                    BmmClass propertyClass = bmmProperty.getType().getBaseClass();
                    if(propertyClass instanceof BmmEnumeration) {
                        //enumeration. This should probably an integer.
                        //TODO: check if we should check the actual type as well as the string values of the enumeration?
                        modelTypeName = ((BmmEnumeration) propertyClass).getUnderlyingTypeName();
                        if(!modelTypeName.equalsIgnoreCase(cRmTypeName)) {
                            return false;//TODO: this should be a different error code/message
                        }
                        else if(cObject instanceof CString && propertyClass instanceof BmmEnumerationString) {
                            BmmEnumerationString enumerationString = (BmmEnumerationString) propertyClass;
                            CString cString = (CString) cObject;
                            if(!cString.getConstraint().stream().allMatch(item -> enumerationString.getItemValues().contains(item))) {
                                return false;
                            }
                        } else if (cObject instanceof CInteger && propertyClass instanceof BmmEnumerationInteger) {
                            BmmEnumerationInteger enumerationInteger = (BmmEnumerationInteger) propertyClass;
                            CInteger cInteger = (CInteger) cObject;
                            //TODO: BMM uses Integers instead of long, that could be aproblem as it can be Integer64 in models!
                            if(!cInteger.getConstraintValues().stream().allMatch(item -> enumerationInteger.getItemValues().contains(item.intValue()))) {
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
            if(modelTypeName.equalsIgnoreCase(cRmTypeName)) {
                return true;//done :)
            }

            String equivalentType = selectedAomProfile.getRmPrimitiveTypeEquivalences().get(modelTypeName);
            if(equivalentType != null && equivalentType.equalsIgnoreCase(cRmTypeName)) {
                return true;
            }
            String substitutedType = selectedAomProfile.getAomRmTypeSubstitutions().get(cRmTypeName.toUpperCase());
            if(substitutedType != null && substitutedType.equalsIgnoreCase(modelTypeName)) {
                return true;
            }

            return false;
        }
    }
}
