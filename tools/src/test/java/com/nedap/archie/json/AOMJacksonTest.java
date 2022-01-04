package com.nedap.archie.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.primitives.ConstraintStatus;
import com.nedap.archie.aom.rmoverlay.VisibilityType;
import com.nedap.archie.base.Interval;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constraint;
import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.ModelReference;
import com.nedap.archie.rules.OperatorKind;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;
import org.threeten.extra.PeriodDuration;

import java.io.InputStream;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * A test that tests JSON parsing of Archetypes using Jackson
 *
 * Created by pieter.bos on 06/07/16.
 */
public class AOMJacksonTest {



    @Test
    public void parseDeliriumObservationScreening() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("delirium_observation_screening.json")) {
            Archetype archetype = JacksonUtil.getObjectMapper().readValue(stream, Archetype.class);
            System.out.println(archetype);
            assertTrue(archetype.getGenerated());
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

    @Test
    public void roundTripDeliriumObservationScreening() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("delirium_observation_screening.json")) {
            Archetype archetype = JacksonUtil.getObjectMapper().readValue(stream, Archetype.class);
            String reserialized = JacksonUtil.getObjectMapper().writeValueAsString(archetype);
            System.out.println(reserialized);
            JacksonUtil.getObjectMapper().readValue(reserialized, Archetype.class);
        }
    }

    @Test
    public void motricityIndex() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream( "/com/nedap/archie/rules/evaluation/openEHR-EHR-OBSERVATION.motricity_index.v1.0.0.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            String serialized = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).writeValueAsString(archetype);
            System.out.println(serialized);
            assertTrue(serialized.contains("EXPR_BINARY_OPERATOR"));
            assertTrue(serialized.contains("EXPR_ARCHETYPE_REF"));
        }
    }

    @Test
    public void motricityIndexOldFormat() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream( "/com/nedap/archie/rules/evaluation/openEHR-EHR-OBSERVATION.motricity_index.v1.0.0.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
            config.setStandardsCompliantExpressionClassNames(false);
            String serialized = JacksonUtil.getObjectMapper(config).writeValueAsString(archetype);
            System.out.println(serialized);
            assertTrue(serialized.contains("\"BINARY_OPERATOR\""));
            assertTrue(serialized.contains("\"MODEL_REFERENCE\""));
            ArchieJacksonConfiguration newConfig = ArchieJacksonConfiguration.createStandardsCompliant();
            newConfig.setStandardsCompliantExpressionClassNames(true);
            Archetype parsedArchetype = JacksonUtil.getObjectMapper(config).readValue(serialized, Archetype.class);
        }
    }

    @Test
    public void archetypeSlot() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream( "/basic.adl")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
            String serialized = objectMapper.writeValueAsString(archetype);
            //System.out.println(serialized);
            assertTrue(serialized.contains("\"EXPR_BINARY_OPERATOR\""));
            assertTrue(serialized.contains("\"EXPR_ARCHETYPE_REF\""));
            assertArchetypeSlot(objectMapper, serialized);
        }
    }

    private void assertArchetypeSlot(ObjectMapper objectMapper, String serialized) throws JsonProcessingException {
        Archetype parsedArchetype = objectMapper.readValue(serialized, Archetype.class);
        ArchetypeSlot slot = parsedArchetype.itemAtPath("/content[id8]");
        BinaryOperator operator = (BinaryOperator) slot.getIncludes().get(0).getExpression();
        assertEquals(OperatorKind.matches, operator.getOperator());
        assertEquals("archetype_id/value", ((ModelReference) operator.getLeftOperand()).getPath());
        CString idConstraint = (CString) ((Constraint) operator.getRightOperand()).getItem();
        assertEquals("/openEHR-EHR-INSTRUCTION\\.medication\\.v1/", idConstraint.getConstraint().get(0));
    }

    @Test
    public void archetypeSlotOldExpressionClassNames() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream( "/basic.adl")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
            config.setStandardsCompliantExpressionClassNames(false);
            ObjectMapper objectMapper = JacksonUtil.getObjectMapper(config);
            String serialized = objectMapper.writeValueAsString(archetype);
            //System.out.println(serialized);
            assertFalse(serialized.contains("EXPR_BINARY_OPERATOR"));
            assertFalse(serialized.contains("EXPR_ARCHETYPE_REF"));
            assertTrue(serialized.contains("\"BINARY_OPERATOR\""));
            assertTrue(serialized.contains("\"MODEL_REFERENCE\""));
            assertArchetypeSlot(objectMapper, serialized);
            //and it should be parsable with the new syntax as well
            assertArchetypeSlot(JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()), serialized);

        }
    }


    @Test
    public void cDuration() throws Exception {
        CDuration cDuration = new CDuration();
        cDuration.addConstraint(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), Duration.of(10, ChronoUnit.SECONDS)));
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        String cDurationJson = objectMapper.writeValueAsString(cDuration);
        assertTrue(cDurationJson.contains("-PT10H"));
        assertTrue(cDurationJson.contains("PT10S"));
        System.out.println(cDurationJson);

        CDuration parsedDuration = objectMapper.readValue(cDurationJson, CDuration.class);
        assertEquals(Lists.newArrayList(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), Duration.of(10, ChronoUnit.SECONDS))), parsedDuration.getConstraint());
    }

    @Test
    public void cDurationPeriodDuration() throws Exception {
        CDuration cDuration = new CDuration();
        PeriodDuration tenYearsTenSeconds = PeriodDuration.of(Period.of(10, 0, 0), Duration.of(10, ChronoUnit.SECONDS));
        cDuration.addConstraint(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), tenYearsTenSeconds));
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        String cDurationJson = objectMapper.writeValueAsString(cDuration);
        assertTrue(cDurationJson.contains("-PT10H"));
        assertTrue(cDurationJson.contains("P10YT10S"));
        System.out.println(cDurationJson);

        CDuration parsedDuration = objectMapper.readValue(cDurationJson, CDuration.class);
        assertEquals(Lists.newArrayList(new Interval<>(Duration.of(-10, ChronoUnit.HOURS), tenYearsTenSeconds)), parsedDuration.getConstraint());
    }

    @Test
    public void cTerminologyCode() throws Exception {
        CTerminologyCode cTermCode = new CTerminologyCode();
        cTermCode.setConstraint(Lists.newArrayList("ac23"));
        cTermCode.setConstraintStatus(ConstraintStatus.PREFERRED);
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant());
        String json = objectMapper.writeValueAsString(cTermCode);

        assertTrue(json.contains("\"constraint_status\" : \"preferred\""));
        CTerminologyCode parsedTermCode = objectMapper.readValue(json, CTerminologyCode.class);
        assertEquals(cTermCode.getConstraint(), parsedTermCode.getConstraint());
        assertEquals(ConstraintStatus.PREFERRED, parsedTermCode.getConstraintStatus());
    }

    @Test
    public void rmOverlay() throws Exception {
        Archetype archetype = TestUtil.parseFailOnErrors("/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.to_flatten_parent_with_overlay.v1.0.0.adls");
        String json = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).writeValueAsString(archetype);

        Archetype parsed = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(json, Archetype.class);
        assertEquals(VisibilityType.HIDE, parsed.getRmOverlay().getRmVisibility().get("/subject").getVisibility());
        assertEquals("at12", parsed.getRmOverlay().getRmVisibility().get("/subject").getAlias().getCodeString());

        assertTrue(json.contains("  \"rm_overlay\" : {\n" +
                "    \"rm_visibility\" : {\n" +
                "      \"/subject\" : {\n" +
                "        \"visibility\" : \"hide\",\n" +
                "        \"alias\" : {\n" +
                "          \"@type\" : \"TerminologyCode\",\n" +
                "          \"terminology_id\" : \"local\",\n" +
                "          \"code_string\" : \"at12\",\n" +
                "          \"terminology_id_string\" : \"local\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"/data[id2]/events[id3]/data[id4]/items[id5]\" : {\n" +
                "        \"visibility\" : \"show\"\n" +
                "      },\n" +
                "      \"/data[id2]/events[id3]/data[id4]/items[id6]\" : {\n" +
                "        \"visibility\" : \"show\"\n" +
                "      }\n" +
                "    }\n" +
                "  },"));
    }
}
