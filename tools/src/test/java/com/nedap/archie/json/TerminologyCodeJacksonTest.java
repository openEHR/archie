package com.nedap.archie.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.base.terminology.TerminologyCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Regression tests for openEHR/archie#723: the type property of TerminologyCode used to be serialized
 * with a hardcoded "@type" property name and the java simple class name "TerminologyCode", instead of
 * respecting the configured type property name and the openEHR type name "TERMINOLOGY_CODE".
 */
public class TerminologyCodeJacksonTest {

    private TerminologyCode terminologyCode() {
        TerminologyCode result = new TerminologyCode();
        result.setTerminologyId("ISO_639-1");
        result.setCodeString("en");
        return result;
    }

    @Test
    public void standardsCompliantHasNoTypeProperty() throws Exception {
        // TERMINOLOGY_CODE is a leaf type, so a standards compliant serialization does not need a type property at all.
        ObjectMapper mapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        JsonNode node = mapper.readTree(mapper.writeValueAsString(terminologyCode()));
        assertNull(node.get("_type"));
        assertFalse(node.has("@type"));
        assertEquals("ISO_639-1", node.get("terminology_id").textValue());
        assertEquals("en", node.get("code_string").textValue());
    }

    @Test
    public void javascriptConfigUsesStandardsCompliantTypeProperty() throws Exception {
        ObjectMapper mapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createConfigForJavascriptUsage());
        JsonNode node = mapper.readTree(mapper.writeValueAsString(terminologyCode()));
        assertEquals("TERMINOLOGY_CODE", node.get("_type").textValue());
        assertFalse(node.has("@type"));
    }

    @Test
    public void legacyConfigUsesLegacyTypePropertyWithOpenEHRName() throws Exception {
        ObjectMapper mapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createLegacyConfiguration());
        JsonNode node = mapper.readTree(mapper.writeValueAsString(terminologyCode()));
        assertEquals("TERMINOLOGY_CODE", node.get("@type").textValue());
        assertFalse(node.has("_type"));
    }

    @Test
    public void roundTrip() throws Exception {
        ObjectMapper mapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        TerminologyCode parsed = mapper.readValue(mapper.writeValueAsString(terminologyCode()), TerminologyCode.class);
        assertEquals("ISO_639-1", parsed.getTerminologyId());
        assertEquals("en", parsed.getCodeString());
    }

    @Test
    public void parsesLegacyTerminologyCodeTypeName() throws Exception {
        // backwards compatibility: older Archie versions serialized the type as the simple java class name.
        ObjectMapper mapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        String legacyJson = "{\"_type\": \"TerminologyCode\", \"terminology_id\": \"ISO_639-1\", \"code_string\": \"en\"}";
        TerminologyCode parsed = mapper.readValue(legacyJson, TerminologyCode.class);
        assertEquals("ISO_639-1", parsed.getTerminologyId());
        assertEquals("en", parsed.getCodeString());
    }
}
