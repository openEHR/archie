package com.nedap.archie.creation;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.utils.AOMUtils;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmProperty;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class OpenEhrRmInstanceGenerator {

    private final OpenEHRExampleCodePhrases exampleCodePhrases;
    private String typePropertyName;
    private final ExampleJsonInstanceGenerator generator;

    public OpenEhrRmInstanceGenerator(ExampleJsonInstanceGenerator generator, String typePropertyName) {
        this.generator = generator;
        this.exampleCodePhrases = new OpenEHRExampleCodePhrases(typePropertyName);
        this.typePropertyName = typePropertyName;
    }

    public void setTypePropertyName(String typePropertyName) {
        this.typePropertyName = typePropertyName;
        exampleCodePhrases.setTypePropertyName(typePropertyName);
    }

    protected String getConcreteTypeOverride(String rmTypeName) {
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
    public Map<String, Object> getOpenEHRCodedTextOrCodePhrase(CComplexObject parent, CAttribute attribute) {
        return exampleCodePhrases.getOpenEHRCodePhraseOrCodedText(parent, attribute);
    }

    /**
     * Generate a custom JSON mapping if required by the given CPrimitiveObject at the given place in the tree.
     * @param child the primitive object to create a custom mapping for
     * @return the custom JSON mapping, or null if no custom mapping is required
     */
    public Object generateCustomMapping(CPrimitiveObject<?, ?> child) {
        if(child instanceof CTerminologyCode) {
            CTerminologyCode cTermCode = (CTerminologyCode) child;

            CAttribute parentAttribute = child.getParent();
            CComplexObject parentObject = (CComplexObject) parentAttribute.getParent();
            BmmClass classDefinition = generator.getBmm().getClassDefinition(parentObject.getRmTypeName());
            if(classDefinition == null) {
                return null;
            }
            BmmProperty<?> property = classDefinition.getFlatProperties().get(parentAttribute.getRmAttributeName());
            if(property == null) {
                return null;
            }
            if(property.getType().getTypeName().equalsIgnoreCase("DV_CODED_TEXT")) {
                Object codePhrase = generator.generateTerminologyCode(cTermCode);
                Map<String, Object> dvCodedText = generator.constructExampleType("DV_CODED_TEXT");
                dvCodedText.put("defining_code", codePhrase);
                if(codePhrase instanceof Map) {
                    Map<String, Object> definingCode = (Map<String, Object>) codePhrase;
                    String codeString = (String) definingCode.get("code_string");//TODO: check terminology code to be local?
                    ArchetypeTerm term = generator.getArchetype().getTerm(child, codeString, generator.getLanguage());
                    dvCodedText.put("value", term == null ? generator.getMissingTermText(child) : term.getText());
                }

                return dvCodedText;
            } else {
                return null;
            }
        }

        return null;
    }

    /** Add any properties required for this specific RM based on the CObject. For openEHR RM, this should at least
     * set the name if present
     */
    public void addAdditionalPropertiesAtBegin(BmmClass classDefinition, Map<String, Object> result, CObject cObject) {

        if (classDefinition.getType().getTypeName().equalsIgnoreCase("LOCATABLE") || classDefinition.findAllAncestors().contains("LOCATABLE")) {

            Map<String, Object> name = new LinkedHashMap<>();
            name.put(typePropertyName, "DV_TEXT");

            if(cObject == null) {
                name.put("value", "example generated name");
            } else {
                ArchetypeTerm term = generator.getArchetype().getTerm(cObject, generator.getLanguage());
                if (term == null) {
                    name.put("value", generator.getMissingTermText(cObject));
                } else {
                    name.put("value", term.getText());
                }
            }
            result.put("name", name);

            if (cObject != null && !(cObject instanceof CPrimitiveObject)) {
                result.put("archetype_node_id", cObject.getNodeId());
            }
        }

        if(cObject != null) {
            if (cObject instanceof ArchetypeSlot) {
                result.put("archetype_details", constructArchetypeDetails("openEHR-EHR-" + cObject.getRmTypeName() + ".archetype-slot.v1"));
            } else if (cObject instanceof CArchetypeRoot) {
                result.put("archetype_details", constructArchetypeDetails(((CArchetypeRoot) cObject).getArchetypeRef()));
            } else if (cObject.isRootNode()) {
                result.put("archetype_details", constructArchetypeDetails(cObject.getArchetype().getArchetypeId().getFullId()));
            }
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

    public void addAdditionalPropertiesAtEnd(BmmClass classDefinition, Map<String, Object> result, CObject cObject) {
        String rmTypeName = classDefinition.getType().getBaseClass().getName();
        switch (rmTypeName.toUpperCase()) {
            case "DV_CODED_TEXT":
                fixCodedText(result, cObject);
                break;
            case "CODE_PHRASE":
                fixCodePhrase(result, cObject);
                break;
            case "HISTORY":
                fixHistory(result);
                break;
            case "ELEMENT":
                fixElement(result);
                break;
            case "DV_MULTIMEDIA":
                fixDvMultimedia(result);
                break;
            case "PARTY_RELATED":
                FixPartyRelated(result);
                break;
            case "DV_PROPORTION":
                fixDvProportion(result);
                break;
            case "DV_URI":
            case "DV_EHR_URI":
                fixDvUri(result);
                break;
            case "PARTY_REF":
                fixPartyRef(result);
                break;
            case "DV_INTERVAL":
                fixDvInterval(result);
                break;
        }
    }

    private void fixDvInterval(Map<String, Object> result) {
        Boolean lowerIncluded = (Boolean) result.get("lower_included");
        Boolean upperIncluded = (Boolean) result.get("upper_included");
        if(result.get("lower") == null) {
            result.put("lower_unbounded", true);
            if(lowerIncluded != null) {
                result.put("lower_included", false);
            }
        } else {
            result.put("lower_unbounded", false);
        }
        if(result.get("upper") == null) {
            result.put("upper_unbounded", true);
            if(upperIncluded != null) {
                result.put("upper_included", false);
            }
        } else {
            result.put("upper_unbounded", false);
        }
    }

    private void fixPartyRef(Map<String, Object> result) {
        String type = (String) result.get("type");
        if(type == null || !(type.equals("PERSON") ||
                type.equals("ORGANISATION") ||
                type.equals("GROUP") ||
                type.equals("AGENT") ||
                type.equals("ROLE") ||
                type.equals("PARTY") ||
                type.equals("ACTOR"))) {
            result.put("type", "PERSON");
        }

    }

    private void fixDvUri(Map<String, Object> result) {
        String value = (String) result.get("value");
        if(value.equalsIgnoreCase("string")) {
            result.put("value", "ehr://something/something");
        }
    }

    private void fixDvProportion(Map<String, Object> result) {
        //this one is tricky. Several cases
        //RATIO(0), UNITARY(1), PERCENT(2), FRACTION(3), INTEGER_FRACTION(4);
        Number proportionKind = (Number) result.get("type");
        Number denominator = (Number) result.get("denominator");
        if(denominator != null && denominator.intValue() == 0) {
            result.put("denominator", 1);
        }
        if(proportionKind == null || proportionKind.intValue() > 4) {
            result.put("type", 2);
            result.put("denominator", 100);
            result.put("numerator", 50);
        } else {
            switch(proportionKind.intValue()) {
                case 0: //ratio
                    //nothing to be done.
                    break;
                case 1: //unitary
                    result.put("denominator", 1);
                    break;
                case 2: //percent
                    result.put("denominator", 100);
                    break;
                case 3: //fraction
                case 4: //integer fraction
                    result.put("precision", 0);
            }
        }
    }

    private void FixPartyRelated(Map<String, Object> result) {
        Object name = result.get("name");
        if(name == null) {
            result.put("name", "John Doe");
        }
    }

    private void fixDvMultimedia(Map<String, Object> result) {
        Object data = result.get("data");
        if(data == null) {
            result.put("data", "NDIK");
        }
    }

    private void fixElement(Map<String, Object> result) {
        Object value = result.get("value");
        Object nullFlavour = result.get("null_flavour");
        if(value == null && nullFlavour == null) {
            Map<String, Object> dvText = new LinkedHashMap<>();
            dvText.put(typePropertyName, "DV_TEXT");
            dvText.put("value", "string");

            result.put("value", dvText);
        }
        if(value != null && nullFlavour != null) {
            result.remove("null_flavour");//can't be set at the same time
        }
    }

    private void fixHistory(Map<String, Object> result) {
        Object events = result.get("events");
        if(events == null || (events instanceof List && ((List) events).isEmpty())) {
            List<Map> newEvents = new ArrayList<>();
            newEvents.add(this.generator.constructExampleType("EVENT"));
            result.put("events", newEvents);
        }
    }

    private void fixCodePhrase(Map<String, Object> result, CObject cObject) {
        String codeString = (String) result.get("code_string");//TODO: check terminology code to be local?

        if(codeString != null && AOMUtils.isValueCode(codeString)) {
            //check for OpenEHR term mapping and use that if available, so we get correct
            //rm objects

            //TODO: quite some more term ids, such as IANA characters sets, etc. HOW?
            URI openehr = generator.getArchetype().getTerminology(cObject).getTermBinding("openehr", codeString);
            if(openehr != null && openehr.getPath() != null) {
                int i = openehr.getPath().lastIndexOf('/');
                if (i > 0) {
                    codeString = openehr.getPath().substring(i+1);
                    Map<String, Object> terminologyId = (Map<String, Object>) result.get("terminology_id");
                    if(terminologyId != null) {
                        terminologyId.put("value", "openehr");
                    }
                    result.put("code_string", codeString);
                    //TODO: add a mapping to the at code in the data.
                }
            }
        }
    }

    private void fixCodedText(Map<String, Object> result, CObject cObject) {
        Map<String, Object> definingCode = (Map<String, Object>) result.get("defining_code");
        String codeString = (String) definingCode.get("code_string");//TODO: check terminology code to be local?
        if(cObject != null) {
            ArchetypeTerm term = generator.getArchetype().getTerm(cObject, codeString, generator.getLanguage());
            if(term != null) {
                result.put("value", term.getText());
            }
        } else {
            //TODO: cObject is required to get a term from an OperationalTemplate. However, all we really need it a childArchetypeId
            //to find the correct component terminology.
            // so, add this as a parameter in which archetype we are now processing, and use that to manually retrieve the correct term
        }
        fixCodePhrase(definingCode, cObject);


    }

    public Map<String, Object> generateCustomExampleType(String actualType) {
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

    public Map<String, Object> getOpenEHRCodedTextOrCodePhrase(String typeName, String name) {
        return exampleCodePhrases.getOpenEHRCodePhraseOrCodedText(typeName, name);
    }
}
