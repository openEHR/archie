package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.utils.AOMUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

class ADL14ExternalTerminologyConversionTest {

    @Test
    void terminologyBindingsConverted() throws IOException, ADLParseException {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);
        //apply the first conversion and store the log. It has created an at code to bind to [openehr::124], used in a DV_QUANTITY.property
        try(InputStream stream = getClass().getResourceAsStream("/adl14/openEHR-EHR-CLUSTER.value_binding.v1.0.0.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream, conversionConfiguration)));
            Archetype converted = result.getConversionResults().get(0).getArchetype();
            CTerminologyCode termCodeConstraint = converted.itemAtPath("/items/value/property[1]");
            String atCode = termCodeConstraint.getConstraint().get(0);
            Assertions.assertTrue(AOMUtils.isValueCode(atCode), "code must be a value, not a value set");
            Assertions.assertEquals("Mass", converted.getTerminology().getTermDefinition("en", atCode).getText());
            Assertions.assertEquals("Mass", converted.getTerminology().getTermDefinition("en", atCode).getDescription());

            Assertions.assertEquals("* Mass (en)", converted.getTerminology().getTermDefinition("no-bk", atCode).getText());
            Assertions.assertEquals("* Mass (en)", converted.getTerminology().getTermDefinition("no-bk", atCode).getDescription());
        }

    }

    @Test
    void twoTermbindingsInOneConstraint() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);
        //apply the first conversion and store the log. It has created an at code to bind to [openehr::124], used in a DV_QUANTITY.property
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.termbinding.v1.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider());
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(parser.parse(stream, conversionConfiguration)));
            Assertions.assertFalse(parser.getErrors().hasErrors());
            Archetype converted = result.getConversionResults().get(0).getArchetype();

            CTerminologyCode termCodeConstraint = converted.itemAtPath("/items/value/defining_code[1]");
            String acCode = termCodeConstraint.getConstraint().get(0);
            Assertions.assertTrue(AOMUtils.isValueSetCode(acCode), "the code should have been converted to a value set");
            List<String> atCodes = termCodeConstraint.getValueSetExpanded();
            Assertions.assertEquals(2, atCodes.size(), atCodes.toString());
            Assertions.assertEquals(new URI("http://openehr.org/id/123"), converted.getTerminology().getTermBinding("openehr", atCodes.get(0)));
            Assertions.assertEquals(new URI("http://openehr.org/id/234"), converted.getTerminology().getTermBinding("openehr", atCodes.get(1)));
            Assertions.assertEquals("Loudness", converted.getTerminology().getTermDefinition("en", atCodes.get(0)).getText());
            Assertions.assertEquals("secondary allied health care", converted.getTerminology().getTermDefinition("en", atCodes.get(1)).getText());

        }
    }
}
