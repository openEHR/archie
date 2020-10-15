package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class ADL14TerminologySupportTest {

    @Test
    public void terminologyBindingsConverted() throws IOException {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), conversionConfiguration);
        String createdAtCode = null;
        //apply the first conversion and store the log. It has created an at code to bind to [openehr::124], used in a DV_QUANTITY.property
        try(InputStream stream = getClass().getResourceAsStream("/adl14/openEHR-EHR-CLUSTER.value_binding.v1.0.0.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModels()).parse(stream, conversionConfiguration)));
            Archetype converted = result.getConversionResults().get(0).getArchetype();
            CTerminologyCode termCodeConstraint = converted.itemAtPath("/items/value/property[1]");
            assertEquals("Mass", converted.getTerminology().getTermDefinition("en", termCodeConstraint.getConstraint().get(0)).getText());
            assertEquals("Mass", converted.getTerminology().getTermDefinition("en", termCodeConstraint.getConstraint().get(0)).getDescription());

            assertEquals("* Mass (en)", converted.getTerminology().getTermDefinition("no-bk", termCodeConstraint.getConstraint().get(0)).getText());
            assertEquals("* Mass (en)", converted.getTerminology().getTermDefinition("no-bk", termCodeConstraint.getConstraint().get(0)).getDescription());
        }

    }
}
