package com.nedap.archie.serializer.adl;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.MultiplicityInterval;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author markopi
 */
public class ADLDefinitionSerializerTest {
    private static Archetype archetypePrimitives;


    @BeforeClass
    public static void setupClass() throws IOException {
        archetypePrimitives = load("openEHR-TEST_PKG-WHOLE.primitive_types.v1.adls");
    }

    private static Archetype load(String resourceName) throws IOException {
        return new ADLParser().parse(ADLDefinitionSerializerTest.class.getResourceAsStream(resourceName));
    }

    @Test
    public void serializeCString() {
        assertPrimitive("string_attr1", "\"something\"");
        assertPrimitive("string_attr2", "/this|that|something else/");
        assertPrimitive("string_attr3", "/cardio.*/");
        assertPrimitive("string_attr5", "\"and\", \"something\", \"else\"");

        assertPrimitive("string_attr102", "/this|that|something else/; \"something else\"");
        assertPrimitive("string_attr105", "\"and\", \"something\", \"else\"; \"something\"");
    }

    @Test
    public void serializeCBoolean() {
        assertPrimitive("boolean_attr1", "True");
        assertPrimitive("boolean_attr2", "False");
        assertPrimitive("boolean_attr3", "True, False");
        assertPrimitive("boolean_attr103", "True, False; True");
    }

    @Test
    public void serializeCInteger() {
        assertPrimitive("integer_attr1", "55");
        assertPrimitive("integer_attr2", "10, 20, 30");
        assertPrimitive("integer_attr3", "|0..100|");
        assertPrimitive("integer_attr4", "|>0..100|");
        assertPrimitive("integer_attr5", "|0..<100|");
        assertPrimitive("integer_attr6", "|>0..<100|");
        assertPrimitive("integer_attr7", "|>10|");
        assertPrimitive("integer_attr8", "|<10|");
        assertPrimitive("integer_attr9", "|>=10|");
        assertPrimitive("integer_attr10", "|<=10|");
        assertPrimitive("integer_attr11", "|-10..-5|");
        assertPrimitive("integer_attr12", "10");

        assertPrimitive("integer_attr102", "10, 20, 30; 20");
        assertPrimitive("integer_attr106", "|>0..<100|; 56");
    }

    @Test
    public void serializeCReal() {
        assertPrimitive("real_attr1", "100.0");
        assertPrimitive("real_attr2", "10.0, 20.0, 30.0");
        assertPrimitive("real_attr3", "|0.0..100.0|");
        assertPrimitive("real_attr4", "|>0.0..100.0|");
        assertPrimitive("real_attr5", "|0.0..<100.0|");
        assertPrimitive("real_attr6", "|>0.0..<100.0|");
        assertPrimitive("real_attr7", "|>10.0|");
        assertPrimitive("real_attr8", "|<10.0|");
        assertPrimitive("real_attr9", "|>=10.0|");
        assertPrimitive("real_attr10", "|<=10.0|");
        assertPrimitive("real_attr11", "|-10.0..-5.0|");
        assertPrimitive("real_attr12", "10.0");

        assertPrimitive("real_attr102", "10.0, 20.0, 30.0; 20.0");
        assertPrimitive("real_attr106", "|>0.0..<100.0|; 56.0");
    }

    @Test
    public void serializeCDate() {
        assertPrimitive("date_attr1", "yyyy-mm-dd");
        assertPrimitive("date_attr2", "yyyy-??-??");
        assertPrimitive("date_attr3", "yyyy-mm-??");
        assertPrimitive("date_attr4", "yyyy-??-XX");
        assertPrimitive("date_attr5", "1983-12-25");
        assertPrimitive("date_attr6", "2000-01-01");
        assertPrimitive("date_attr7", "2000-01-01");
        assertPrimitive("date_attr8", "|2000-01-01..2000-02-01|");
        assertPrimitive("date_attr9", "|>2000-01-01..2000-02-01|");
        assertPrimitive("date_attr10", "|2000-01-01..<2000-02-01|");
        assertPrimitive("date_attr11", "|>2000-01-01..<2000-02-01|");

        assertPrimitive("date_attr101", "yyyy-mm-dd; 2001-02-03");
        assertPrimitive("date_attr108", "|2000-01-01..2000-02-01|; 2000-01-10");
    }

