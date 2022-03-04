package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDuration;
import org.junit.Test;
import org.threeten.extra.PeriodDuration;

import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RMJacksonTest {

    @Test
    public void parseEhrBaseJsonExample() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("pablos_example.json")) {
            ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
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

    @Test
    public void parseDuration() throws Exception {
        String json = "{\n" +
                "  \"_type\": \"DV_DURATION\",\n" +
                "  \"value\": \"PT12H20S\"\n" +
                "}";
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        DvDuration dvDuration = objectMapper.readValue(json, DvDuration.class);
        assertEquals(Duration.parse("PT12H20S"), dvDuration.getValue());

        String s = objectMapper.writeValueAsString(dvDuration);
        assertTrue(s.contains("PT12H20S"));
    }

    @Test
    public void parseNegativeDuration() throws Exception {
        String json = "{\n" +
                "  \"_type\": \"DV_DURATION\",\n" +
                "  \"value\": \"-PT12H20S\"\n" +
                "}";
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        DvDuration dvDuration = objectMapper.readValue(json, DvDuration.class);
        assertEquals(Duration.parse("-PT12H20S"), dvDuration.getValue());

        String s = objectMapper.writeValueAsString(dvDuration);
        assertTrue(s.contains("-PT12H20S"));
    }

    @Test
    public void parsePeriodDuration() throws Exception {
        String json = "{\n" +
                "  \"_type\": \"DV_DURATION\",\n" +
                "  \"value\": \"-P10Y10DT12H20S\"\n" +
                "}";
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        DvDuration dvDuration = objectMapper.readValue(json, DvDuration.class);
        assertEquals(PeriodDuration.parse("-P10Y10DT12H20S"), dvDuration.getValue());

        String s = objectMapper.writeValueAsString(dvDuration);
        assertTrue(s.contains("-P10Y10DT12H20S"));
    }

    @Test
    public void emptyDvTextIsIncluded() throws JsonProcessingException {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
        configuration.setSerializeEmptyCollections(false);
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(configuration);
        DvText dvText = new DvText("");


        String actualJson = objectMapper.writeValueAsString(dvText);
        assertEquals(
                        removeWhiteSpaces("{\n"
                                + "  \"_type\" : \"DV_TEXT\",\n"
                                + "  \"value\" : \"\"\n"
                                + "}"), removeWhiteSpaces(actualJson));
    }

    @Test
    public void emptyCollectionIsNotIncluded() throws JsonProcessingException {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();;
        configuration.setSerializeEmptyCollections(false);
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(configuration);
        DvText dvText = new DvText("");
        dvText.setMappings(new ArrayList<>());


        String actualJson = objectMapper.writeValueAsString(dvText);
        assertEquals(
                removeWhiteSpaces("{\n"
                        + "  \"_type\" : \"DV_TEXT\",\n"
                        + "  \"value\" : \"\"\n"
                        + "}"), removeWhiteSpaces(actualJson));
    }

    @Test
    public void emptyCollectionInCollection() throws JsonProcessingException {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();;
        configuration.setSerializeEmptyCollections(false);
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(configuration);
        Map<String, Map<String, String>> map = new LinkedHashMap<>();
        map.put("test", new LinkedHashMap<>());


        String actualJson = objectMapper.writeValueAsString(map);
        assertEquals(
                removeWhiteSpaces("{\"test\":{}}"),
                removeWhiteSpaces(actualJson));
    }

    String removeWhiteSpaces(String input) {
        return input.replaceAll("\\s+", "");
    }
}
