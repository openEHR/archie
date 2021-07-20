package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ADL14TerminologyConversionTest {

    @Test
    public void twoTermbindingsInOneConstraint() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = OpenEHRADL14ConversionConfiguration.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), conversionConfiguration);
        //apply the first conversion and store the log. It has created an at code to bind to [openehr::124], used in a DV_QUANTITY.property
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.termbinding.v1.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(parser.parse(stream, conversionConfiguration)));
            Archetype converted = result.getConversionResults().get(0).getArchetype();

            assertEquals("extra_value", converted.getTerminology().getTermDefinition("en", "id1").getOtherItems().get("extra_item"));
            String serialized = ADLArchetypeSerializer.serialize(converted);
            assertTrue(serialized.contains("extra_item = <\"extra_value\">"));

        }
    }
}
