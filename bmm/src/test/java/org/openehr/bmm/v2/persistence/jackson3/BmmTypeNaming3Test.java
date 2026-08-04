package org.openehr.bmm.v2.persistence.jackson3;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.jackson.BmmJacksonUtil;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Direct unit tests for BmmTypeNaming3 — covers methods not reachable from normal
 * serialization flow (init, getMechanism, idFromBaseType, getDescForKnownTypeIds, etc.).
 */
public class BmmTypeNaming3Test {

    private BmmTypeNaming3 resolver() {
        return new BmmTypeNaming3();
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
        BmmTypeNaming3 r = resolver();
        JavaType jt = JsonMapper.builder().build().getTypeFactory().constructType(PBmmSchema.class);
        r.init(jt);
    }

    @Test
    public void idFromValueAndType_delegatesToIdFromValue() {
        BmmTypeNaming3 r = resolver();
        PBmmSchema schema = new PBmmSchema();
        String fromValue = r.idFromValue(null, schema);
        String fromValueAndType = r.idFromValueAndType(null, schema, PBmmSchema.class);
        assertEquals(fromValue, fromValueAndType);
    }

    @Test
    public void idFromBaseType_withoutInit_returnsEmpty() {
        BmmTypeNaming3 r = resolver();
        assertEquals("", r.idFromBaseType(null));
    }

    @Test
    public void idFromBaseType_withInit_returnsTypeName() {
        BmmTypeNaming3 r = resolver();
        JavaType jt = JsonMapper.builder().build().getTypeFactory().constructType(PBmmSchema.class);
        r.init(jt);
        String result = r.idFromBaseType(null);
        assertNotNull(result);
    }

    @Test
    public void jackson2AndJackson3ProduceSameOutput() throws Exception {
        PBmmSchema schema = new PBmmSchema();
        schema.setBmmVersion("2.3");
        String j2 = BmmJacksonUtil.getObjectMapper().writeValueAsString(schema);
        String j3 = BmmJacksonUtil3.getObjectMapper().writeValueAsString(schema);
        assertEquals(j2, j3);
    }
}
