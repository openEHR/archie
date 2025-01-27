package com.nedap.archie.creation;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.*;
import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomPropertyMapping;
import com.nedap.archie.aom.profile.AomTypeMapping;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.rminfo.MetaModels;
import org.openehr.bmm.core.*;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.threeten.extra.PeriodDuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * generates an example structure for any model based on an operational template + a BMM model + the AOP profile
 *
 * Output is a Map&lt;String, Object&gt;, where object is again a Map&lt;String, Object&gt;, or a simple type directly serializable
 * using the jackson object mapper. This can be simply serialized to JSON if desired.
 *
 *
 * This contains a tiny bit of OpenEHR RM specific code, that is to be converted to subclasses for the different RMs
 * BMM + AOP simply does nto contain enough information for this to be truly RM independent
 */
public  class ExampleJsonInstanceGenerator {

    public static final String MISSING_TERM_IN_ARCHETYPE_FOR_LANGUAGE = "missing term in archetype for language ";
    private final String language;
    private final MetaModels models;
    private OperationalTemplate archetype;
    private String rmRelease;
    private BmmModel bmm;
    private AomProfile aomProfile;

    private boolean useTypeNameWhenTermMissing = false;
    private boolean addUniqueNamesForSiblingNodes = false;

    private String typePropertyName = "_type";

    OpenEhrRmInstanceGenerator openEhrRmInstanceGenerator;

    public ExampleJsonInstanceGenerator(MetaModels models, String language) {
        this.language = language;
        this.models = models;
        openEhrRmInstanceGenerator = new OpenEhrRmInstanceGenerator(this, typePropertyName);
    }

    public Map<String, Object> generate(OperationalTemplate archetype) {
        this.archetype = archetype;
        rmRelease = archetype.getRmRelease();
        //rm release 1.0.4 and 1.1.0 supported. if other versions, switch to 1.1.0 automatically to support other archetypes
        if(rmRelease == null ||
                !(rmRelease.equalsIgnoreCase("1.0.4") || rmRelease.equalsIgnoreCase("1.1.0"))) {
            rmRelease = "1.1.0";
        }
        models.selectModel(archetype, rmRelease);
        aomProfile = models.getSelectedAomProfile();
        bmm = models.getSelectedBmmModel();
        return generate(archetype.getDefinition());
    }


    public boolean isUseTypeNameWhenTermMissing() {
        return useTypeNameWhenTermMissing;
    }

    /**
     * Set whether to add a human readable 'name is missing' message in english for a missing type name, or
     * to use the RM type name as name
     * @param useTypeNameWhenTermMissing
     */
    public void setUseTypeNameWhenTermMissing(boolean useTypeNameWhenTermMissing) {
        this.useTypeNameWhenTermMissing = useTypeNameWhenTermMissing;
    }


    /**
     * The the property name for the type identifier. Defaults to "@type", can be set to "_type" for standard behaviour
     * @param typePropertyName the name of the type property
     */
    public void setTypePropertyName(String typePropertyName) {
        this.typePropertyName = typePropertyName;
        this.openEhrRmInstanceGenerator.setTypePropertyName(typePropertyName);
    }


    public boolean isAddUniqueNamesForSiblingNodes() {
        return addUniqueNamesForSiblingNodes;
    }

    /**
     * Set to false to just include the terms from the archetype as names
     * Set to true to append a numeric suffix to these terms in case two sibling nodes will have the same name with a different archetype node id
     *
     * @param addUniqueNamesForSiblingNodes whether to ensure name uniqueness or not
     */
    public void setAddUniqueNamesForSiblingNodes(boolean addUniqueNamesForSiblingNodes) {
        this.addUniqueNamesForSiblingNodes = addUniqueNamesForSiblingNodes;
    }

