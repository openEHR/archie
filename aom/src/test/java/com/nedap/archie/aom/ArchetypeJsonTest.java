package com.nedap.archie.aom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.json.JacksonUtil;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArchetypeJsonTest {

    @Test
    public void testCTerminologyConstraint() throws Exception {
        Archetype archetype;
        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.c_terminology_code.v1.0.0.adls")) {
            archetype = new ADLParser().parse(stream);
        }
        String json = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).writeValueAsString(archetype);

        JsonNode objectNode = new ObjectMapper().readValue(json, JsonNode.class);
        JsonNode elements = objectNode.get("definition").get("attributes").get(0).get("children");

        assertRmTypeName("DV_CODED_TEXT", elements.get(0));
        assertRmTypeName("DV_ORDINAL", elements.get(1));
        assertRmTypeName("DV_SCALE", elements.get(2));
    }

    private void assertRmTypeName(String expectedDataValueRmTypeName, JsonNode element) {
        JsonNode dataValue = element.get("attributes").get(0).get("children").get(0);
        String dataValueRmTypeName = dataValue.get("rm_type_name").textValue(); // DV_CODED_TEXT, DV_ORDINAL or DV_SCALE
        assertEquals(expectedDataValueRmTypeName, dataValueRmTypeName);

        JsonNode dataValueAttribute = dataValue.get("attributes").get(dataValueRmTypeName.equals("DV_CODED_TEXT") ? 0 : 1);
        assertEquals(dataValueRmTypeName.equals("DV_CODED_TEXT") ? "defining_code" : "symbol", dataValueAttribute.get("rm_attribute_name").textValue());

        JsonNode cTerminologyConstraint = dataValueAttribute.get("children").get(0);
        assertEquals("C_TERMINOLOGY_CODE", cTerminologyConstraint.get("_type").textValue());
        assertEquals(dataValueRmTypeName.equals("DV_CODED_TEXT") ? "CODE_PHRASE" : "DV_CODED_TEXT", cTerminologyConstraint.get("rm_type_name").textValue());
    }
}
