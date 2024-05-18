package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.datavalues.quantity.datetime.DvDuration;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import org.junit.Test;
import org.threeten.extra.PeriodDuration;

import java.io.InputStream;
import java.time.*;
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
            Composition parsed = OpenEhrRmJacksonUtil.getObjectMapper(configuration).readValue(stream, Composition.class);
            assertEquals("__THIS_SHOULD_BE_MODIFIED_BY_THE_TEST_::piri.ehrscape.com::1", parsed.getUid().getValue());
            assertEquals("openEHR-EHR-COMPOSITION.report-mnd.v1", parsed.getArchetypeNodeId());


            String json = OpenEhrRmJacksonUtil.getObjectMapper(configuration).writeValueAsString(parsed);
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
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
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
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
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
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        DvDuration dvDuration = objectMapper.readValue(json, DvDuration.class);
        assertEquals(PeriodDuration.parse("-P10Y10DT12H20S"), dvDuration.getValue());

        String s = objectMapper.writeValueAsString(dvDuration);
        assertTrue(s.contains("-P10Y10DT12H20S"));
    }

    @Test
    public void emptyDvTextIsIncluded() throws JsonProcessingException {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
        configuration.setSerializeEmptyCollections(false);
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(configuration);
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
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(configuration);
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
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(configuration);
        Map<String, Map<String, String>> map = new LinkedHashMap<>();
        map.put("test", new LinkedHashMap<>());


        String actualJson = objectMapper.writeValueAsString(map);
        assertEquals(
                removeWhiteSpaces("{\"test\":{}}"),
                removeWhiteSpaces(actualJson));
    }

    @Test
    public void serializeDvDateTime() throws Exception {
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        DvDateTime dateTime = new DvDateTime(LocalDateTime.of(2015, 1, 1, 12, 10, 12, 0));
        String dateTimeString = objectMapper.writeValueAsString(dateTime);
        assertTrue(dateTimeString.contains("\"2015-01-01T12:10:12\""));
        DvDateTime parsedDateTime = objectMapper.readValue(dateTimeString, DvDateTime.class);
        assertEquals(dateTime.getValue(), parsedDateTime.getValue());

        DvDateTime date = new DvDateTime(LocalDate.of(2015, 1, 1));
        String dateString = objectMapper.writeValueAsString(date);
        assertTrue(dateString.contains("\"2015-01-01\""));
        DvDateTime parsedDate = objectMapper.readValue(dateString, DvDateTime.class);
        assertEquals(date.getValue(), parsedDate.getValue());

        DvDateTime yearMonth = new DvDateTime(YearMonth.of(2015, 1));
        String yearMonthString = objectMapper.writeValueAsString(yearMonth);
        assertTrue(yearMonthString.contains("\"2015-01\""));
        DvDateTime parsedYearMonth = objectMapper.readValue(yearMonthString, DvDateTime.class);
        assertEquals(yearMonth.getValue(), parsedYearMonth.getValue());

        DvDateTime year = new DvDateTime(Year.of(2015));
        String yearString = objectMapper.writeValueAsString(year);
        assertTrue(yearString, yearString.contains("\"2015\""));
        DvDateTime parsedYear = objectMapper.readValue(yearString, DvDateTime.class);
        assertEquals(year.getValue(), parsedYear.getValue());
    }

    String removeWhiteSpaces(String input) {
        return input.replaceAll("\\s+", "");
    }



}
