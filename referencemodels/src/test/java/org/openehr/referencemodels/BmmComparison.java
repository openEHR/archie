package org.openehr.referencemodels;

import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import com.nedap.archie.rminfo.RMTypeInfo;
import org.openehr.bmm.core.*;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class to compare a BMM with metadata derived from an implementation, in the form of a ModelInfoLookup
 * Returns a list of differences.
 *
 * Missing primitive types are NOT reported since this is rather implementation specific
 */
public class BmmComparison {


    private final Set<String> extraParamsInModel;
    private final Map<String, String> bmmToImplementationTypeMap;
    private final Map<String, String> typeNamesOverride;

    /**
     * Create a BMM Comparison class.
     * @param extraParamsInModel parameters in the implementation that can be safely ignored if they are not in the BMM.
     *                           in the format className.propertyName, or in the form propertyName if it should be ignored
     *                           for all classes
     * @param bmmToImplementationTypeMap map of BMM types to implementation types, such as Integer64 -> Long
     * @param typeNamesOverride
     */
    public BmmComparison(Set<String> extraParamsInModel, Map<String,
                            String> bmmToImplementationTypeMap,
                            Map<String, String> typeNamesOverride) {

        this.extraParamsInModel = extraParamsInModel.stream().map(s -> s.toLowerCase()).collect(Collectors.toSet());
        this.bmmToImplementationTypeMap = new HashMap<>();
        bmmToImplementationTypeMap.forEach((k, v) -> this.bmmToImplementationTypeMap.put(k.toLowerCase(), v));
        this.typeNamesOverride = new HashMap<>();
        typeNamesOverride.forEach((k, v) -> this.typeNamesOverride.put(k.toLowerCase(), v));
    }

