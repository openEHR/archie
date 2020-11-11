package com.nedap.archie.creation;

import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.utils.AOMUtils;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmProperty;

import java.net.URI;
import java.util.LinkedHashMap;
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
    public Map<String, Object> getOpenEHRCodedText(CComplexObject parent, CAttribute attribute) {
        return exampleCodePhrases.getOpenEHRCodedText(parent, attribute);
    }

    /**
     * Generate a custom JSON mapping if required by the given CPrimitiveObject at the given place in the tree.
     * @param child
     * @return the custom JSON mapping, or null if no custom mapping is required
     */
    public Object generateCustomMapping(CPrimitiveObject child) {
        if(child instanceof CTerminologyCode) {
            CTerminologyCode cTermCode = (CTerminologyCode) child;

            CAttribute parentAttribute = child.getParent();
            CComplexObject parentObject = (CComplexObject) parentAttribute.getParent();
            BmmClass classDefinition = generator.getBmm().getClassDefinition(parentObject.getRmTypeName());
            if(classDefinition == null) {
                return null;
            }
            BmmProperty property = classDefinition.getFlatProperties().get(parentAttribute.getRmAttributeName());
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
        String rmTypeName = classDefinition.getType().getTypeName();
        if(rmTypeName.equalsIgnoreCase("DV_CODED_TEXT")) {
            fixCodedText(result, cObject);
        } else if(rmTypeName.equalsIgnoreCase("CODE_PHRASE")) {
            fixCodePhrase(result, cObject);
        } else if(rmTypeName.equalsIgnoreCase("ELEMENT")) {
            Object value = result.get("value");
            Object nullFlavour = result.get("null_flavour");
            if(value == null && nullFlavour == null) {
                Map<String, Object> dvText = new LinkedHashMap<>();
                dvText.put(typePropertyName, "DV_TEXT");
                dvText.put("value", "string");

                result.put("value", dvText);
            }
        } else if (rmTypeName.equalsIgnoreCase("DV_MULTIMEDIA")) {
            Object data = result.get("data");
            if(data == null) {
                result.put("data", "NDIK");
            }
        }
    }

    private void fixCodePhrase(Map<String, Object> result, CObject cObject) {
        try {
            String codeString = (String) result.get("code_string");//TODO: check terminology code to be local?

            if(AOMUtils.isValueCode(codeString)) {
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
        } catch (Exception e) {
            //if statements would be cleaner, but this should not happen and is a lot less code
            //cannot set this apparently, it will be filled by the BMM required property later
        }
    }

    private void fixCodedText(Map<String, Object> result, CObject cObject) {
        try {
            Map<String, Object> definingCode = (Map<String, Object>) result.get("defining_code");
            String codeString = (String) definingCode.get("code_string");//TODO: check terminology code to be local?
            ArchetypeTerm term = generator.getArchetype().getTerm(cObject, codeString, generator.getLanguage());
            result.put("value", term.getText());
            fixCodePhrase(definingCode, cObject);
        } catch (Exception e) {
            //if statements would be cleaner, but this should not happen and is a lot less code
            //cannot set this apparently, it will be filled by the BMM required property later
        }
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

    public Map<String, Object> getOpenEHRCodePhrase(String typeName, String name) {
        return exampleCodePhrases.getOpenEHRCodePhrase(typeName, name);
    }
}
