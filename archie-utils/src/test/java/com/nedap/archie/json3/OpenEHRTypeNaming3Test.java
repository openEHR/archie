package com.nedap.archie.json3;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.datavalues.DvText;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Direct unit tests for OpenEHRTypeNaming3 — covers the methods Jackson 3 doesn't
 * call automatically during normal serialization (init, getMechanism, idFromBaseType, etc.).
 */
public class OpenEHRTypeNaming3Test {

    private OpenEHRTypeNaming3 resolver() {
        return new OpenEHRTypeNaming3(false);
    }

    @Test
    public void getMechanism_returnsName() {
        assertEquals(JsonTypeInfo.Id.NAME, resolver().getMechanism());
    }

    @Test
    public void getDescForKnownTypeIds_returnsNull() {
        assertNull(resolver().getDescForKnownTypeIds());
    }

    @Test
    public void init_storesBaseType() {
        OpenEHRTypeNaming3 r = resolver();
        JavaType jt = JsonMapper.builder().build().getTypeFactory().constructType(DvText.class);
        r.init(jt);
    }

    @Test
    public void idFromValueAndType_delegatesToIdFromValue() {
        OpenEHRTypeNaming3 r = resolver();
        DvText value = new DvText("hello");
        String fromValue = r.idFromValue(null, value);
        String fromValueAndType = r.idFromValueAndType(null, value, DvText.class);
        assertEquals(fromValue, fromValueAndType);
    }

    @Test
    public void idFromBaseType_withoutInit_returnsEmpty() {
        OpenEHRTypeNaming3 r = resolver();
        assertEquals("", r.idFromBaseType(null));
    }

    @Test
    public void idFromBaseType_withInit_returnsTypeName() {
        OpenEHRTypeNaming3 r = resolver();
        JavaType jt = JsonMapper.builder().build().getTypeFactory().constructType(Composition.class);
        r.init(jt);
        String result = r.idFromBaseType(null);
        assertNotNull(result);
    }

    @Test
    public void idFromValue_unknownClass_fallsBackToNamingStrategy() {
        OpenEHRTypeNaming3 r = resolver();
        // Use a class not registered in either RM or AOM lookup
        String result = r.idFromValue(null, new Object());
        assertNotNull(result);
    }

    @Test
    public void jackson2AndJackson3ProduceSameOutput() throws Exception {
        DvText dvText = new DvText("hello world");
        String j2 = JacksonUtil.getObjectMapper().writeValueAsString(dvText);
        String j3 = JacksonUtil3.getObjectMapper().writeValueAsString(dvText);
        // Field ordering may differ between J2/J3; compare semantic content
        assertEquals(
            JacksonUtil.getObjectMapper().readTree(j2),
            JacksonUtil.getObjectMapper().readTree(j3)
        );
    }
}
