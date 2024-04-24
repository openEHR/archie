package com.nedap.archie.rm.changecontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import org.openehr.rm.changecontrol.Contribution;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.ObjectId;
import org.openehr.rm.support.identification.ObjectRef;
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

        List<ObjectRef<? extends ObjectId>> versions = new ArrayList<>();
        versions.add(new ObjectRef<>(new HierObjectId("value"), "namespace", "type"));
        expected.setVersions(versions);

        StringWriter sw = new StringWriter();
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        objectMapper.writeValue(sw, expected);

        Contribution actual = objectMapper.readValue(sw.toString(), Contribution.class);
        assertEquals(expected, actual);
    }
}
