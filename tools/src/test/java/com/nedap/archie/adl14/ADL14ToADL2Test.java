package com.nedap.archie.adl14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.antlr.v4.runtime.CharStreams;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ADL14ToADL2Test {

    private String path = "/com/nedap/archie/adl14/";

    @Test
    public void demoFromAdl14ToAdl2IdCodedTest() throws Exception {
        Archetype adl14;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-OBSERVATION.demo_adl14.v1.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            adl14 = parser.parse(stream, ConversionConfigForTest.getConfig());
        }

        // Configuration defaults to ID_CODED
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), new ADL14ConversionConfiguration());

        ADL2ConversionResultList resultList = converter.convert(Arrays.asList(adl14));
        Archetype result = resultList.getConversionResults().get(0).getArchetype();

        assertEquals(
                CharStreams.fromStream(new BOMInputStream(getClass().getResourceAsStream(path + "openEHR-EHR-OBSERVATION.demo_adl2_id.v1.0.0.adls"))).toString(),
                ADLArchetypeSerializer.serialize(result)
        );
    }

    @Test
    public void demoFromAdl14ToAdl2AtCodedTest() throws Exception {
        Archetype adl14;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-OBSERVATION.demo_adl14.v1.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            adl14 = parser.parse(stream, ConversionConfigForTest.getConfig());
        }

        ADL14ConversionConfiguration configuration = new ADL14ConversionConfiguration();
        configuration.setNodeIdCodeSystem(ADL14ConversionConfiguration.NodeIdCodeSystem.AT_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), configuration);

        ADL2ConversionResultList resultList = converter.convert(Arrays.asList(adl14));
        Archetype result = resultList.getConversionResults().get(0).getArchetype();

        assertEquals(
                CharStreams.fromStream(new BOMInputStream(getClass().getResourceAsStream(path + "openEHR-EHR-OBSERVATION.demo_adl2_at.v1.0.0.adls"))).toString(),
                ADLArchetypeSerializer.serialize(result)
        );
    }

    @Test
    public void examAbdomenFromAdl14ToAdl2IdCodedTest() throws Exception {
        Archetype parent;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam_adl14.v2.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            parent = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype child;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam-abdomen_adl14.v0.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            child = parser.parse(stream, ConversionConfigForTest.getConfig());
        }

        // Configuration defaults to ID_CODED
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), new ADL14ConversionConfiguration());

        ADL2ConversionResultList resultList = converter.convert(Arrays.asList(parent, child));
        Archetype parentResult = resultList.getConversionResults().get(0).getArchetype();
        Archetype childResult = resultList.getConversionResults().get(1).getArchetype();

        assertEquals(
                CharStreams.fromStream(new BOMInputStream(getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam_adl2_id.v2.1.3.adls"))).toString(),
                ADLArchetypeSerializer.serialize(parentResult)
        );
        assertEquals(
                CharStreams.fromStream(new BOMInputStream(getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam-abdomen_adl2_id.v0.0.1-alpha.adls"))).toString(),
                ADLArchetypeSerializer.serialize(childResult)
        );
    }

    @Test
    public void examAbdomenFromAdl14ToAdl2AtCodedTest() throws Exception {
        Archetype parent;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam_adl14.v2.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            parent = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype child;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam-abdomen_adl14.v0.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            child = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        
        ADL14ConversionConfiguration configuration = new ADL14ConversionConfiguration();
        configuration.setNodeIdCodeSystem(ADL14ConversionConfiguration.NodeIdCodeSystem.AT_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), configuration);

        ADL2ConversionResultList resultList = converter.convert(Arrays.asList(parent, child));
        Archetype parentResult = resultList.getConversionResults().get(0).getArchetype();
        Archetype childResult = resultList.getConversionResults().get(1).getArchetype();

        assertEquals(
                CharStreams.fromStream(new BOMInputStream(getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam_adl2_at.v2.1.3.adls"))).toString(),
                ADLArchetypeSerializer.serialize(parentResult)
        );
        assertEquals(
                CharStreams.fromStream(new BOMInputStream(getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam-abdomen_adl2_at.v0.0.1-alpha.adls"))).toString(),
                ADLArchetypeSerializer.serialize(childResult)
        );
    }
}
