package com.nedap.archie.json;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.json3.JacksonUtil3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.exc.InvalidTypeIdException;

import java.net.URI;
import java.util.LinkedHashMap;
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
        assertFalse(jackson2Json.contains("SNOMED-CT"), jackson2Json);
        assertFalse(jackson3Json.contains("SNOMED-CT"), jackson3Json);
        assertEquals(jackson2Json, jackson3Json);
    }

    @Test
    public void unknownTypeIdThrowsInvalidTypeIdException() {
        String json = "{\"_type\": \"NOT_A_TYPE\", \"rm_type_name\": \"ELEMENT\"}";
        assertThrows(InvalidTypeIdException.class, () -> JacksonUtil3.getObjectMapper().readValue(json, CObject.class));
    }
}