    /**
     * Compare the given BMM Model with the ModelInfoLookup
     * @param model the BMM Model to compare
     * @param lookup the model info lookup
     * @return a list of differences
     */
    public List<ModelDifference> compare(BmmModel model, ModelInfoLookup lookup) {
        List<ModelDifference> result = new ArrayList<>();

        for(RMTypeInfo typeInfo:lookup.getAllTypes()) {
            BmmClass classDefinition = model.getClassDefinition(typeInfo.getRmName());
            if(classDefinition == null) {
                result.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_BMM,
                        MessageFormat.format("ModelInfoLookup class {0} is missing in BMM", typeInfo.getRmName()),
                        typeInfo.getRmName()));
            } else {
                result.addAll(compareClass(typeInfo, classDefinition));
            }
        }
        for(BmmClass bmmClass:model.getClassDefinitions().values()) {
            RMTypeInfo typeInfo = lookup.getTypeInfo(bmmClass.getName());
            if(typeInfo == null && !bmmClass.isPrimitiveType()) {
                result.add(new ModelDifference(ModelDifferenceType.CLASS_MISSING_IN_MODEL,
                        MessageFormat.format("BMM class {0} is missing in ModelInfoLookup", bmmClass.getName()),
                        bmmClass.getName()));
            }
        }
        return result;
    }

    private List<ModelDifference> compareClass(RMTypeInfo typeInfo, BmmClass classDefinition) {
        List<ModelDifference> result = new ArrayList<>();
        for(RMAttributeInfo attributeInfo:typeInfo.getAttributes().values()) {
            if(!isIgnorableModelParam(classDefinition.getName(), attributeInfo.getRmName())) {
                BmmProperty bmmProperty = classDefinition.getFlatProperties().get(attributeInfo.getRmName());
                if (bmmProperty == null)
                    result.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_BMM,
                            MessageFormat.format("class {0}: ModelInfoLookup property {1} is missing in BMM", classDefinition.getType().getTypeName(), attributeInfo.getRmName()),
                            typeInfo.getRmName(),
                            attributeInfo.getRmName()));
                else
                    result.addAll(compareProperty(classDefinition.getName(), attributeInfo, bmmProperty));
            }
        }
        for(BmmProperty property: classDefinition.getFlatProperties().values()) {
            if(!typeInfo.getAttributes().containsKey(property.getName())) {
                String propertyDescription = property.getComputed() ? "computed property" : "property";
                result.add(new ModelDifference(ModelDifferenceType.PROPERTY_MISSING_IN_MODEL,
                        MessageFormat.format("class {1}: BMM {0} {2} is missing in Model", propertyDescription, classDefinition.getType().getTypeName(), property.getName()),
                        typeInfo.getRmName(),
                        property.getName()));
            }
        }


        //ancestor comparison
        for(String ancestor:classDefinition.getAncestors().keySet()) {
            Set<RMTypeInfo> directParentClasses = typeInfo.getDirectParentClasses();
            Set<String> parentTypeNames = directParentClasses.stream().map((type) -> type.getRmName()).collect(Collectors.toSet());

            String ancestorClassName = BmmDefinitions.typeNameToClassKey(ancestor);
            if(!ancestorClassName.equalsIgnoreCase("any") && !parentTypeNames.contains(ancestorClassName)) {
                result.add(new ModelDifference(ModelDifferenceType.ANCESTOR_DIFFERENCE,
                        MessageFormat.format("class {0} has ancestor {1} in BMM, but not in ModelInfoLookup", classDefinition.getType().getTypeName(), ancestorClassName),
                        typeInfo.getRmName()));
            }
        }

        //abstract/concrete class
        if(typeInfo.getJavaClass() != null && Modifier.isAbstract(typeInfo.getJavaClass().getModifiers()) != classDefinition.isAbstract()) {
            result.add(new ModelDifference(ModelDifferenceType.ABSTRACT_DIFFERENCE,
                    MessageFormat.format("class {0} abstract difference: BMM: {1}, Model: {2}", classDefinition.getType().getTypeName(), classDefinition.isAbstract(), Modifier.isAbstract(typeInfo.getJavaClass().getModifiers())),
                    typeInfo.getRmName()));
        }

        return result;
    }

    /**
     * return true if the given property in the given class should be ignored in the implementation, because it is implementation specific
     * @param className name of the RM class
     * @param propertyName name of the RM Property
     * @return true if the given property in the given class should be ignored in the implementation, because it is implementation specific
     */
    private boolean isIgnorableModelParam(String className, String propertyName) {
        return extraParamsInModel.contains(className.toLowerCase() + "." + propertyName.toLowerCase()) ||
                extraParamsInModel.contains(propertyName.toLowerCase());
    }

    private Collection<? extends ModelDifference> compareProperty(String className, RMAttributeInfo attributeInfo, BmmProperty bmmProperty) {
        List<ModelDifference> result = new ArrayList<>();
        String modelInfoTypeName = attributeInfo.getTypeNameInCollection();
        String bmmTypeName = BmmDefinitions.typeNameToClassKey(getBmmTypeName(bmmProperty.getType()));
        if(!(modelInfoTypeName.equalsIgnoreCase(bmmTypeName) ||
                modelInfoTypeName.equalsIgnoreCase(getModelInfoTypeName(className, bmmProperty.getName(), bmmTypeName)))) {
            result.add(new ModelDifference(ModelDifferenceType.TYPE_NAME_DIFFERENCE,
                    MessageFormat.format("type name difference for {0}: BMM: {1}, implementation: {2}", className + "." + bmmProperty.getName(), bmmTypeName, modelInfoTypeName),
                    className, bmmProperty.getName()
                    ));
        }

        if(attributeInfo.isNullable() != !bmmProperty.getMandatory() && !bmmProperty.getComputed()) {
            result.add(new ModelDifference(ModelDifferenceType.EXISTENCE_DIFFERENCE,
                    MessageFormat.format("mandatory difference {2}: BMM: {0}, implementation: {1}", bmmProperty.getMandatory(), !attributeInfo.isNullable(), className + "." + bmmProperty.getName()),
                    className, bmmProperty.getName()));
        }

        if(bmmProperty instanceof BmmContainerProperty) {
            BmmContainerProperty containerProperty = (BmmContainerProperty) bmmProperty;
            if(!attributeInfo.isMultipleValued()) {
                result.add(new ModelDifference(ModelDifferenceType.CARDINALITY_DIFFERENCE,
                        MessageFormat.format("bmm {0} is container property, model is single valued", className + "." + bmmProperty.getName()),
                        className, bmmProperty.getName()));
            }
        } else {
            if(attributeInfo.isMultipleValued()) {
                result.add(new ModelDifference(ModelDifferenceType.CARDINALITY_DIFFERENCE,
                        MessageFormat.format("bmm {0} is single property, model is multiply valued", className + "." + bmmProperty.getName()),
                        className, bmmProperty.getName()));
            }
        }
        return result;
    }

    /**
     * Retruns the model info type name for a BMM type name
     * @param bmmTypeName the BMM type name
     * @return the corresponding model info lookup type name
     */
    private String getModelInfoTypeName(String className, String propertyName, String bmmTypeName) {
        String classKey = className.toLowerCase() + "." + propertyName.toLowerCase();
        if(typeNamesOverride.containsKey(classKey)) {
            return typeNamesOverride.get(classKey);
        }
        return bmmToImplementationTypeMap.get(bmmTypeName.toLowerCase());
    }

    private String getBmmTypeName(BmmType type) {
        if(type instanceof BmmSimpleType) {
            BmmSimpleType simpleType = (BmmSimpleType) type;
            return simpleType.getTypeName();
        } else if (type instanceof BmmContainerType) {
            BmmContainerType containerType = (BmmContainerType) type;
            return getBmmTypeName(containerType.getBaseType());
        } else if (type instanceof BmmGenericType) {
            BmmGenericType genericType = (BmmGenericType) type;
            return genericType.getBaseClass().getType().getTypeName();
        } else if (type instanceof BmmParameterType) {
            BmmParameterType parameterType = (BmmParameterType) type;
            BmmEffectiveType conformsToType = parameterType.getConformsToType();
            if(conformsToType == null) {
                return BmmDefinitions.ANY_TYPE;
            }
            return conformsToType.getTypeName();
        }
        return "UNKNOWN_TYPE";
    }
}
