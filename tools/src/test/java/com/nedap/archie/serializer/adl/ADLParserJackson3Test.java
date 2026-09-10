package com.nedap.archie.serializer.adl;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.ADLParser3;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.json.ArchieRMObjectMapperProvider;
import com.nedap.archie.json3.ArchieRMObjectMapperProvider3;
import com.nedap.archie.json3.JacksonUtil3;
import com.nedap.archie.rminfo.*;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ADLParserJackson3Test {
    static List<AdlSerializationTestUtil.Case> cases() throws Exception {
        // Serialized overlay fragments require a containing template in the ADL grammar.
        // The template cases include these overlays. Standalone serialization is tested separately.
        return AdlSerializationTestUtil.cases().stream()
                .filter(c -> !(c.getArchetype() instanceof TemplateOverlay)).toList();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void parsedAdlMatchesJackson2(AdlSerializationTestUtil.Case corpusCase) throws Exception {
        String input = ADLArchetypeSerializer.serialize(corpusCase.getArchetype(), null,
                new ArchieRMObjectMapperProvider());
        Archetype expected = new ADLParser().parse(CharStreams.fromString(input));
        Archetype actual = new ADLParser3().parse(CharStreams.fromString(input));
        assertEquals(ADLArchetypeSerializer.serialize(expected),
                ADLArchetypeSerializer.serializeWithJackson3(actual, null, null));
        assertEquals(JacksonUtil3.getObjectMapper().writeValueAsString(expected),
                JacksonUtil3.getObjectMapper().writeValueAsString(actual));
    }

    @Test
    void defaultValuesUseJackson3Mappers() throws Exception {
        MetaModelProvider metadata = (publisher, rmPackage, release) -> {
            MetaModel model = BuiltinReferenceModels.getMetaModelProvider()
                    .getMetaModel(publisher, rmPackage, release);
            return new MetaModel(model.getModelInfoLookup(), model.getBmmModel(), model.getAomProfile()) {
                @Override
                public com.fasterxml.jackson.databind.ObjectMapper getJsonObjectMapper() {
                    throw new AssertionError("Jackson 2 JSON mapper requested");
                }
                @Override
                public com.fasterxml.jackson.databind.ObjectMapper getOdinInputObjectMapper() {
                    throw new AssertionError("Jackson 2 ODIN mapper requested");
                }
            };
        };
        ADLParser3 parser = new ADLParser3(new SimpleMetaModelProvider3(metadata,
                new ArchieRMObjectMapperProvider3()));
        Archetype expected;
        Archetype actual;
        try (InputStream input = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.default_values.v1.adls")) {
            expected = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(input);
        }
        try (InputStream input = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.default_values.v1.adls")) {
            actual = parser.parse(input);
        }
        assertEquals(JacksonUtil3.getObjectMapper().writeValueAsString(expected),
                JacksonUtil3.getObjectMapper().writeValueAsString(actual));
        for (boolean odin : new boolean[]{false, true}) {
            ArchieRMObjectMapperProvider writer = new ArchieRMObjectMapperProvider() {
                @Override
                public com.fasterxml.jackson.databind.ObjectMapper getJsonObjectMapper() {
                    return odin ? null : super.getJsonObjectMapper();
                }
            };
            String adl = ADLArchetypeSerializer.serialize(expected, null, writer);
            Archetype parsed = parser.parse(CharStreams.fromString(adl));
            assertEquals(JacksonUtil3.getObjectMapper().writeValueAsString(expected),
                    JacksonUtil3.getObjectMapper().writeValueAsString(parsed));
        }
        assertTrue(parser.getErrors().hasNoErrors());
    }

}