    private Map<String, Object> generate(CComplexObject cObject) {
        String type = getConcreteTypeName(cObject.getRmTypeName());
        Map<String, Object> result = openEhrRmInstanceGenerator.generateCustomExampleType(type);
        if(result == null) {
            result = new LinkedHashMap<>();
            result.put(typePropertyName, type);
        }

        BmmClass classDefinition = bmm.getClassDefinition(cObject.getRmTypeName());

        openEhrRmInstanceGenerator.addAdditionalPropertiesAtBegin(classDefinition, result, cObject);


        for (CAttribute attribute : cObject.getAttributes()) {
            BmmProperty<?> property = bmm.propertyAtPath (cObject.getRmTypeName(), attribute.getRmAttributeName());
            if(property == null || property.getComputed()) {
                continue;//do not serialize non-bmm properties such as functions and computed properties
            }
            List<Object> children = new ArrayList<>();

            for (CObject child : attribute.getChildren()) {
                MultiplicityInterval multiplicityInterval = child.effectiveOccurrences(models.getSelectedModel()::referenceModelPropMultiplicity);
                int occurrences = Math.max(1, multiplicityInterval.getLower());
                if(multiplicityInterval.isProhibited()) {
                    occurrences = 0;
                } else if(multiplicityInterval.has(2) && occurrences <= 1) {
                    if(attribute.getCardinality() == null || attribute.getCardinality().getInterval().isUpperUnbounded()) {
                        occurrences = 2 ; //indicate that multiple of these can be added by adding 2 of them if the cardinality is x..*
                    }
                }

                for(int i = 0; i < occurrences; i++){
                    if (child instanceof CComplexObject) {
                        Map<String, Object> next = generate((CComplexObject) child);
                        children.add(next);
                    } else if (child instanceof CPrimitiveObject) {

                        Object toAdd = generateCPrimitive((CPrimitiveObject<?, ?>) child);
                        if(toAdd instanceof Map) {
                            //primitive object, but actual object in JSON
                            //TODO: how to do properties at begin? Just add as empty map, then add fields here then at end?
                            //perhaps not required?
                            Map<String, Object> toAddMap = (Map<String, Object>) toAdd;
                            String childTypeName = toAddMap.containsKey(typePropertyName) ? (String) toAddMap.get(typePropertyName) : child.getRmTypeName();
                            BmmClass childClassDefinition = bmm.getClassDefinition(childTypeName);
                            openEhrRmInstanceGenerator.addAdditionalPropertiesAtEnd(childClassDefinition, toAddMap, child);
                        }
                        children.add(toAdd);

                    } else if (child instanceof ArchetypeSlot) {
                        //TODO: it would be better to actually include an archetype
                        //however that leads to some tricky situations when this archetype again optionally includes
                        //the same archetype - you end with with an infinite loop in that case, for example see
                        //the CKM use of the device archetype, that includes another device...
                        Map<String, Object> next = new LinkedHashMap<>();

                        String concreteTypeName = getConcreteTypeName(child.getRmTypeName());
                        BmmClass childClassDefinition = bmm.getClassDefinition(concreteTypeName);
                        next.put(typePropertyName, concreteTypeName);
                        openEhrRmInstanceGenerator.addAdditionalPropertiesAtBegin(childClassDefinition, next, child);
                        addRequiredPropertiesFromBmm(next, childClassDefinition);
                        openEhrRmInstanceGenerator.addAdditionalPropertiesAtEnd(childClassDefinition, next, child);
                        children.add(next);
                    } else {
                        children.add("unsupported constraint: " + child.getClass().getSimpleName());
                    }
                }
            }

            if (property instanceof BmmContainerProperty) {
                ensureNoDuplicateName(children);
                result.put(attribute.getRmAttributeName(), children);
            } else if (!children.isEmpty()) {
                result.put(attribute.getRmAttributeName(), children.get(0));
            }
        }

        addRequiredPropertiesFromBmm(result, classDefinition);

        openEhrRmInstanceGenerator.addAdditionalPropertiesAtEnd(classDefinition, result, cObject);
        return result;

    }

