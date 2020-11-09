package com.nedap.archie.creation;

import com.nedap.archie.adlparser.modelconstraints.BMMConstraintImposer;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.primitives.CBoolean;
import com.nedap.archie.aom.primitives.CDate;
import com.nedap.archie.aom.primitives.CDateTime;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.primitives.CTime;
import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomPropertyMapping;
import com.nedap.archie.aom.profile.AomTypeMapping;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.rminfo.MetaModels;
import org.openehr.bmm.core.*;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private BmmModel bmm;
    private AomProfile aomProfile;

    private boolean useTypeNameWhenTermMissing = false;
    private boolean addUniqueNamesForSiblingNodes = false;

    private String typePropertyName = "@type";

    private OpenEHRExampleCodePhrases exampleCodePhrases;

    public ExampleJsonInstanceGenerator(MetaModels models, String language) {
        this.language = language;
        this.models = models;
        this.exampleCodePhrases = new OpenEHRExampleCodePhrases(typePropertyName);
    }

    public Map<String, Object> generate(OperationalTemplate archetype) {
        this.archetype = archetype;
        String rmRelease = archetype.getRmRelease();
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
        this.exampleCodePhrases = new OpenEHRExampleCodePhrases(typePropertyName);
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
        Map<String, Object> result = generateCustomExampleType(type);
        if(result == null) {
            result = new LinkedHashMap<>();
            result.put(typePropertyName, type);
        }

        BmmClass classDefinition = bmm.getClassDefinition(cObject.getRmTypeName());

        addAdditionalPropertiesAtBegin(classDefinition, result, cObject);


        for (CAttribute attribute : cObject.getAttributes()) {
            BmmProperty property = bmm.propertyAtPath (cObject.getRmTypeName(), attribute.getRmAttributeName());
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
                        children.add(generateCPrimitive((CPrimitiveObject) child));
                    } else if (child instanceof ArchetypeSlot) {
                        //TODO: it would be better to actually include an archetype
                        //however that leads to some tricky situations when this archetype again optionally includes
                        //the same archetype - you end with with an infinite loop in that case, for example see
                        //the CKM use of the device archetype, that includes another device...
                        Map<String, Object> next = new LinkedHashMap<>();

                        String concreteTypeName = getConcreteTypeName(child.getRmTypeName());
                        BmmClass childClassDefinition = bmm.getClassDefinition(concreteTypeName);
                        next.put(typePropertyName, concreteTypeName);
                        addAdditionalPropertiesAtBegin(classDefinition, next, child);
                        addRequiredPropertiesFromBmm(next, childClassDefinition);
                        addAdditionalPropertiesAtEnd(classDefinition, next, child);
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

        addAdditionalPropertiesAtEnd(classDefinition, result, cObject);
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
            String customConcreteType = getConcreteTypeOverride(rmTypeName);
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
        return rmTypeName;
    }

    private void addRequiredPropertiesFromBmm(Map<String, Object> result, BmmClass classDefinition) {
        Map<String, BmmProperty> properties = classDefinition.getFlatProperties();
        //add all mandatory properties from the RM
        for (BmmProperty property : properties.values()) {
            if (property.getMandatory() && !result.containsKey(property.getName())) {
                Map<String, Object> potentialCodePhrase = exampleCodePhrases.getOpenEHRCodePhrase(classDefinition.getType().getTypeName(), property.getName());
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

    private void addRequiredProperty(Map<String, Object> result, BmmProperty property) {
        addRequiredProperty(result, property, null);
    }

    private void addRequiredProperty(Map<String, Object> result, BmmProperty property, String value) {
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

    private Map<String, Object> constructExampleType(String actualType) {
        Map<String, Object> custom = generateCustomExampleType(actualType);
        if(custom != null) {
            return custom;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        String className = getConcreteTypeName(actualType);
        BmmClass classDefinition = bmm.getClassDefinition(actualType);
        result.put(typePropertyName, className);
        if(classDefinition != null) {
            addRequiredPropertiesFromBmm(result, classDefinition);
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

    private Object generateCPrimitive(CPrimitiveObject child) {
        //optionally create a custom mapping for the current RM. useful to map to strange objects
        //such as mapping a CTerminologyCode to a DV_CODED_TEXT in OpenEHR ERM
        Object customMapping = generateCustomMapping(child);
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
            CInteger integer = (CInteger) child;
            if (integer.getConstraint() != null && !integer.getConstraint().isEmpty()) {
                Interval<Long> longInterval = integer.getConstraint().get(0);
                if(longInterval.isUpperUnbounded() && longInterval.isLowerUnbounded()) {
                    return 42;
                } else if(longInterval.isUpperUnbounded()) {
                    return longInterval.getLower() + 1;
                } else if (longInterval.isLowerUnbounded()) {
                    return longInterval.getUpper() - 1;
                } else {
                    if(longInterval.isLowerIncluded()) {
                        return longInterval.getLower();
                    } else {
                        return longInterval.getUpper() + 1;
                    }
                }
            } else {
                return 42;
            }
        } else if (child instanceof CReal) {
            CReal real = (CReal) child;
            if (real.getConstraint() != null && !real.getConstraint().isEmpty()) {
                Interval<Double> doubleInterval = real.getConstraint().get(0);
                if(doubleInterval.isUpperUnbounded() && doubleInterval.isLowerUnbounded()) {
                    return 42.0d;
                } else if(doubleInterval.isUpperUnbounded()) {
                    return doubleInterval.getLower() + 1.0d;
                } else if (doubleInterval.isLowerUnbounded()) {
                    return doubleInterval.getUpper() - 1.0d;
                } else {
                    if(doubleInterval.isLowerIncluded()) {
                        return doubleInterval.getLower();
                    } else {
                        return doubleInterval.getUpper() + 1.0d;
                    }
                }
            } else {
                return 42.0d;
            }
        } else if (child instanceof CTerminologyCode) {
            return generateTerminologyCode( (CTerminologyCode) child);
        } else if (child instanceof CDuration) {
            return "PT12m";
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



    private Object generateTerminologyCode(CTerminologyCode child) {

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
                Map<String, Object> potentialResult = getOpenEHRCodedText(parent, attribute);
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

    ///// BEGIN OPENEHR RM SPECIFIC CODE TO BE EXTRACTED /////

    private String getConcreteTypeOverride(String rmTypeName) {
        if(rmTypeName.equalsIgnoreCase("ITEM")) {
            return "ELEMENT";
        } else if (rmTypeName.equalsIgnoreCase("EVENT")) {
            return "POINT_EVENT";
        }

        return null;
    }


    /**
     * Returns a DV_CODED_TEXT instance of the given place in the RM requires a custom known code, for example 'UTF-8' in encoding.
     * @param parent
     * @param attribute
     * @return
     */
    private Map<String, Object> getOpenEHRCodedText(CComplexObject parent, CAttribute attribute) {
        return exampleCodePhrases.getOpenEHRCodedText(parent, attribute);
    }

    /**
     * Generate a custom JSON mapping if required by the given CPrimitiveObject at the given place in the tree.
     * @param child
     * @return the custom JSON mapping, or null if no custom mapping is required
     */
    private Object generateCustomMapping(CPrimitiveObject child) {
        if(child instanceof CTerminologyCode) {
            CTerminologyCode cTermCode = (CTerminologyCode) child;

            CAttribute parentAttribute = child.getParent();
            CComplexObject parentObject = (CComplexObject) parentAttribute.getParent();
            BmmClass classDefinition = bmm.getClassDefinition(parentObject.getRmTypeName());
            if(classDefinition == null) {
                return null;
            }
            BmmProperty property = classDefinition.getFlatProperties().get(parentAttribute.getRmAttributeName());
            if(property == null) {
                return null;
            }
            if(property.getType().getTypeName().equalsIgnoreCase("DV_CODED_TEXT")) {
                Object codePhrase = this.generateTerminologyCode(cTermCode);
                Map<String, Object> dvCodedText = constructExampleType("DV_CODED_TEXT");
                dvCodedText.put("defining_code", codePhrase);
                if(codePhrase instanceof Map) {
                    Map<String, Object> definingCode = (Map<String, Object>) codePhrase;
                    String codeString = (String) definingCode.get("code_string");//TODO: check terminology code to be local?
                    ArchetypeTerm term = archetype.getTerm(child, codeString, language);
                    dvCodedText.put("value", term == null ? getMissingTermText(child) : term.getText());
                }

                return dvCodedText;
            } else {
                return null;
            }
        }

        return null;
    }

    private String getMissingTermText(CObject cObject) {
        if(this.useTypeNameWhenTermMissing && !(cObject instanceof CPrimitiveObject)) {
            return cObject.getRmTypeName();
        }
        return MISSING_TERM_IN_ARCHETYPE_FOR_LANGUAGE + language;
    }

    /** Add any properties required for this specific RM based on the CObject. For openEHR RM, this should at least
     * set the name if present
     */
    protected void addAdditionalPropertiesAtBegin(BmmClass classDefinition, Map<String, Object> result, CObject cObject) {

        if (classDefinition.getType().getTypeName().equalsIgnoreCase("LOCATABLE") || classDefinition.findAllAncestors().contains("LOCATABLE")) {

            Map<String, Object> name = new LinkedHashMap<>();
            name.put(typePropertyName, "DV_TEXT");
            ArchetypeTerm term = archetype.getTerm(cObject, language);
            if (term == null) {
                name.put("value", getMissingTermText(cObject));
            } else {
                name.put("value", term.getText());
            }
            result.put("name", name);

            if (!(cObject instanceof CPrimitiveObject)) {
                result.put("archetype_node_id", cObject.getNodeId());
            }
        }

        if(cObject instanceof ArchetypeSlot) {
            result.put("archetype_details", constructArchetypeDetails("openEHR-EHR-" + cObject.getRmTypeName() + ".archetype-slot.v1"));
        } else if (cObject instanceof CArchetypeRoot) {
            result.put("archetype_details", constructArchetypeDetails(((CArchetypeRoot) cObject).getArchetypeRef()));
        } else if(cObject.isRootNode()) {
            result.put("archetype_details", constructArchetypeDetails(cObject.getArchetype().getArchetypeId().getFullId()));
        }
    }

    private Map<String, Object> constructArchetypeDetails(String archetypeIdValue) {
        Map<String, Object> archetypeDetails = new LinkedHashMap<>();
        archetypeDetails.put(typePropertyName, "ARCHETYPED");
        Map<String, Object> archetypeId = new LinkedHashMap<>();
        archetypeId.put(typePropertyName, "ARCHETYPE_ID");
        archetypeId.put("value", archetypeIdValue);
        archetypeDetails.put("archetype_id", archetypeId); //TODO: add template id?
        archetypeDetails.put("rm_version", "1.0.4");
        return archetypeDetails;
    }

    protected void addAdditionalPropertiesAtEnd(BmmClass classDefinition, Map<String, Object> result, CObject cObject) {
        if(classDefinition.getType().getTypeName().equalsIgnoreCase("DV_CODED_TEXT")) {
            try {
                Map<String, Object> definingCode = (Map<String, Object>) result.get("defining_code");
                String codeString = (String) definingCode.get("code_string");//TODO: check terminology code to be local?
                if(AOMUtils.isValueCode(codeString)) {
                    //check for OpenEHR term mapping and use that if available, so we get correct
                    //rm objects
                    //TODO: quite some more term ids, such as IANA characters sets, etc. HOW?
                    URI openehr = archetype.getTerminology(cObject).getTermBinding("openehr", codeString);
                    if(openehr != null && openehr.getPath() != null) {
                        int i = openehr.getPath().lastIndexOf('/');
                        if (i > 0) {
                            codeString = openehr.getPath().substring(i+1);
                            Map<String, Object> terminologyId = (Map<String, Object>) definingCode.get("terminology_id");
                            if(terminologyId != null) {
                                terminologyId.put("value", "openehr");
                            }
                            //TODO: add a mapping to the at code in the data.
                        }
                    }
                }
                ArchetypeTerm term = archetype.getTerm(cObject, codeString, language);
                result.put("value", term.getText());
            } catch (Exception e) {
                //if statements would be cleaner, but this should not happen and is a lot less code
                //cannot set this apparently, it will be filled by the BMM required property later
            }
        }
    }

    private Map<String, Object> generateCustomExampleType(String actualType) {
        if(actualType.equalsIgnoreCase("DV_DATE_TIME")) {
            //In BMM, value is a string, and not a date time, so impossible to map automatically
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            result.put(typePropertyName, "DV_DATE_TIME");
            result.put("value", "2018-01-01T12:00:00+0000");
            return result;
        } else if (actualType.equalsIgnoreCase("DV_DATE")) {
            //In BMM, value is a string, and not a date time, so impossible to map automatically
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            result.put(typePropertyName, "DV_DATE");
            result.put("value", "2018-01-01");
            return result;
        }  else if (actualType.equalsIgnoreCase("DV_TIME")) {
            //In BMM, value is a string, and not a date time, so impossible to map automatically
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            result.put(typePropertyName, "DV_TIME");
            result.put("value", "12:00:00");
            return result;
        }  else if (actualType.equalsIgnoreCase("DV_DURATION")) {
            //In BMM, value is a string, and not a date time, so impossible to map automatically
            LinkedHashMap<String, Object> result = new LinkedHashMap<>();
            result.put(typePropertyName, "DV_DURATION");
            result.put("value", "PT20m");
            return result;
        }
        return null;
    }

    ///// END OPENEHR RM SPECIFIC CODE TO BE EXTRACTED /////
}
