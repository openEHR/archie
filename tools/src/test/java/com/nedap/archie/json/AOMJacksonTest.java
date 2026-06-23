package com.nedap.archie.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nedap.archie.json3.JacksonUtil3;
import com.nedap.archie.json.JacksonTestMappers.JsonMapper;
import com.google.common.collect.Lists;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.primitives.ConstraintStatus;
import com.nedap.archie.aom.rmoverlay.VisibilityType;
import com.nedap.archie.base.Interval;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constraint;
import com.nedap.archie.rules.ModelReference;
import com.nedap.archie.rules.OperatorKind;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import com.nedap.archie.testutil.TestUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openehr.referencemodels.BuiltinReferenceModels;
import org.threeten.extra.PeriodDuration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A test that tests JSON parsing of Archetypes using Jackson
 *
 * Created by pieter.bos on 06/07/16.
 */
public class AOMJacksonTest {

    interface JsonMapperFactory extends JacksonTestMappers.JsonMapperFactory {
        JsonMapper createNoIndent(ArchieJacksonConfiguration config);
    }

    private static Stream<Arguments> mappers() {
        JsonMapperFactory j2 = new JsonMapperFactory() {
            public JsonMapper create() { return JacksonTestMappers.wrap(JacksonUtil.getObjectMapper()); }
            public JsonMapper create(ArchieJacksonConfiguration c) { return JacksonTestMappers.wrap(JacksonUtil.getObjectMapper(c)); }
            public JsonMapper createNoIndent(ArchieJacksonConfiguration c) {
                ObjectMapper m = new ObjectMapper();
                JacksonUtil.configureObjectMapper(m, c);
                m.disable(SerializationFeature.INDENT_OUTPUT);
                return JacksonTestMappers.wrap(m);
            }
        };
        JsonMapperFactory j3 = new JsonMapperFactory() {
            public JsonMapper create() { return JacksonTestMappers.wrap(JacksonUtil3.getObjectMapper()); }
            public JsonMapper create(ArchieJacksonConfiguration c) { return JacksonTestMappers.wrap(JacksonUtil3.getObjectMapper(c)); }
            public JsonMapper createNoIndent(ArchieJacksonConfiguration c) {
                var builder = tools.jackson.databind.json.JsonMapper.builder();
                JacksonUtil3.configureBuilder(builder, c);
                builder.disable(tools.jackson.databind.SerializationFeature.INDENT_OUTPUT);
                return JacksonTestMappers.wrap(builder.build());
            }
        };
        return Stream.of(
                Arguments.of(Named.of("Jackson 2", j2)),
                Arguments.of(Named.of("Jackson 3", j3))
        );
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void parseDeliriumObservationScreening(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("delirium_observation_screening.json")) {
            Archetype archetype = factory.create(ArchieJacksonConfiguration.createLegacyConfiguration()).readValue(stream, Archetype.class);
            System.out.println(archetype);
            assertTrue(((AuthoredArchetype) archetype).getGenerated());
            assertThat(archetype.getArchetypeId().getFullId(), is("openEHR-EHR-GENERIC_ENTRY.delirium_observation_screening.v1.0.0"));
            assertThat(archetype.getDefinition().getRmTypeName(), is("GENERIC_ENTRY"));
            CComplexObject cluster = archetype.getDefinition().itemAtPath("/data[id2]/items[id3]/items[id4]");
            assertThat(cluster.getRmTypeName(), is("CLUSTER"));
            assertThat(cluster.getNodeId(), is("id4"));
            assertThat(cluster.getParent(), is(equalTo(archetype.getDefinition().itemAtPath("/data[id2]/items[id3]/items"))));
            assertEquals("Groep1", cluster.getTerm().getText());
            System.out.println(ADLArchetypeSerializer.serialize(archetype));
        }
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void roundTripDeliriumObservationScreening(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("delirium_observation_screening.json")) {
            JsonMapper mapper = factory.create(ArchieJacksonConfiguration.createLegacyConfiguration());
            Archetype archetype = mapper.readValue(stream, Archetype.class);
            String reserialized = mapper.writeValueAsString(archetype);
            mapper.readValue(reserialized, Archetype.class);
        }
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void parseLifecycleStateStringTest(JsonMapperFactory factory) throws Exception {
        ResourceDescription resourceDescription = factory.create(ArchieJacksonConfiguration.createLegacyConfiguration())
                .readValue("{ \"lifecycle_state\" :\"unmanaged\" }", ResourceDescription.class);
        assertEquals("unmanaged", resourceDescription.getLifecycleState());
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void parseLifecycleStateTerminologyCodeTest(JsonMapperFactory factory) throws Exception {
        ResourceDescription resourceDescription = factory.create(ArchieJacksonConfiguration.createLegacyConfiguration())
                .readValue("{ \"lifecycle_state\" : { \"code_string\" : \"unmanaged\" }}", ResourceDescription.class);
        assertEquals("unmanaged", resourceDescription.getLifecycleState());
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void parseLifecycleStateTerminologyCodeCodeStringNullTest(JsonMapperFactory factory) throws Exception {
        ResourceDescription resourceDescription = factory.create(ArchieJacksonConfiguration.createLegacyConfiguration())
                .readValue("{ \"lifecycle_state\" : { \"code_string\" : null }}", ResourceDescription.class);
        assertNull(resourceDescription.getLifecycleState());
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void parseLifecycleStateTerminologyCodeNoCodeStringTest(JsonMapperFactory factory) throws Exception {
        ResourceDescription resourceDescription = factory.create(ArchieJacksonConfiguration.createLegacyConfiguration())
                .readValue("{ \"lifecycle_state\" : { \"placeholder\" : \"placeholder\" }}", ResourceDescription.class);
        assertNull(resourceDescription.getLifecycleState());
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void parseLifecycleStateArrayTest(JsonMapperFactory factory) throws Exception {
        ResourceDescription resourceDescription = factory.create(ArchieJacksonConfiguration.createLegacyConfiguration())
                .readValue("{ \"lifecycle_state\" : [] }", ResourceDescription.class);
        assertNull(resourceDescription.getLifecycleState());
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void motricityIndex(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/com/nedap/archie/rules/evaluation/openEHR-EHR-OBSERVATION.motricity_index.v1.0.0.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            String serialized = factory.create(ArchieJacksonConfiguration.createStandardsCompliant()).writeValueAsString(archetype);
            assertTrue(serialized.contains("EXPR_BINARY_OPERATOR"));
            assertTrue(serialized.contains("\"operator_def\" : {\n" +
                    "          \"_type\" : \"OPERATOR_DEF_BUILTIN\",\n" +
                    "          \"identifier\" : \"op_plus\"\n" +
                    "        }"));
            assertTrue(serialized.contains("EXPR_ARCHETYPE_REF"));
            assertTrue(serialized.contains("\"rules\" : [ {"));
            Archetype parsedArchetype = factory.create().readValue(serialized, Archetype.class);
            assertEquals(8, parsedArchetype.getRules().getRules().size());

            ArchieJacksonConfiguration newConfig = ArchieJacksonConfiguration.createStandardsCompliant();
            newConfig.setStandardsCompliantExpressions(false);
            Archetype parsedArchetype2 = factory.create(newConfig).readValue(serialized, Archetype.class);
            assertEquals(8, parsedArchetype2.getRules().getRules().size());
        }
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void motricityIndexRulesOldFormatAsList(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("motricity_index_legacy_format_rules_section_as_array.json")) {
            ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
            config.setStandardsCompliantExpressions(false);
            Archetype parsedArchetype = factory.create(config).readValue(stream, Archetype.class);
            assertEquals(8, parsedArchetype.getRules().getRules().size());
        }
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void motriciyIndexJavascriptFormat(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/com/nedap/archie/rules/evaluation/openEHR-EHR-OBSERVATION.motricity_index.v1.0.0.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            String serialized = factory.create(ArchieJacksonConfiguration.createConfigForJavascriptUsage()).writeValueAsString(archetype);
            assertTrue(serialized.contains("EXPR_BINARY_OPERATOR"));
            assertTrue(serialized.contains("EXPR_ARCHETYPE_REF"));
            assertTrue(serialized.contains("\"rules\" : [ {"));
            Archetype parsedArchetype = factory.create().readValue(serialized, Archetype.class);
            assertEquals(8, parsedArchetype.getRules().getRules().size());

            ArchieJacksonConfiguration newConfig = ArchieJacksonConfiguration.createStandardsCompliant();
            newConfig.setStandardsCompliantExpressions(false);
            Archetype parsedArchetype2 = factory.create(newConfig).readValue(serialized, Archetype.class);
            assertEquals(8, parsedArchetype2.getRules().getRules().size());
        }
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void motricityIndexOldFormat(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/com/nedap/archie/rules/evaluation/openEHR-EHR-OBSERVATION.motricity_index.v1.0.0.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
            config.setStandardsCompliantExpressions(false);
            String serialized = factory.create(config).writeValueAsString(archetype);
            System.out.println(serialized);
            assertTrue(serialized.contains("\"BINARY_OPERATOR\""));
            assertTrue(serialized.contains("\"operator\" : \"eq\","));
            assertTrue(serialized.contains("\"MODEL_REFERENCE\""));
            assertTrue(serialized.contains("\"rules\" : {"));
            Archetype parsedArchetype = factory.create(config).readValue(serialized, Archetype.class);
            assertEquals(8, parsedArchetype.getRules().getRules().size());
        }
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void archetypeSlot(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/basic.adl")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            JsonMapper mapper = factory.createNoIndent(ArchieJacksonConfiguration.createStandardsCompliant());
            String serialized = mapper.writeValueAsString(archetype);
            assertTrue(serialized.contains("\"EXPR_BINARY_OPERATOR\""));
            assertTrue(serialized.contains("\"EXPR_ARCHETYPE_REF\""));
            assertTrue(serialized.contains("\"operator_def\":{\"_type\":\"OPERATOR_DEF_BUILTIN\",\"identifier\":\"op_matches\"}"));
            assertArchetypeSlot(mapper, serialized);
        }
    }

    private void assertArchetypeSlot(JsonMapper mapper, String serialized) throws Exception {
        Archetype parsedArchetype = mapper.readValue(serialized, Archetype.class);
        ArchetypeSlot slot = parsedArchetype.itemAtPath("/content[id8]");
        BinaryOperator operator = (BinaryOperator) slot.getIncludes().get(0).getExpression();
        assertEquals(OperatorKind.matches, operator.getOperator());
        assertEquals("archetype_id/value", ((ModelReference) operator.getLeftOperand()).getPath());
        CString idConstraint = (CString) ((Constraint<?>) operator.getRightOperand()).getItem();
        assertEquals("/openEHR-EHR-INSTRUCTION\\.medication\\.v1/", idConstraint.getConstraint().get(0));
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void archetypeSlotOldExpressionClassNames(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/basic.adl")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
            config.setStandardsCompliantExpressions(false);
            JsonMapper mapper = factory.create(config);
            String serialized = mapper.writeValueAsString(archetype);
            System.out.println(serialized);
            assertFalse(serialized.contains("EXPR_BINARY_OPERATOR"));
            assertFalse(serialized.contains("EXPR_ARCHETYPE_REF"));
            assertTrue(serialized.contains("\"operator\" : \"matches\","));
            assertTrue(serialized.contains("\"BINARY_OPERATOR\""));
            assertTrue(serialized.contains("\"MODEL_REFERENCE\""));
            assertArchetypeSlot(mapper, serialized);
            assertArchetypeSlot(factory.create(ArchieJacksonConfiguration.createStandardsCompliant()), serialized);
        }
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void cDuration(JsonMapperFactory factory) throws Exception {
        CDuration cDuration = new CDuration();
        cDuration.addConstraint(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), Duration.of(10, ChronoUnit.SECONDS)));
        JsonMapper mapper = factory.create(ArchieJacksonConfiguration.createStandardsCompliant());
        String cDurationJson = mapper.writeValueAsString(cDuration);
        assertTrue(cDurationJson.contains("-PT10H"));
        assertTrue(cDurationJson.contains("PT10S"));
        System.out.println(cDurationJson);
        CDuration parsedDuration = mapper.readValue(cDurationJson, CDuration.class);
        assertEquals(Lists.newArrayList(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), Duration.of(10, ChronoUnit.SECONDS))), parsedDuration.getConstraint());
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void cDurationPeriodDuration(JsonMapperFactory factory) throws Exception {
        CDuration cDuration = new CDuration();
        PeriodDuration tenYearsTenSeconds = PeriodDuration.of(Period.of(10, 0, 0), Duration.of(10, ChronoUnit.SECONDS));
        cDuration.addConstraint(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), tenYearsTenSeconds));
        JsonMapper mapper = factory.create(ArchieJacksonConfiguration.createStandardsCompliant());
        String cDurationJson = mapper.writeValueAsString(cDuration);
        assertTrue(cDurationJson.contains("-PT10H"));
        assertTrue(cDurationJson.contains("P10YT10S"));
        System.out.println(cDurationJson);
        CDuration parsedDuration = mapper.readValue(cDurationJson, CDuration.class);
        assertEquals(Lists.newArrayList(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), tenYearsTenSeconds)), parsedDuration.getConstraint());
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void cTerminologyCode(JsonMapperFactory factory) throws Exception {
        CTerminologyCode cTermCode = new CTerminologyCode();
        cTermCode.setConstraint("ac23");
        cTermCode.setConstraintStatus(ConstraintStatus.PREFERRED);
        JsonMapper mapper = factory.create(ArchieJacksonConfiguration.createStandardsCompliant());
        String json = mapper.writeValueAsString(cTermCode);
        assertTrue(json.contains("\"constraint_status\" : \"preferred\""));
        assertTrue(json.contains("\"constraint\" : \"ac23\""));
        CTerminologyCode parsedTermCode = objectMapper.readValue(json, CTerminologyCode.class);
        assertEquals(cTermCode.getConstraint(), parsedTermCode.getConstraint());
        assertEquals(ConstraintStatus.PREFERRED, parsedTermCode.getConstraintStatus());
    }