    private void ensureNoDuplicateName(List<Object> children) {
        if(!this.addUniqueNamesForSiblingNodes) {
            return;
        }
        Map<String, String> nameToNodeIdMap = new LinkedHashMap<>();
        for(Object child:children) {
            if(child instanceof Map) {
                Map json = (Map) child;
                String archetypeNodeId = (String) json.get("archetype_node_id");
                Object name = json.get("name");
                if(archetypeNodeId != null && name instanceof Map) {
                    Map nameMap = (Map) name;
                    String nameValue = (String) nameMap.get("value");
                    if(nameValue != null) {
                        String existingNodeId = nameToNodeIdMap.get(nameValue);
                        if(existingNodeId != null && !existingNodeId.equalsIgnoreCase(archetypeNodeId)) { //if the node ids are the same, it's just a second instance, that's fine.
                            //we now have two nodes with the same name, both with a different node id. That can be a problem in some cases

                            int index = 1;
                            String newName = nameValue + " " + index;
                            while(nameToNodeIdMap.containsKey(newName)) {
                                index++;
                                newName = nameValue + " " + index;
                            }
                            nameMap.put("value", newName);
                            nameToNodeIdMap.put(newName, archetypeNodeId);
                        } else {
                            nameToNodeIdMap.put(nameValue, archetypeNodeId);
                        }
                    }
                }
            }
        }
    }

    protected String getConcreteTypeName(String rmTypeName) {
        BmmClass classDefinition = bmm.getClassDefinition(rmTypeName);
        if(classDefinition.isAbstract()) {
            String customConcreteType = openEhrRmInstanceGenerator.getConcreteTypeOverride(rmTypeName);
            if(customConcreteType != null) {
                return customConcreteType;
            }
            List<String> allDescendants = classDefinition.findAllDescendants();
            for(String descendant: allDescendants) {
                BmmClass descendantClassDefinition = bmm.getClassDefinition(descendant);
                if(!descendantClassDefinition.isAbstract()) {
                    //TODO: should we return generics here? for now left out
                    return BmmDefinitions.typeNameToClassKey(descendantClassDefinition.getType().getTypeName());
                }

            }
        }
        //not abstract or cannot find a non-abstract subclass. Return the original parameters
        //throw away any potential generics information, not useful in JSON
        return rmTypeName.replaceAll("\\<.*\\>$", "");
    }

    private void addRequiredPropertiesFromBmm(Map<String, Object> result, BmmClass classDefinition) {
        Map<String, BmmProperty<?>> properties = classDefinition.getFlatProperties();
        //add all mandatory properties from the RM
        for (BmmProperty<?> property : properties.values()) {
            if (property.getMandatory() && !result.containsKey(property.getName())) {
                Map<String, Object> potentialCodePhrase = openEhrRmInstanceGenerator.getOpenEHRCodedTextOrCodePhrase(classDefinition.getType().typeBaseName(), property.getName());
                if(potentialCodePhrase != null) {
                    result.put(property.getName(), potentialCodePhrase);
                } else if(property.getName().equalsIgnoreCase("archetype_node_id")) {
                    addRequiredProperty(result, property, "idX");
                } else {
                    addRequiredProperty(result, property);
                }
            }
        }
    }

    private void addRequiredProperty(Map<String, Object> result, BmmProperty<?> property) {
        addRequiredProperty(result, property, null);
    }

    private void addRequiredProperty(Map<String, Object> result, BmmProperty<?> property, String value) {
        BmmType type = property.getType();
        if (value != null) {
            result.put(property.getName(), value);
        } else if (property instanceof BmmContainerProperty) {
            List<Object> children = new ArrayList<>();
            MultiplicityInterval cardinality = ((BmmContainerProperty) property).getCardinality();
            if (cardinality.isMandatory()) {
                //if mandatory attribute, create at least one child type
                //this won't be from an actual archetype, but at least it is valid RM data
                String actualType = ((BmmContainerType) type).getBaseType().getTypeName();
                children.add(createExampleFromTypeName(actualType));
            }
            result.put(property.getName(), children);
        } else if (type instanceof BmmParameterType) {
            result.put(property.getName(), createExampleFromTypeName(type.getEffectiveType().getTypeName()));
        } else if (type instanceof BmmDefinedType) {
            BmmClass propertyClass = ((BmmDefinedType)type).getBaseClass();
            if (propertyClass instanceof BmmEnumerationInteger) {
                result.put(property.getName(), 0);
            } else if (propertyClass instanceof BmmEnumerationString) {
                result.put(property.getName(), "string");
            } else {
                result.put(property.getName(), createExampleFromTypeName(type.getTypeName()));
            }
        } else {
            // TODO: if we reach here, we are on a property of a built-in type, either a Tuple
            // (for which we can build an example instance) or an agent, for which we cannot.
        }
    }

