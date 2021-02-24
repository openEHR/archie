package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.base.Interval;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Test;
import org.threeten.extra.PeriodDuration;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * A test that tests JSON parsing of Archetypes using Jackson
 *
 * Created by pieter.bos on 06/07/16.
 */
public class AOMJacksonTest {

    @Test
    public void parseDeliriumObservationScreening() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("delirium_observation_screening.json")) {
            Archetype archetype = JacksonUtil.getObjectMapper().readValue(stream, Archetype.class);
            System.out.println(ADLArchetypeSerializer.serialize(archetype));
            assertTrue(archetype.getGenerated());
            dosAssertions(archetype);

        }
    }

    private void dosAssertions(Archetype archetype) {
        assertEquals("with a value", archetype.getOtherMetaData().get("something"));
        assertEquals("another value", archetype.getOtherMetaData().get("and_also"));
        assertThat(archetype.getArchetypeId().getFullId(), is("openEHR-EHR-GENERIC_ENTRY.delirium_observation_screening.v1.0.0"));
        assertThat(archetype.getDefinition().getRmTypeName(), is("GENERIC_ENTRY"));
        CComplexObject cluster = archetype.getDefinition().itemAtPath("/data[id2]/items[id3]/items[id4]");
        assertThat(cluster.getRmTypeName(), is("CLUSTER"));
        assertThat(cluster.getNodeId(), is("id4"));
        assertThat(cluster.getParent(), is(equalTo(archetype.getDefinition().itemAtPath("/data[id2]/items[id3]/items"))));
        assertEquals("Groep1", cluster.getTerm().getText());
    }

    @Test
    public void roundTripDeliriumObservationScreening() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("delirium_observation_screening.json")) {
            Archetype archetype = JacksonUtil.getObjectMapper().readValue(stream, Archetype.class);
            String reserialized = JacksonUtil.getObjectMapper().writeValueAsString(archetype);
            System.out.println(reserialized);
            Archetype archetype1 = JacksonUtil.getObjectMapper().readValue(reserialized, Archetype.class);
            dosAssertions(archetype1);
        }
    }

    @Test
    public void optIncludedTerminologies() throws JsonProcessingException {
        OperationalTemplate opt = new OperationalTemplate();
        opt.setArchetypeId(new ArchetypeHRID("openEHR-EHR-CLUSTER.test.v0.0.1"));
        opt.addComponentTerminology("openEHR-EHR-CLUSTER.included.v0.0.1", new ArchetypeTerminology());
        String s = JacksonUtil.getObjectMapper().writeValueAsString(opt);
        assertTrue(s.contains("  \"component_terminologies\" : {\n" +
                "    \"openEHR-EHR-CLUSTER.included.v0.0.1\" : {\n" +
                "      \"@type\" : \"ARCHETYPE_TERMINOLOGY\",\n" +
                "      \"term_definitions\" : { },\n" +
                "      \"term_bindings\" : { },\n" +
                "      \"terminology_extracts\" : { },\n" +
                "      \"value_sets\" : { }\n" +
                "    }\n"));

        String adlOpt = ADLArchetypeSerializer.serialize(opt);
        assertTrue(adlOpt.contains("component_terminologies = < [\"openEHR-EHR-CLUSTER.included.v0.0.1\"] = <\n" +
                "        >\n" +
                "    > "));
    }


    @Test
    public void cDuration() throws Exception {
        CDuration cDuration = new CDuration();
        cDuration.addConstraint(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), Duration.of(10, ChronoUnit.SECONDS)));
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(RMJacksonConfiguration.createStandardsCompliant());
        String cDurationJson = objectMapper.writeValueAsString(cDuration);
        assertTrue(cDurationJson.contains("-PT10H"));
        assertTrue(cDurationJson.contains("PT10S"));
        System.out.println(cDurationJson);

        CDuration parsedDuration = objectMapper.readValue(cDurationJson, CDuration.class);
        assertEquals(Lists.newArrayList(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), Duration.of(10, ChronoUnit.SECONDS))), parsedDuration.getConstraint());
    }

    @Test
    public void cDurationPeriodDuration() throws Exception {
        CDuration cDuration = new CDuration();
        PeriodDuration tenYearsTenSeconds = PeriodDuration.of(Period.of(10, 0, 0), Duration.of(10, ChronoUnit.SECONDS));
        cDuration.addConstraint(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), tenYearsTenSeconds));
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(RMJacksonConfiguration.createStandardsCompliant());
        String cDurationJson = objectMapper.writeValueAsString(cDuration);
        assertTrue(cDurationJson.contains("-PT10H"));
        assertTrue(cDurationJson.contains("P10YT10S"));
        System.out.println(cDurationJson);

        CDuration parsedDuration = objectMapper.readValue(cDurationJson, CDuration.class);
        assertEquals(Lists.newArrayList(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), tenYearsTenSeconds)), parsedDuration.getConstraint());
    }
}
