package com.nedap.archie.aom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonArray elements = jsonObject.getAsJsonObject("definition")
                .getAsJsonArray("attributes").get(0).getAsJsonObject()
                .getAsJsonArray("children");

        assertRmTypeName("DV_CODED_TEXT", elements.get(0).getAsJsonObject());
        assertRmTypeName("DV_ORDINAL", elements.get(1).getAsJsonObject());
        assertRmTypeName("DV_SCALE", elements.get(2).getAsJsonObject());
    }

    private void assertRmTypeName(String expectedDataValueRmTypeName, JsonObject element) {
        JsonObject dataValue = element.getAsJsonArray("attributes").get(0).getAsJsonObject()
                .getAsJsonArray("children").get(0).getAsJsonObject();
        String dataValueRmTypeName = dataValue.get("rm_type_name").getAsString(); // DV_CODED_TEXT, DV_ORDINAL or DV_SCALE
        assertEquals(expectedDataValueRmTypeName, dataValueRmTypeName);

        JsonObject dataValueAttribute = dataValue.getAsJsonArray("attributes").get(dataValueRmTypeName.equals("DV_CODED_TEXT") ? 0 : 1).getAsJsonObject();
        assertEquals(dataValueRmTypeName.equals("DV_CODED_TEXT") ? "defining_code" : "symbol", dataValueAttribute.get("rm_attribute_name").getAsString());

        JsonObject cTerminologyConstraint = dataValueAttribute.getAsJsonArray("children").get(0).getAsJsonObject();
        assertEquals("C_TERMINOLOGY_CODE", cTerminologyConstraint.get("_type").getAsString());
        assertEquals(dataValueRmTypeName.equals("DV_CODED_TEXT") ? "CODE_PHRASE" : "DV_CODED_TEXT", cTerminologyConstraint.get("rm_type_name").getAsString());
    }
}