    private Object createExampleFromTypeName(String typeName) {
        String actualType = getConcreteTypeName(typeName);
        BmmClass classDefinition1 = bmm.getClassDefinition(actualType);
        if(classDefinition1 != null && classDefinition1.isPrimitiveType()) {
            if (aomProfile.getRmPrimitiveTypeEquivalences().get(actualType) != null) {
                actualType = aomProfile.getRmPrimitiveTypeEquivalences().get(actualType);
            }
            return generatePrimitiveTypeExample(actualType);
        } else {
            return constructExampleType(actualType);
        }
    }

    protected Map<String, Object> constructExampleType(String actualType) {
        Map<String, Object> custom = openEhrRmInstanceGenerator.generateCustomExampleType(actualType);
        if(custom != null) {
            return custom;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        String className = getConcreteTypeName(actualType);
        BmmClass classDefinition = bmm.getClassDefinition(actualType);
        result.put(typePropertyName, className);
        if(classDefinition != null) {
            openEhrRmInstanceGenerator.addAdditionalPropertiesAtBegin(classDefinition, result, null);
            addRequiredPropertiesFromBmm(result, classDefinition);
            openEhrRmInstanceGenerator.addAdditionalPropertiesAtEnd(classDefinition, result, null);
        }
        return result;
    }

    private Object generatePrimitiveTypeExample(String typeName) {
        switch(typeName.toLowerCase()) {
            case "string":
                return "string";
            case "real":
                return 42.0d;
            case "integer":
                return 42;
            case "date":
                return "2018-01-01";
            case "date_time":
                return "2018-01-01T12:00:00+00:00";
            case "time":
                return "12:00:00";
            case "duration":
                return "PT10M";
            case "boolean":
                return true;
        }
        return "unknown primitive type " + typeName;
    }

    private Object generateCPrimitive(CPrimitiveObject<?, ?> child) {
        //optionally create a custom mapping for the current RM. useful to map to strange objects
        //such as mapping a CTerminologyCode to a DV_CODED_TEXT in OpenEHR ERM
        Object customMapping = openEhrRmInstanceGenerator.generateCustomMapping(child);
        if(customMapping != null) {
            return customMapping;
        }
        if(child instanceof CString) {
            CString string = (CString) child;
            if (string.getConstraint() != null && !string.getConstraint().isEmpty()) {
                return string.getConstraint().get(0);
            } else {
                return "string";
            }
        } else if (child instanceof CBoolean) {
            CBoolean bool = (CBoolean) child;
            if (bool.getConstraint() != null && !bool.getConstraint().isEmpty()) {
                return bool.getConstraint().get(0);
            } else {
                return true;
            }
        } else if (child instanceof CInteger) {
            return getValueForCOrdered((CInteger) child, 42L, v -> v + 1, v -> v - 1);
        } else if (child instanceof CReal) {
            return getValueForCOrdered((CReal) child, 42.0d, v -> v + 1.0, v -> v - 1.0);
        } else if (child instanceof CTerminologyCode) {
            return generateTerminologyCode( (CTerminologyCode) child);
        } else if (child instanceof CDuration) {
            return getValueForCOrdered((CDuration) child, Duration.ofMinutes(12),
                    v -> PeriodDuration.from(v).plus(PeriodDuration.of(Duration.ofSeconds(1))),
                    v -> PeriodDuration.from(v).minus(PeriodDuration.of(Duration.ofSeconds(1)))).toString();
        } else if (child instanceof CDate) {
            return "2018-01-01";
        } else if (child instanceof CTime) {
            return "12:00:00";
        } else if (child instanceof CDateTime) {
            return "2018-01-01T12:00:00+0000";
        } else {
            return "TODO: unsupported primitive object constraint " + child.getClass();
        }
    }

    private <T> T getValueForCOrdered(COrdered<T> cOrdered, T defaultValue, Function<T, T> plusFunction, Function<T, T> minusFunction) {
        if (cOrdered.getConstraint() != null && !cOrdered.getConstraint().isEmpty()) {
            Interval<T> interval = cOrdered.getConstraint().get(0);
            if(interval.isUpperUnbounded() && interval.isLowerUnbounded()) {
                return defaultValue;
            } else if(interval.isUpperUnbounded()) {
                return plusFunction.apply(interval.getLower());
            } else if (interval.isLowerUnbounded()) {
                return minusFunction.apply(interval.getUpper());
            } else {
                if(interval.isLowerIncluded()) {
                    return interval.getLower();
                } else {
                    return plusFunction.apply(interval.getLower());
                }
            }
        } else {
            return defaultValue;
        }
    }

    protected Object generateTerminologyCode(CTerminologyCode child) {

        if(aomProfile == null) {
            return "cannot convert CTerminologyCode without AOM profile";
        }
        AomTypeMapping termCodeMapping = aomProfile.getAomRmTypeMappings().get("TERMINOLOGY_CODE");
        if(termCodeMapping == null) {
            return "cannot convert a CTerminology code without an AOM profile containing at least a mapping of Terminology code";
        } else {
            Map<String, Object> result = new LinkedHashMap<>();
            String type = termCodeMapping.getTargetClassName();
            result.put(typePropertyName, type);
            AomPropertyMapping terminologyIdMapping = termCodeMapping.getPropertyMappings().get("terminology_id");
            AomPropertyMapping codeStringMapping = termCodeMapping.getPropertyMappings().get("code_string");
            String codeString = "term code";
            Map<String, Object> terminologyId = new LinkedHashMap<>();
            terminologyId.put(typePropertyName, "TERMINOLOGY_ID");
            terminologyId.put("value", "local");
            String termString = "term";
            ArchetypeTerminology terminology = archetype.getTerminology(child);
            if(child.getConstraint().isEmpty()) {
                codeString = "term code";
                CAttribute attribute = child.getParent();
                CComplexObject parent = (CComplexObject) attribute.getParent();
                Map<String, Object> potentialResult = openEhrRmInstanceGenerator.getOpenEHRCodedTextOrCodePhrase(parent, attribute);
                if(potentialResult != null) {
                    return potentialResult;
                }
            } else {
                String constraint = child.getConstraint().get(0);
                if(constraint.startsWith("ac")) {

                    ValueSet valueSet = terminology.getValueSets().get(constraint);
                    if(valueSet == null) {
                        valueSet = archetype.getTerminology().getValueSets().get(constraint);
                    }
                    if(valueSet == null || valueSet.getMembers().isEmpty()) {
                    } else {
                        codeString = valueSet.getMembers().iterator().next();

                        ArchetypeTerm term = archetype.getTerm(child, codeString, language);
                        if(term != null) {
                            termString = term.getText();
                        }
                    }

                } else if (constraint.startsWith("at")) {
                    codeString = constraint;
                    ArchetypeTerm term = archetype.getTerm(child, constraint, language);
                    if(term != null) {
                        termString = term.getText();
                    }
                } else {
                    codeString = "unknown term code mapping" + constraint;
                }
            }

            if(terminologyIdMapping != null) {
                result.put(terminologyIdMapping.getTargetPropertyName(), terminologyId);
            }
            if(codeStringMapping == null) {
                //erm, right
                return "cannot convert a CTerminology code without an AOM profile containing at least a mapping of Terminology code";
            } else {
                String targetPropertyName = codeStringMapping.getTargetPropertyName();

                result.put(targetPropertyName, codeString);
            }
            return result;
        }
    }

    protected String getMissingTermText(CObject cObject) {
        if(this.useTypeNameWhenTermMissing && !(cObject instanceof CPrimitiveObject)) {
            return cObject.getRmTypeName();
        }
        return MISSING_TERM_IN_ARCHETYPE_FOR_LANGUAGE + language;
    }

    protected BmmModel getBmm() {
        return bmm;
    }

    protected String getLanguage() {
        return language;
    }

    protected OperationalTemplate getArchetype() {
        return archetype;
    }

    protected String getRmRelease() {
        return rmRelease;
    }
}