    @Test
    public void serializeCTime() {
        assertPrimitive("time_attr1", "hh:mm:ss");
        assertPrimitive("time_attr2", "hh:mm:??");
        assertPrimitive("time_attr3", "hh:mm:XX");
        assertPrimitive("time_attr4", "hh:??:??");
        assertPrimitive("time_attr5", "hh:??:XX");
        assertPrimitive("time_attr6", "22:00:05");
        assertPrimitive("time_attr7", "00:00:59");
        assertPrimitive("time_attr8", "00:00:59");
        assertPrimitive("time_attr9", "|01:00..02:00|");
        assertPrimitive("time_attr10", "|>01:00..02:00|");
        assertPrimitive("time_attr11", "|01:00..<02:00|");
        assertPrimitive("time_attr12", "|>01:00..<02:00|");

        assertPrimitive("time_attr101", "hh:mm:ss; 14:34:20");
        assertPrimitive("time_attr109", "|01:00..02:00|; 01:30:12");
    }

    @Test
    public void serializeCDateTime() {
        assertPrimitive("date_time_attr1", "yyyy-mm-ddThh:mm:ss");
        assertPrimitive("date_time_attr2", "yyyy-mm-ddThh:mm:??");
        assertPrimitive("date_time_attr3", "yyyy-mm-ddThh:mm:XX");
        assertPrimitive("date_time_attr4", "yyyy-mm-ddThh:??:??");
        assertPrimitive("date_time_attr5", "yyyy-mm-ddThh:??:XX");
        assertPrimitive("date_time_attr6", "yyyy-??-??T??:??:??");
        assertPrimitive("date_time_attr7", "1983-12-25T22:00:05");
        assertPrimitive("date_time_attr8", "2000-01-01T00:00:59");
        assertPrimitive("date_time_attr9", "2000-01-01T00:00:59");
        assertPrimitive("date_time_attr10", "|2000-01-01T01:00..2000-01-01T02:00|");
        assertPrimitive("date_time_attr11", "|>2000-01-01T01:00..2000-01-01T02:00|");
        assertPrimitive("date_time_attr12", "|2000-01-01T01:00..<2000-01-01T02:00|");
        assertPrimitive("date_time_attr13", "|>2000-01-01T01:00..<2000-01-01T02:00|");

        assertPrimitive("date_time_attr101", "yyyy-mm-ddThh:mm:ss; 2014-10-28T05:12:56");
        assertPrimitive("date_time_attr110", "|2000-01-01T01:00..2000-01-01T02:00|; 2000-01-01T01:32:33");
    }

    @Test
    public void serializeDuration() {
        // java.time.Duration has no notion of weeks, so the value is always serialized in days.
        // relevant tests were commented out for now

        assertPrimitive("duration_attr1", "Pw");
        assertPrimitive("duration_attr2", "Pmw");
        assertPrimitive("duration_attr3", "PWD");
        assertPrimitive("duration_attr4", "PD");
        assertPrimitive("duration_attr5", "Pym");
        assertPrimitive("duration_attr6", "PdThms");
        assertPrimitive("duration_attr7", "PTs");
        assertPrimitive("duration_attr8", "PThm");
        assertPrimitive("duration_attr9", "PT0S");
        assertPrimitive("duration_attr10", "PT0S");
        assertPrimitive("duration_attr11", "P1D");
//        assertPrimitive("duration_attr12", "|P38W..P39W4D|");
//        assertPrimitive("duration_attr13", "|>P38W..P39W4D|");
//        assertPrimitive("duration_attr14", "|P38W..<P39W4D|");
//        assertPrimitive("duration_attr15", "|>P38W..<P39W4D|");
        assertPrimitive("duration_attr16", "PT2H5M");
        assertPrimitive("duration_attr17", "PT1H55M");
        assertPrimitive("duration_attr18", "|<=PT1H|");
        assertPrimitive("duration_attr19", "PT1H30M");
        assertPrimitive("duration_attr20", "Pw/PT0S");
        assertPrimitive("duration_attr21", "Pmw/PT0S");
        assertPrimitive("duration_attr22", "PWD/PT0S");
        assertPrimitive("duration_attr23", "PD/PT0S");
        assertPrimitive("duration_attr24", "Pym/PT0S");
        assertPrimitive("duration_attr25", "PdThms/PT0S");
        assertPrimitive("duration_attr26", "PTs/PT0S");
        assertPrimitive("duration_attr27", "PThm/PT0S");
//        assertPrimitive("duration_attr28", "Pw/|P38W..P39W4D|");
//        assertPrimitive("duration_attr29", "Pmw/|P38W..P39W4D|");
//        assertPrimitive("duration_attr30", "PWD/|P38W..P39W4D|");
//        assertPrimitive("duration_attr31", "PD/|P38W..P39W4D|");
//        assertPrimitive("duration_attr32", "Pym/|P38W..P39W4D|");
//        assertPrimitive("duration_attr33", "PdThms/|P38W..P39W4D|");
//        assertPrimitive("duration_attr34", "PTs/|P38W..P39W4D|");
//        assertPrimitive("duration_attr35", "PThm/|P38W..P39W4D|");
        assertPrimitive("duration_attr36", "|>=PT0S|");

        assertPrimitive("duration_attr104", "PD; P4D");
        assertPrimitive("duration_attr118", "|<=PT1H|; PT30M");
        assertPrimitive("duration_attr128", "Pw/|P266D..P277D|; P268D"); // duration in weeks is serialized as days

    }

