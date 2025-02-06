package com.nedap.archie.adl14;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ADL14ToADL2Test {

    @Test
    public void Adl14ToAdl2IdCodedTest() throws Exception {
        Archetype adl14;
        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.body_weight-adl14.v2.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            adl14 = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype expected;
        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.body_weight.v2.1.6.adls")) {
            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            expected = parser.parse(stream);
        }

        ADL14ConversionConfiguration configuration = new ADL14ConversionConfiguration();
        configuration.setAdlConfiguration(ADL14ConversionConfiguration.ADL2VERSION.ID_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), configuration);

        ADL2ConversionResultList resultList = converter.convert(List.of(adl14));
        Archetype result = resultList.getConversionResults().get(0).getArchetype();

        assertEquals(ADLArchetypeSerializer.serialize(expected), ADLArchetypeSerializer.serialize(result));
    }

    @Test
    public void Adl14ToAdl2AtCodedTest() throws Exception {
        Archetype adl14;
        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.body_weight-adl14.v2.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            adl14 = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        // Until the grammar of adl 2.4 is merged, we cannot parse the expected archetype yet
//        Archetype expected;
//        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.body_weight.v2.1.6-at-coded.adls")) {
//            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
//            expected = parser.parse(stream);
//        }

        ADL14ConversionConfiguration configuration = new ADL14ConversionConfiguration();
        configuration.setAdlConfiguration(ADL14ConversionConfiguration.ADL2VERSION.AT_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), configuration);

        ADL2ConversionResultList resultList = converter.convert(List.of(adl14));
        Archetype result = resultList.getConversionResults().get(0).getArchetype();

        // assert expected archetype vs result
    }
}
