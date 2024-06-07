package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.InputStream;

import static junit.framework.TestCase.assertEquals;

public class ConversionConfigurationTest {

    @Test
    public void testRmRelease() throws Exception {

        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(AllMetaModelsInitialiser.getMetaModels(), conversionConfiguration);


        try(InputStream stream = getClass().getResourceAsStream("openehr-EHR-COMPOSITION.review.v1.adl")) {
            Archetype adl14 = new ADL14Parser(AllMetaModelsInitialiser.getMetaModels()).parse(stream, conversionConfiguration);
            ADL2ConversionResultList result = converter.convert(Lists.newArrayList(adl14));

            assertEquals("1.1.0", result.getConversionResults().get(0).getArchetype().getRmRelease());

            conversionConfiguration.setRmRelease("1.0.4");
            converter = new ADL14Converter(AllMetaModelsInitialiser.getMetaModels(), conversionConfiguration);

            result = converter.convert(Lists.newArrayList(adl14));
            assertEquals("1.0.4", result.getConversionResults().get(0).getArchetype().getRmRelease());
        }

    }
}
