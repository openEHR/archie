package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConversionConfigurationTest {

    @Test
    public void testRmRelease() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);

        try(InputStream stream = getClass().getResourceAsStream("openehr-EHR-COMPOSITION.review.v1.adl")) {
            Archetype adl14 = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream, conversionConfiguration);
            ADL2ConversionResultList result = converter.convert(Lists.newArrayList(adl14));

            assertEquals("1.1.0", ((AuthoredArchetype) result.getConversionResults().get(0).getArchetype()).getRmRelease());

            conversionConfiguration.setRmRelease("1.0.4");
            converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);

            result = converter.convert(Lists.newArrayList(adl14));
            assertEquals("1.0.4", ((AuthoredArchetype) result.getConversionResults().get(0).getArchetype()).getRmRelease());
        }

    }

    @Test
    public void testRmReleaseFromArchetype() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        conversionConfiguration.setRmRelease("1.0.4");
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);

        try(InputStream stream = getClass().getResourceAsStream("openehr-EHR-COMPOSITION.review.v1.adl")) {
            AuthoredArchetype adl14 = (AuthoredArchetype) new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream, conversionConfiguration);

            // header item rm_release=N.M.P, accepted by the ADL 1.4 grammar
            adl14.setRmRelease("1.0.2");
            AuthoredArchetype converted = convertSingle(converter, adl14);
            assertEquals("1.0.2", converted.getRmRelease());

            // other_details["rm_release"] wins over the header item and is removed after conversion
            adl14.getDescription().getOtherDetails().put("rm_release", "1.1.0");
            converted = convertSingle(converter, adl14);
            assertEquals("1.1.0", converted.getRmRelease());
            assertFalse(converted.getDescription().getOtherDetails().containsKey("rm_release"));
            assertTrue(adl14.getDescription().getOtherDetails().containsKey("rm_release"), "input archetype must not be changed");

            // a value that is not a 3-part version is ignored and kept in other_details
            adl14.setRmRelease(null);
            adl14.getDescription().getOtherDetails().put("rm_release", "1.1");
            converted = convertSingle(converter, adl14);
            assertEquals("1.0.4", converted.getRmRelease());
            assertEquals("1.1", converted.getDescription().getOtherDetails().get("rm_release"));
        }
    }

    private static AuthoredArchetype convertSingle(ADL14Converter converter, Archetype adl14) {
        ADL2ConversionResultList result = converter.convert(Lists.newArrayList(adl14));
        return (AuthoredArchetype) result.getConversionResults().get(0).getArchetype();
    }
}
