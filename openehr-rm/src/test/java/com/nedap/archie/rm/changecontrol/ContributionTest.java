package com.nedap.archie.rm.changecontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.json.RMJacksonConfiguration;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ContributionTest {

    @Test
    public void testJsonSerialization() throws IOException {
        Contribution expected = new Contribution();

        List<ObjectRef<?>> versions = new ArrayList<>();
        versions.add(new ObjectRef<>(new HierObjectId("value"), "namespace", "type"));
        expected.setVersions(versions);

        StringWriter sw = new StringWriter();
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(RMJacksonConfiguration.createStandardsCompliant());
        objectMapper.writeValue(sw, expected);

        Contribution actual = objectMapper.readValue(sw.toString(), Contribution.class);
        assertEquals(expected, actual);
    }
}