    @Test
    public void backwardsCompatibilityCTerminologyCodeConstraintTypeFromJsonTest() throws Exception {
        String json = "{\n" +
                "  \"rm_type_name\" : \"terminology_code\",\n" +
                "  \"node_id\" : \"id9999\",\n" +
                "  \"constraint\" : [ \"ac23\" ]\n" +
                "}";
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        CTerminologyCode parsedTermCode = objectMapper.readValue(json, CTerminologyCode.class);
        assertEquals("ac23", parsedTermCode.getConstraint());
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void rmOverlay(JsonMapperFactory factory) throws Exception {
        Archetype archetype = TestUtil.parseFailOnErrors("/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.to_flatten_parent_with_overlay.v1.0.0.adls");
        JsonMapper mapper = factory.create(ArchieJacksonConfiguration.createStandardsCompliant());
        String json = mapper.writeValueAsString(archetype);
        Archetype parsed = mapper.readValue(json, Archetype.class);
        assertEquals(VisibilityType.HIDE, parsed.getRmOverlay().getRmVisibility().get("/subject").getVisibility());
        assertEquals("at12", parsed.getRmOverlay().getRmVisibility().get("/subject").getAlias().getCodeString());
        assertEquals("local", parsed.getRmOverlay().getRmVisibility().get("/subject").getAlias().getTerminologyId());
        assertEquals(VisibilityType.SHOW, parsed.getRmOverlay().getRmVisibility().get("/data[id2]/events[id3]/data[id4]/items[id5]").getVisibility());
        assertEquals(VisibilityType.SHOW, parsed.getRmOverlay().getRmVisibility().get("/data[id2]/events[id3]/data[id4]/items[id6]").getVisibility());
    }

    @ParameterizedTest
    @MethodSource("mappers")
    public void parseS2AomJson(JsonMapperFactory factory) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream("/S2/json/flat/s2-EHR-Action.medication.v1.2.2.json")) {
            Archetype parsed = factory.create(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(stream, Archetype.class);
            assertNotNull(parsed);
        }
    }

    @Disabled // for local testing
    @Test
    public void parseS2AomJsonAll() throws Exception {
        ObjectMapper mapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        Files.find(new File("../../ADL-exported/S2-S2_demo/json/flat").toPath(),
                        1,
                        (file, att) -> file.toString().endsWith(".json"))
                .forEach(file -> {
                    try (InputStream stream = Files.newInputStream(file)) {
                        Archetype parsed = mapper.readValue(stream, Archetype.class);
                        assertNotNull(parsed);
                    } catch (Exception e) {
                        System.out.println("Error parsing " + file.toString());
                        throw new RuntimeException(e);
                    }
                });
    }
}
