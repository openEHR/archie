package com.nedap.archie.json;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * A test that tests JSON parsing of Archetypes using Jackson
 *
 * Created by pieter.bos on 06/07/16.
 */
public class AOMParseTest {



    @Test
    public void parseDeliriumObservationScreening() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("delirium_observation_screening.json")) {
            Archetype archetype = JacksonUtil.getObjectMapper().readValue(stream, Archetype.class);
            System.out.println(archetype);
            assertTrue(archetype.getGenerated());
            assertThat(archetype.getArchetypeId().getFullId(), is("openEHR-EHR-GENERIC_ENTRY.delirium_observation_screening.v1.0.0"));
            assertThat(archetype.getDefinition().getRmTypeName(), is("GENERIC_ENTRY"));
            CComplexObject cluster = archetype.getDefinition().itemAtPath("/data[id2]/items[id3]/items[id4]");
            assertThat(cluster.getRmTypeName(), is("CLUSTER"));
            assertThat(cluster.getNodeId(), is("id4"));
            assertThat(cluster.getParent(), is(equalTo(archetype.getDefinition().itemAtPath("/data[id2]/items[id3]/items"))));
            assertEquals("Groep1", cluster.getTerm().getText());
            System.out.println(ADLArchetypeSerializer.serialize(archetype));
        }
    }

    @Test
    public void roundTripDeliriumObservationScreening() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("delirium_observation_screening.json")) {
            Archetype archetype = JacksonUtil.getObjectMapper().readValue(stream, Archetype.class);
            String reserialized = JacksonUtil.getObjectMapper().writeValueAsString(archetype);
            System.out.println(reserialized);
            JacksonUtil.getObjectMapper().readValue(reserialized, Archetype.class);
        }
    }
}
