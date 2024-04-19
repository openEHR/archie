package com.nedap.archie.rm.composition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import org.openehr.rm.composition.Observation;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.ObjectRef;
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
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        objectMapper.writeValue(sw, expected);

        Observation actual = objectMapper.readValue(sw.toString(), Observation.class);
        assertEquals(expected, actual);
    }
}
