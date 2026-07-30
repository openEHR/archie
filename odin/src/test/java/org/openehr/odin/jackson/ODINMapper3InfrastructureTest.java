package org.openehr.odin.jackson;

import org.junit.jupiter.api.Test;
import org.openehr.odin.jackson.testclasses.ContainerWithList;
import org.openehr.odin.jackson.testclasses.TestObject;
import org.openehr.odin.jackson3.ODINFactory3;
import org.openehr.odin.jackson3.ODINGenerator3;
import org.openehr.odin.jackson3.ODINMapper3;
import org.openehr.odin.jackson3.ODINPrettyPrinter3;
import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.util.DefaultIndenter;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//This class can be removed after complete jackson 3 switch
public class ODINMapper3InfrastructureTest {

    // ── ODINFactory3 ──────────────────────────────────────────────────────────
    @Test
    public void factory_formatName() {
        assertEquals("ODIN", new ODINFactory3().getFormatName());
    }

    @Test
    public void factory_cannotUseCharArrays() {
        assertFalse(new ODINFactory3().canUseCharArrays());
    }

    @Test
    public void factory_utf8GeneratorThrows() {
        ODINMapper3 mapper = ODINMapper3.builder().build();
        assertThrows(UnsupportedOperationException.class,
                () -> mapper.writeValueAsBytes(new TestObject()));
    }

    // ── ODINMapper3 ───────────────────────────────────────────────────────────
    @Test
    public void mapper_getFactory() {
        ODINMapper3 mapper = ODINMapper3.builder().build();
        ODINFactory3 factory = mapper.getFactory();
        assertNotNull(factory);
        assertEquals("ODIN", factory.getFormatName());
    }

    // ── ODINPrettyPrinter3 ────────────────────────────────────────────────────
    @Test
    public void prettyPrinter_createInstance() {
        ODINPrettyPrinter3 pp = new ODINPrettyPrinter3();
        assertNotNull(pp.createInstance());
    }

    @Test
    public void prettyPrinter_withObjectIndenter() {
        ODINPrettyPrinter3 pp = new ODINPrettyPrinter3();
        assertNotNull(pp.withObjectIndenter(new tools.jackson.core.util.DefaultIndenter()));
    }

    @Test
    public void prettyPrinter_withObjectIndenter_null() {
        ODINPrettyPrinter3 pp = new ODINPrettyPrinter3();
        assertNotNull(pp.withObjectIndenter(null));
    }

    @Test
    public void prettyPrinter_withSeparators() {
        ODINPrettyPrinter3 pp = new ODINPrettyPrinter3();
        assertNotNull(pp.withSeparators(tools.jackson.core.util.Separators.createDefaultInstance()));
    }

    @Test
    public void prettyPrinter_indentObjectsWith_null() {
        ODINPrettyPrinter3 pp = new ODINPrettyPrinter3();
        pp.indentObjectsWith(null);
    }

    // ── Numeric type serialization (double, float, long) ─────────────────────
    @Test
    public void serialize_numericTypes_matchesJackson2() throws Exception {
        TestObject obj = new TestObject();
        obj.setStringField("test");
        obj.setIntField(1);
        obj.setLongField(123456789L);
        obj.setDoubleField(3.14);
        obj.setFloatField(1.5f);

        String j2 = new ODINMapper().writeValueAsString(obj);
        String j3 = ODINMapper3.builder().deactivateDefaultTyping().build().writeValueAsString(obj);
        assertEquals(j2, j3);
    }

