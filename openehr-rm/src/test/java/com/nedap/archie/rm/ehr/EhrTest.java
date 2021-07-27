package com.nedap.archie.rm.ehr;

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

public class EhrTest {

    @Test
    public void testJsonSerialization() throws IOException {
        Ehr expected = new Ehr();

        List<ObjectRef<?>> contributions = new ArrayList<>();
        contributions.add(new ObjectRef<>(new HierObjectId("contribution_value"), "contribution_namespace", "contribution_type"));
        expected.setContributions(contributions);

        List<ObjectRef<?>> compositions = new ArrayList<>();
        compositions.add(new ObjectRef<>(new HierObjectId("composition_value"), "composition_namespace", "composition_type"));
        expected.setCompositions(compositions);

        List<ObjectRef<?>> folders = new ArrayList<>();
        folders.add(new ObjectRef<>(new HierObjectId("folder_value"), "folder_namespace", "folder_type"));
        expected.setFolders(folders);

        StringWriter sw = new StringWriter();
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(RMJacksonConfiguration.createStandardsCompliant());
        objectMapper.writeValue(sw, expected);

        Ehr actual = objectMapper.readValue(sw.toString(), Ehr.class);
        assertEquals(expected, actual);
    }
}
