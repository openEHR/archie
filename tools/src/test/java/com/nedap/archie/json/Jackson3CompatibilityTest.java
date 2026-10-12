package com.nedap.archie.json;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.json3.JacksonUtil3;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datastructures.ItemTable;
import com.nedap.archie.rm.datavalues.DvText;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.exc.InvalidTypeIdException;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks that Jackson 3 behaves the same as Jackson 2 where Jackson 3 changed its defaults.
 */
public class Jackson3CompatibilityTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void nullMapValuesAreNotSerialized(boolean serializeEmptyCollections) throws Exception {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
        configuration.setSerializeEmptyCollections(serializeEmptyCollections);

        ArchetypeTerminology terminology = new ArchetypeTerminology();
        Map<String, Map<String, URI>> termBindings = new LinkedHashMap<>();
        termBindings.put("SNOMED-CT", null);
        terminology.setTermBindings(termBindings);

        String jackson2Json = JacksonUtil.getObjectMapper(configuration).writeValueAsString(terminology);
        String jackson3Json = JacksonUtil3.getObjectMapper(configuration).writeValueAsString(terminology);
        assertFalse(jackson3Json.contains("SNOMED-CT"), jackson3Json);
        assertEquals(jackson2Json, jackson3Json);
    }

    @Test
    public void unknownTypeIdThrowsInvalidTypeIdException() {
        String json = "{\"_type\": \"NOT_A_TYPE\", \"rm_type_name\": \"ELEMENT\"}";
        assertThrows(InvalidTypeIdException.class, () -> JacksonUtil3.getObjectMapper().readValue(json, CObject.class));
    }

    @Test
    public void readsItemTableWrittenByJackson2InStrictMode() throws Exception {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
        configuration.setFailOnUnknownProperties(true);

        Element element = new Element("id3", new DvText("element"), new DvText("value"));
        Cluster row = new Cluster("id2", new DvText("row"), new ArrayList<>(List.of(element)));
        ItemTable table = new ItemTable("id1", new DvText("table"), new ArrayList<>(List.of(row)));

        // Jackson 2 serializes the derived items property
        String json = JacksonUtil.getObjectMapper(configuration).writeValueAsString(table);
        assertTrue(json.contains("\"items\""));

        ItemTable parsed = JacksonUtil3.getObjectMapper(configuration).readValue(json, ItemTable.class);
        assertEquals(1, parsed.getRows().size());
        Element parsedElement = (Element) parsed.getRows().get(0).getItems().get(0);
        assertEquals("value", ((DvText) parsedElement.getValue()).getValue());
    }
}
