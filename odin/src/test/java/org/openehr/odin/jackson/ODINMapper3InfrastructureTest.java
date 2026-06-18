package org.openehr.odin.jackson;

import org.junit.jupiter.api.Test;
import org.openehr.odin.jackson.testclasses.ContainerWithList;
import org.openehr.odin.jackson.testclasses.TestObject;
import org.openehr.odin.jackson3.ODINFactory3;
import org.openehr.odin.jackson3.ODINMapper3;
import org.openehr.odin.jackson3.ODINPrettyPrinter3;

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
}
