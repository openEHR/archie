package com.nedap.archie.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.composition.Composition;
import org.junit.Test;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RMParseTest {

    @Test
    public void parseEhrBaseJsonExample() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("pablos_example.json")) {
            RMJacksonConfiguration configuration = new RMJacksonConfiguration();
            configuration.setTypePropertyName("_type");
            Composition parsed = JacksonUtil.getObjectMapper(configuration).readValue(stream, Composition.class);
            assertEquals("__THIS_SHOULD_BE_MODIFIED_BY_THE_TEST_::piri.ehrscape.com::1", parsed.getUid().getValue());
            assertEquals("openEHR-EHR-COMPOSITION.report-mnd.v1", parsed.getArchetypeNodeId());


            String json = JacksonUtil.getObjectMapper(configuration).writeValueAsString(parsed);
            ObjectMapper simpleMapper = new ObjectMapper();
            LinkedHashMap mapped = simpleMapper.readValue(json, LinkedHashMap.class);
            assertEquals("openEHR-EHR-COMPOSITION.report-mnd.v1", mapped.get("archetype_node_id"));
            Map uidMap = (Map) mapped.get("uid");
            assertEquals("__THIS_SHOULD_BE_MODIFIED_BY_THE_TEST_::piri.ehrscape.com::1", uidMap.get("value"));
        }
    }
}