    @Test
    public void serializeTerminologyCode() {
        assertPrimitive("terminology_code_attr1", "[at1001]");
        assertPrimitive("terminology_code_attr2", "[ac1]");

        assertPrimitive("terminology_code_attr102", "[ac1; at1003]");
    }

    @Test
    public void serializeTupleOrdinal() throws Exception {
        Archetype archetype = loadRoot("adl2-tests/features/aom_structures/tuples/CIMI-CORE-ITEM_GROUP.real_ordinal.v1.0.0.adls");
        CComplexObject ordinal = archetype.itemAtPath("/item[id2]/value[id3]");
        String serialized = serializeConstraint(ordinal);
        assertThat(serialized, equalTo("\n" +
                "    ORDINAL[id3] matches {\n" +
                "        [symbol, value] matches {\n" +
                "            [{[at1]}, {0.0}],\n" +
                "            [{[at2]}, {1.0}],\n" +
                "            [{[at3]}, {2.0}]\n" +
                "        }\n" +
                "    }"));
    }

    @Test
    public void serializeTupleDvQuantity() throws Exception {
        Archetype archetype = loadRoot("adl2-tests/features/aom_structures/tuples/openehr-test_pkg-SOME_TYPE.dv_quantity_tuple.v1.0.0.adls");
        CComplexObject ordinal = archetype.itemAtPath("/clinical_quantity_attr_1[id2]");

        String serialized = serializeConstraint(ordinal);
        assertThat(serialized, equalTo("\n" +
                "    DV_QUANTITY[id2] matches {\n" +
                "        property matches {[at1]}\n" +
                "        [units, magnitude] matches {\n" +
                "            [{\"C\"}, {|>=4.0|}],\n" +
                "            [{\"F\"}, {|>=40.0|}]\n" +
                "        }\n" +
                "    }"));


    }

    @Test
    public void serializeArchetypeSlot() throws Exception {
        Archetype archetype = loadRoot("adl2-tests/features/aom_structures/slots/openEHR-EHR-SECTION.slot_include_any_exclude_empty.v1.0.0.adls");
        ArchetypeSlot slot = archetype.itemAtPath("/items[id2]");
        String serialized = serializeConstraint(slot);
        assertThat(serialized, equalTo("\n    allow_archetype OBSERVATION[id2] occurrences matches {0..1} matches {     -- Vital signs\n" +
                "        include\n" +
                "            archetype_id/value matches {/.*/}\n" +
                "    }"));


        archetype = load("openEHR-EHR-CLUSTER.device.v1.adls");
        slot = archetype.itemAtPath("/items[id10]");
        serialized = serializeConstraint(slot);
        assertThat(serialized, equalTo("\n    allow_archetype CLUSTER[id10] occurrences matches {0..*} matches {     -- Properties\n" +
                "        include\n" +
                "            archetype_id/value matches {/openEHR-EHR-CLUSTER\\.dimensions(-a-zA-Z0-9_]+)*\\.v1|openEHR-EHR-CLUSTER\\.catheter(-a-zA-Z0-9_]+)*\\.v1/}\n" +
                "    }"));


        archetype = loadRoot("adl2-tests/features/aom_structures/slots/openEHR-EHR-SECTION.slot_include_empty_exclude_non_any.v1.0.0.adls");
        slot = archetype.itemAtPath("/items[id2]");
        serialized = serializeConstraint(slot);
        assertThat(serialized, equalTo("\n    allow_archetype OBSERVATION[id2] occurrences matches {0..1} matches {     -- Vital signs\n" +
                "        exclude\n" +
                "            archetype_id/value matches {/openEHR-EHR-OBSERVATION\\.blood_pressure([a-zA-Z0-9_]+)*\\.v1/}\n" +
                "    }"));
    }

