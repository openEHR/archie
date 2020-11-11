package com.nedap.archie.creation;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** The OpenEHR Example generator, complete with Hungarian Phrasebook */
public class OpenEHRExampleCodePhrases {

    private final Map<String, Supplier<Map<String, Object>>> exampleObjectsByAttribute = new LinkedHashMap<>();
    private String typeNameProperty;

    public OpenEHRExampleCodePhrases(String typeNameProperty) {
        this.typeNameProperty = typeNameProperty;
        exampleObjectsByAttribute.put("COMPOSITION.category", () -> createCodedText("openehr", "431", "persistent"));
        exampleObjectsByAttribute.put("DV_ORDERED.normal_status", () -> createCodePhrase("openehr", "N"));
        exampleObjectsByAttribute.put("ATTESTATION.reason", () -> createCodedText("openehr", "240", "Signed"));
        exampleObjectsByAttribute.put("AUDIT_DETAILS.change_type", () -> createCodedText("openehr", "249", "creation"));
        exampleObjectsByAttribute.put("ELEMENT.null_flavour", () -> createCodedText("openehr", "253", "unknown"));
        exampleObjectsByAttribute.put("EVENT_CONTEXT.setting", () -> createCodedText("openehr", "228", "primary medical care"));
        exampleObjectsByAttribute.put("INTERVAL_EVENT.math_function", () -> createCodedText("openehr", "146", "mean"));
        exampleObjectsByAttribute.put("ISM_TRANSITION.current_state", () -> createCodedText("openehr", "245", "active"));
        exampleObjectsByAttribute.put("ISM_TRANSITION.transition", () -> createCodedText("openehr", "540", "start"));
        exampleObjectsByAttribute.put("PARTICIPATION.function", () -> createCodedText("openehr", "253", "unknown"));
        exampleObjectsByAttribute.put("PARTICIPATION.mode", () -> createCodedText("openehr", "203", "teleconference"));
        exampleObjectsByAttribute.put("PARTY_RELATED.relationship", () -> createCodedText("openehr", "23", "brother"));
        //term mapping purpose is maybe not so relevant here

        exampleObjectsByAttribute.put("COMPOSITION.territory", () -> createCodePhrase("ISO_3166-1", "NL"));
        exampleObjectsByAttribute.put("COMPOSITION.language", () -> createCodePhrase("ISO_639-1", "en"));
        exampleObjectsByAttribute.put("DV_ENCAPSULATED.language", () -> createCodePhrase("ISO_639-1", "en"));
        exampleObjectsByAttribute.put("DV_ENCAPSULATED.charset", () -> createCodePhrase("IANA_character-sets", "UTF-8"));
        exampleObjectsByAttribute.put("DV_MULTIMEDIA.media_type", () -> createCodePhrase("IANA_media-types", "image/png"));
        exampleObjectsByAttribute.put("DV_MULTIMEDIA.compression_algorithm", () -> createCodePhrase("openehr", "gzip"));
        exampleObjectsByAttribute.put("DV_MULTIMEDIA.integrity_check_algorithm", () -> createCodePhrase("openehr", "SHA-256"));
        exampleObjectsByAttribute.put("DV_TEXT.language", () -> createCodePhrase("ISO_639-1", "en"));
        exampleObjectsByAttribute.put("DV_TEXT.encoding", () -> createCodePhrase("IANA_character-sets", "UTF-8"));
        exampleObjectsByAttribute.put("DV_CODED_TEXT.language", () -> createCodePhrase("ISO_639-1", "en"));
        exampleObjectsByAttribute.put("DV_CODED_TEXT.encoding", () -> createCodePhrase("IANA_character-sets", "UTF-8"));
        //TODO: entry subtypes: OBSERVATION, EVALUATION, INSTRUCTION, ACTION, AMIN_ENTRY. GENERIC_ENTRY is not an entry, but a CONTENT_ITEM
        exampleObjectsByAttribute.put("OBSERVATION.language", () -> createCodePhrase("ISO_639-1", "en"));
        exampleObjectsByAttribute.put("OBSERVATION.encoding", () -> createCodePhrase("IANA_character-sets", "UTF-8"));
        exampleObjectsByAttribute.put("EVALUATION.language", () -> createCodePhrase("ISO_639-1", "en"));
        exampleObjectsByAttribute.put("EVALUATION.encoding", () -> createCodePhrase("IANA_character-sets", "UTF-8"));
        exampleObjectsByAttribute.put("INSTRUCTION.language", () -> createCodePhrase("ISO_639-1", "en"));
        exampleObjectsByAttribute.put("INSTRUCTION.encoding", () -> createCodePhrase("IANA_character-sets", "UTF-8"));
        exampleObjectsByAttribute.put("ACTION.language", () -> createCodePhrase("ISO_639-1", "en"));
        exampleObjectsByAttribute.put("ACTION.encoding", () -> createCodePhrase("IANA_character-sets", "UTF-8"));
        exampleObjectsByAttribute.put("ADMIN_ENTRY.language", () -> createCodePhrase("ISO_639-1", "en"));
        exampleObjectsByAttribute.put("ADMIN_ENTRY.encoding", () -> createCodePhrase("IANA_character-sets", "UTF-8"));


    }

    public void setTypePropertyName(String typePropertyName) {
        this.typeNameProperty = typePropertyName;
    }

    private Map<String, Object> createCodedText(String terminologyId, String codeString, String name) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(typeNameProperty, "DV_CODED_TEXT");
        result.put("value", name);
        result.put("defining_code", createCodePhrase(terminologyId, codeString));
        return result;
    }

    private Map<String, Object> createCodePhrase(String terminologyId, String codeString) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(typeNameProperty, "CODE_PHRASE");
        result.put("code_string", codeString);
        result.put("terminology_id", createTerminologyId(terminologyId));
        return result;
    }

    private Map<String, Object> createTerminologyId(String terminologyId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(typeNameProperty, "TERMINOLOGY_ID");
        result.put("value", terminologyId);
        return result;
    }

    /**
     * Returns a
     * @param parent
     * @param attribute
     * @return
     */
    public Map<String, Object> getOpenEHRCodedText(CComplexObject parent, CAttribute attribute) {
        return exampleObjectsByAttribute.get(parent.getRmTypeName().toUpperCase() + "." + attribute.getRmAttributeName().toLowerCase()).get();
    }


    /**
     * Returns a
     * @param parent
     * @param attribute
     * @return
     */
    public Map<String, Object> getOpenEHRCodePhrase(CComplexObject parent, CAttribute attribute) {
        return exampleObjectsByAttribute.get(parent.getRmTypeName().toUpperCase() + "." + attribute.getRmAttributeName().toLowerCase()).get();
    }

    public Map<String, Object> getOpenEHRCodePhrase(String typeName, String attributeName) {
        Supplier<Map<String, Object>> mapSupplier = exampleObjectsByAttribute.get(typeName.toUpperCase() + "." + attributeName.toLowerCase());
        if(mapSupplier == null) {
            return null;
        }
        return mapSupplier.get();
    }
}
