package com.nedap.archie.rm.composition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class ObservationTest {

    @Test
    public void testJsonSerialization() throws IOException {
        Observation expected = new Observation();

        expected.setGuidelineId(new ObjectRef<>(new HierObjectId("value"), "namespace", "type"));
        StringWriter sw = new StringWriter();
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        objectMapper.writeValue(sw, expected);

        Observation actual = objectMapper.readValue(sw.toString(), Observation.class);
        assertEquals(expected, actual);
    }
}