    @Test
    public void serializeCArchetypeRoot() throws IOException {
        Archetype archetype = loadRoot("adl2-tests/features/aom_structures/use_archetype/openEHR-EHR-COMPOSITION.ext_ref.v1.0.0.adls");

        List<CObject> archetypeRoots = archetype.getDefinition().getAttribute("content").getChildren();

        assertThat(serializeConstraint(archetypeRoots.get(0)).trim(),
                equalTo("use_archetype SECTION[id2, openEHR-EHR-SECTION.section_parent.v1] occurrences matches {0..1}"));
        assertThat(serializeConstraint(archetypeRoots.get(1)).trim(),
                equalTo("use_archetype OBSERVATION[id3, openEHR-EHR-OBSERVATION.spec_test_obs.v1] occurrences matches {1}"));
    }

    @Test
    public void serializeCComplexObjectProxy() throws IOException {
        Archetype archetype = loadRoot("adl2-tests/features/aom_structures/use_node/openEHR-DEMOGRAPHIC-PERSON.use_node_occurrences.v1.0.0.adls");

        CObject parentCObj = archetype.getDefinition().itemAtPath("/contacts[id10]");
        List<CObject> ccobjProxies = parentCObj.getAttribute("addresses").getChildren();

        assertThat(serializeConstraint(ccobjProxies.get(0)).trim(),
                equalTo("use_node ADDRESS[id11] /contacts[id6]/addresses[id7]"));
        assertThat(serializeConstraint(ccobjProxies.get(1)).trim(),
                equalTo("use_node ADDRESS[id12] /contacts[id6]/addresses[id8]"));
        assertThat(serializeConstraint(ccobjProxies.get(2)).trim(),
                equalTo("use_node ADDRESS[id13] occurrences matches {1..3} /contacts[id6]/addresses[id9]"));
    }

    @Test
    public void serializeOccurrencesTest() {
        checkOccurrences("5..*", MultiplicityInterval.createUpperUnbounded(5));
        checkOccurrences("1", MultiplicityInterval.createMandatory());
        checkOccurrences("0..1", MultiplicityInterval.createOptional());
        checkOccurrences("0", MultiplicityInterval.createProhibited());
        checkOccurrences("10..50", MultiplicityInterval.createBounded(10, 50));
        checkOccurrences("0..5", new MultiplicityInterval(null, false, true, 5, true, false));
        checkOccurrences("4..5", new MultiplicityInterval(3, false, false, 5, true, false));
        checkOccurrences("4", new MultiplicityInterval(3, false, false, 5, false, false));
        checkOccurrences("4", new MultiplicityInterval(4, true, false, 5, false, false));
        checkOccurrences("4", new MultiplicityInterval(3, false, false, 4, true, false));

    }

    private void checkOccurrences(String expected, MultiplicityInterval interval) {
        ADLStringBuilder adlStringBuilder = new ADLStringBuilder();
        ArchetypeSerializeUtils.buildOccurrences(adlStringBuilder, interval);
        assertEquals(expected, adlStringBuilder.toString());
    }


    private void assertPrimitive(String attributeName, String expected) {
        CObject cons = archetypePrimitives.getDefinition().getAttribute(attributeName).getChildren().get(0);

        String actual = serializeConstraint(cons);

        assertThat(actual, equalTo(expected));
    }

    private String serializeConstraint(CObject cons) {
        ADLDefinitionSerializer serializer = new ADLDefinitionSerializer(new ADLStringBuilder());
        serializer.appendCObject(cons);
        return serializer.getBuilder().toString();
    }

    private Archetype loadRoot(String resourceName) throws IOException {
        return new ADLParser().parse(ADLArchetypeSerializerTest.class.getClassLoader().getResourceAsStream(resourceName));
    }

}