    // ── Empty nested object (PrettyPrinter writeEndObject with 0 entries) ────
    @Test
    public void serialize_emptyObject_doesNotThrow() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("nested", new LinkedHashMap<>());
        ODINMapper3.builder().deactivateDefaultTyping().build().writeValueAsString(map);
    }

    // ── String-keyed map (exercises OdinStringMapKeySerializer) ──────────────
    @Test
    public void serialize_stringKeyedMap_matchesJackson2() throws Exception {
        ContainerWithList container = new ContainerWithList();
        container.setSomeField("test");
        TestObject obj = new TestObject();
        obj.setStringField("val");
        obj.setIntField(1);
        container.addListItem(obj);

        String j2 = new ODINMapper().writeValueAsString(container);
        String j3 = ODINMapper3.builder().deactivateDefaultTyping().build().writeValueAsString(container);
        assertEquals(j2, j3);
    }

    // ── ODINFactory3 copy constructor / copy() ────────────────────────────────
    @Test
    public void factory_copy() {
        ODINFactory3 original = new ODINFactory3();
        ODINFactory3 copy = original.copy();
        assertNotNull(copy);
        assertEquals("ODIN", copy.getFormatName());
    }

    // ── ODINGenerator3 getter methods ─────────────────────────────────────────
    @Test
    public void generator_getterMethods() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            assertNotNull(gen.version());
            assertNotNull(gen.streamWriteContext());
            assertNotNull(gen.streamWriteCapabilities());
            assertEquals(sw, gen.streamWriteOutputTarget());
            assertEquals(-1, gen.streamWriteOutputBuffered());
            assertFalse(gen.canWriteObjectId());
            assertTrue(gen.canWriteTypeId());
            assertNotNull(gen.getPrettyPrinter());
            // currentValue and assignCurrentValue
            gen.assignCurrentValue("test");
            assertNotNull(gen.currentValue());
            // getFormatFeatures
            ((org.openehr.odin.jackson3.ODINGenerator3) gen).getFormatFeatures();
            // writePOJO and writePropertyId
            gen.writeStartObject();
            gen.writePropertyId(1L);
            gen.writeNumber(42);
            gen.writeEndObject();
        }
    }

    // ── ODINGenerator3 special number types ──────────────────────────────────
    @Test
    public void generator_specialNumberTypes() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("bi");
            gen.writeNumber(BigInteger.valueOf(99999L));
            gen.writeName("bd");
            gen.writeNumber(new BigDecimal("3.14159"));
            gen.writeName("str");
            gen.writeNumber("42");
            gen.writeName("sh");
            gen.writeNumber((short) 7);
            gen.writeName("nil");
            gen.writeNull();
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 string overloads ──────────────────────────────────────
    @Test
    public void generator_stringOverloads() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("charArr");
            gen.writeString("hello".toCharArray(), 0, 5);
            gen.writeName("sstr");
            gen.writeString(new tools.jackson.core.io.SerializedString("world"));
            gen.writeName("utf8");
            gen.writeUTF8String("hi".getBytes(java.nio.charset.StandardCharsets.UTF_8), 0, 2);
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 writeString(null) covers null-string branch ───────────
    @Test
    public void generator_writeNullString() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("nil");
            gen.writeString((String) null);
            gen.writeEndObject();
        }
    }

    // ── ODINGenerator3 primitive array — covers comma separator ──────────────
    @Test
    public void generator_primitiveArrayComma() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("arr");
            gen.writeStartArray();
            gen.writeNumber(1);   // first element — STATUS_OK_AS_IS
            gen.writeNumber(2);   // second element — STATUS_OK_AFTER_COMMA (non-object array)
            gen.writeEndArray();
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 typeId at root context ─────────────────────────────────
    @Test
    public void generator_typeIdAtRoot() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeTypeId("MyType");   // sets _typeId and _typeIdAtRoot at root
            gen.writeStartObject();
            gen.writeName("x");
            gen.writeNumber(1);
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 raw write overloads (bypass write context) ────────────
    @Test
    public void generator_rawWriteOverloads() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        // writeRaw variants bypass write context — call them outside object/array
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeRaw("hello", 0, 5);
            gen.writeRaw(new char[]{'w', 'o', 'r', 'l', 'd'}, 0, 5);
            gen.writeRaw('!');
        }
        assertEquals("helloworld!", sw.toString());
    }

    // ── ODINGenerator3 binary ─────────────────────────────────────────────────
    @Test
    public void generator_binary() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("bin");
            gen.writeBinary(new byte[]{1, 2, 3});
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 flush ──────────────────────────────────────────────────
    @Test
    public void generator_flush() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("x");
            gen.writeNumber(1);
            gen.writeEndObject();
            gen.flush();
        }
    }

    // ── ODINGenerator3 writeStartArray/Object forValue overloads ─────────────
    @Test
    public void generator_startArrayAndObjectForValueOverloads() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("arr");
            gen.writeStartArray("ignored");
            gen.writeEndArray();
            gen.writeName("arr2");
            gen.writeStartArray("ignored", 0);
            gen.writeEndArray();
            gen.writeName("obj");
            gen.writeStartObject("ignored");
            gen.writeEndObject();
            gen.writeName("obj2");
            gen.writeStartObject("ignored", 0);
            gen.writeEndObject();
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 writeObjectId / writeObjectRef ────────────────────────
    @Test
    public void generator_objectIdAndRef() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (JsonGenerator gen = factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeObjectId("id-1");
            gen.writeName("x");
            gen.writeNumber(1);
            gen.writeName("ref");
            gen.writeObjectRef("ref-1");
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 writeRawValue(String/char[], offset, len) ─────────────
    @Test
    public void generator_writeRawValueWithOffset() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("s");
            gen.writeRawValue("hello", 1, 3);  // "ell"
            gen.writeName("c");
            gen.writeRawValue(new char[]{'w', 'o', 'r', 'l', 'd'}, 1, 3);  // "orl"
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 writeBoolean ───────────────────────────────────────────
    @Test
    public void generator_writeBoolean() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("t");
            gen.writeBoolean(true);
            gen.writeName("f");
            gen.writeBoolean(false);
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 writeRawValue(String) ─────────────────────────────────
    @Test
    public void generator_writeRawValue() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("raw");
            gen.writeRawValue("raw_content");
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 writePOJO ──────────────────────────────────────────────
    @Test
    public void generator_writePOJO() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("pojo");
            gen.writePOJO(new Object());
            gen.writeEndObject();
        }
    }

    // ── ODINGenerator3 null paths for writeNumber / writeBinary ──────────────
    @Test
    public void generator_nullNumberAndBinaryPaths() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("bi");
            gen.writeNumber((BigInteger) null);
            gen.writeName("bd");
            gen.writeNumber((BigDecimal) null);
            gen.writeName("str");
            gen.writeNumber((String) null);
            gen.writeName("bin");
            gen.writeBinary(Base64Variants.MIME, null, 0, 0);
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 writeBinary with offset ────────────────────────────────
    @Test
    public void generator_writeBinaryWithOffset() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("bin");
            gen.writeBinary(Base64Variants.MIME, new byte[]{0, 1, 2, 3}, 1, 2);
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 writeBinary with non-default Base64 variant ────────────
    @Test
    public void generator_writeBinaryNonDefaultVariant() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("bin");
            gen.writeBinary(Base64Variants.PEM, new byte[]{1, 2, 3}, 0, 3);
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 writeNumber(BigDecimal) with WRITE_BIGDECIMAL_AS_PLAIN ─
    @Test
    public void generator_writeBigDecimalAsPlain() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("bd", new BigDecimal("1.2E+10"));
        String result = ODINMapper3.builder()
                .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN)
                .deactivateDefaultTyping()
                .build()
                .writeValueAsString(map);
        assertNotNull(result);
    }

    // ── ODINGenerator3 error paths — use direct try-catch (not lambda) ────────
    @Test
    public void generator_writeNameInValueContext_throws() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), new StringWriter());
        boolean threw = false;
        try {
            gen.writeStartObject();
            gen.writeName("key");
            gen.writeName("bad"); // context expects VALUE, not NAME
        } catch (Exception e) {
            threw = true;
        } finally {
            try { gen.close(); } catch (Exception ignored) {}
        }
        assertTrue(threw);
    }

    @Test
    public void generator_writeEndArrayOutsideArray_throws() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), new StringWriter());
        boolean threw = false;
        try {
            gen.writeEndArray(); // not in array context
        } catch (Exception e) {
            threw = true;
        } finally {
            try { gen.close(); } catch (Exception ignored) {}
        }
        assertTrue(threw);
    }

    @Test
    public void generator_writeEndObjectOutsideObject_throws() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), new StringWriter());
        boolean threw = false;
        try {
            gen.writeEndObject(); // not in object context
        } catch (Exception e) {
            threw = true;
        } finally {
            try { gen.close(); } catch (Exception ignored) {}
        }
        assertTrue(threw);
    }

    // ── ODINGenerator3 writeTypeId inside nested object ───────────────────────
    @Test
    public void generator_typeIdInsideObject() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("nested");
            gen.writeTypeId("InnerType");  // NOT at root — covers false branch of inRoot()
            gen.writeStartObject();
            gen.writeName("x");
            gen.writeNumber(1);
            gen.writeEndObject();
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 array of two objects — covers 2nd-object array path ──
    @Test
    public void generator_arrayOfTwoObjects() throws Exception {
        ContainerWithList container = new ContainerWithList();
        container.setSomeField("test");
        TestObject obj1 = new TestObject();
        obj1.setStringField("first");
        obj1.setIntField(1);
        TestObject obj2 = new TestObject();
        obj2.setStringField("second");
        obj2.setIntField(2);
        container.addListItem(obj1);
        container.addListItem(obj2);
        String result = ODINMapper3.builder().deactivateDefaultTyping().build().writeValueAsString(container);
        assertNotNull(result);
    }

    // ── ODINGenerator3 STATUS_OK_AFTER_SPACE — two root values ───────────────
    @Test
    public void generator_twoRootValues() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        StringWriter sw = new StringWriter();
        try (ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), sw)) {
            gen.writeStartObject();
            gen.writeName("a");
            gen.writeNumber(1);
            gen.writeEndObject();
            // second root value triggers STATUS_OK_AFTER_SPACE
            gen.writeStartObject();
            gen.writeName("b");
            gen.writeNumber(2);
            gen.writeEndObject();
        }
        assertNotNull(sw.toString());
    }

    // ── ODINGenerator3 STATUS_EXPECT_NAME — write value where name expected ──
    @Test
    public void generator_writeValueWhereNameExpected_throws() throws Exception {
        ODINFactory3 factory = new ODINFactory3();
        ODINGenerator3 gen = (ODINGenerator3) factory.createGenerator(ObjectWriteContext.empty(), new StringWriter());
        boolean threw = false;
        try {
            gen.writeStartObject();
            gen.writeNumber(42); // context expects NAME, triggers STATUS_EXPECT_NAME
        } catch (Exception e) {
            threw = true;
        } finally {
            try { gen.close(); } catch (Exception ignored) {}
        }
        assertTrue(threw);
    }
}
