package com.nedap.archie.rm.ehr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import org.junit.Test;
import org.openehr.rm.ehr.EhrStatus;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EhrStatusTest {

    @Test
    public void testBooleanSerialization() throws IOException {
        EhrStatus status = new EhrStatus();
        StringWriter stringWriter = new StringWriter();
        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper();
        objectMapper.writeValue(stringWriter, status);

        Map<?, ?> actual = objectMapper.readValue(stringWriter.toString(), Map.class);
        assertTrue(actual.containsKey("is_queryable"));
        assertFalse(actual.containsKey("queryable"));
        assertTrue(actual.containsKey("is_modifiable"));
        assertFalse(actual.containsKey("modifiable"));
    }
}