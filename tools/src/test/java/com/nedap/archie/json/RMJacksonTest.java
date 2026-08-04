package com.nedap.archie.json;

import com.nedap.archie.json.JacksonTestMappers.JsonMapper;
import com.nedap.archie.json.JacksonTestMappers.JsonMapperFactory;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDuration;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.threeten.extra.PeriodDuration;

import java.io.InputStream;
import java.time.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RMJacksonTest {

    @ParameterizedTest
    @MethodSource("com.nedap.archie.json.JacksonTestMappers#mappers")
    public void parseEhrBaseJsonExample(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("pablos_example.json")) {
            ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
            Composition parsed = factory.create(configuration).readValue(stream, Composition.class);
            assertEquals("__THIS_SHOULD_BE_MODIFIED_BY_THE_TEST_::piri.ehrscape.com::1", parsed.getUid().getValue());
            assertEquals("openEHR-EHR-COMPOSITION.report-mnd.v1", parsed.getArchetypeNodeId());

            String json = factory.create(configuration).writeValueAsString(parsed);
            LinkedHashMap mapped = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, LinkedHashMap.class);
            assertEquals("openEHR-EHR-COMPOSITION.report-mnd.v1", mapped.get("archetype_node_id"));
            Map uidMap = (Map) mapped.get("uid");
            assertEquals("__THIS_SHOULD_BE_MODIFIED_BY_THE_TEST_::piri.ehrscape.com::1", uidMap.get("value"));
        }
    }

    @ParameterizedTest
    @MethodSource("com.nedap.archie.json.JacksonTestMappers#mappers")
    public void parseDuration(JsonMapperFactory factory) throws Exception {
        String json = "{\n" +
                "  \"_type\": \"DV_DURATION\",\n" +
                "  \"value\": \"PT12H20S\"\n" +
                "}";
        JsonMapper objectMapper = factory.create(ArchieJacksonConfiguration.createStandardsCompliant());
        DvDuration dvDuration = objectMapper.readValue(json, DvDuration.class);
        assertEquals(Duration.parse("PT12H20S"), dvDuration.getValue());
        String s = objectMapper.writeValueAsString(dvDuration);
        assertTrue(s.contains("PT12H20S"));
    }

    @ParameterizedTest
    @MethodSource("com.nedap.archie.json.JacksonTestMappers#mappers")
    public void parseNegativeDuration(JsonMapperFactory factory) throws Exception {
        String json = "{\n" +
                "  \"_type\": \"DV_DURATION\",\n" +
                "  \"value\": \"-PT12H20S\"\n" +
                "}";
        JsonMapper objectMapper = factory.create(ArchieJacksonConfiguration.createStandardsCompliant());
        DvDuration dvDuration = objectMapper.readValue(json, DvDuration.class);
        assertEquals(Duration.parse("-PT12H20S"), dvDuration.getValue());
        String s = objectMapper.writeValueAsString(dvDuration);
        assertTrue(s.contains("-PT12H20S"));
    }

    @ParameterizedTest
    @MethodSource("com.nedap.archie.json.JacksonTestMappers#mappers")
    public void parsePeriodDuration(JsonMapperFactory factory) throws Exception {
        String json = "{\n" +
                "  \"_type\": \"DV_DURATION\",\n" +
                "  \"value\": \"-P10Y10DT12H20S\"\n" +
                "}";
        JsonMapper objectMapper = factory.create(ArchieJacksonConfiguration.createStandardsCompliant());
        DvDuration dvDuration = objectMapper.readValue(json, DvDuration.class);
        assertEquals(PeriodDuration.parse("-P10Y10DT12H20S"), dvDuration.getValue());
        String s = objectMapper.writeValueAsString(dvDuration);
        assertTrue(s.contains("-P10Y10DT12H20S"));
    }

    @ParameterizedTest
    @MethodSource("com.nedap.archie.json.JacksonTestMappers#mappers")
    public void emptyDvTextIsIncluded(JsonMapperFactory factory) throws Exception {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
        configuration.setSerializeEmptyCollections(false);
        JsonMapper objectMapper = factory.create(configuration);
        DvText dvText = new DvText("");
        String actualJson = objectMapper.writeValueAsString(dvText);
        assertEquals(
                removeWhiteSpaces("{\n"
                        + "  \"_type\" : \"DV_TEXT\",\n"
                        + "  \"value\" : \"\"\n"
                        + "}"), removeWhiteSpaces(actualJson));
    }

    @ParameterizedTest
    @MethodSource("com.nedap.archie.json.JacksonTestMappers#mappers")
    public void emptyCollectionIsNotIncluded(JsonMapperFactory factory) throws Exception {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
        configuration.setSerializeEmptyCollections(false);
        JsonMapper objectMapper = factory.create(configuration);
        DvText dvText = new DvText("");
        dvText.setMappings(new ArrayList<>());
        String actualJson = objectMapper.writeValueAsString(dvText);
        assertEquals(
                removeWhiteSpaces("{\n"
                        + "  \"_type\" : \"DV_TEXT\",\n"
                        + "  \"value\" : \"\"\n"
                        + "}"), removeWhiteSpaces(actualJson));
    }

    @ParameterizedTest
    @MethodSource("com.nedap.archie.json.JacksonTestMappers#mappers")
    public void emptyCollectionInCollection(JsonMapperFactory factory) throws Exception {
        ArchieJacksonConfiguration configuration = ArchieJacksonConfiguration.createStandardsCompliant();
        configuration.setSerializeEmptyCollections(false);
        JsonMapper objectMapper = factory.create(configuration);
        Map<String, Map<String, String>> map = new LinkedHashMap<>();
        map.put("test", new LinkedHashMap<>());
        String actualJson = objectMapper.writeValueAsString(map);
        assertEquals(removeWhiteSpaces("{\"test\":{}}"), removeWhiteSpaces(actualJson));
    }

    @ParameterizedTest
    @MethodSource("com.nedap.archie.json.JacksonTestMappers#mappers")
    public void serializeDvDateTime(JsonMapperFactory factory) throws Exception {
        JsonMapper objectMapper = factory.create(ArchieJacksonConfiguration.createStandardsCompliant());
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
        assertThat(yearString, yearString.contains("\"2015\""));
        DvDateTime parsedYear = objectMapper.readValue(yearString, DvDateTime.class);
        assertEquals(year.getValue(), parsedYear.getValue());
    }

    String removeWhiteSpaces(String input) {
        return input.replaceAll("\\s+", "");
    }
}
