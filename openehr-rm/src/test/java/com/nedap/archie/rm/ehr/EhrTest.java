package com.nedap.archie.rm.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import org.openehr.rm.ehr.Ehr;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.ObjectRef;
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
        expected.setEhrStatus(new ObjectRef<>(new HierObjectId("ehr_status_value"), "ehr_status_namespace", "ehr_status_type"));
        expected.setEhrAccess(new ObjectRef<>(new HierObjectId("ehr_access_value"), "ehr_access_namespace", "ehr_access_type"));
        expected.setDirectory(new ObjectRef<>(new HierObjectId("directory_value"), "directory_namespace", "directory_type"));

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
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        objectMapper.writeValue(sw, expected);

        Ehr actual = objectMapper.readValue(sw.toString(), Ehr.class);
        assertEquals(expected, actual);
    }
}
