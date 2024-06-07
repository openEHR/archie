package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CComplexObject;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.InputStream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DvScaleConversionTest {

    @Test
    public void testDvScale() throws Exception {

        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(AllMetaModelsInitialiser.getMetaModels(), conversionConfiguration);


        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.ordinalandscale.v0.adl")) {
            ADL14Parser adl14Parser = new ADL14Parser(AllMetaModelsInitialiser.getMetaModels());
            Archetype adl14 = adl14Parser.parse(stream, conversionConfiguration);
            assertFalse(adl14Parser.getErrors().hasErrors());
            ADL2ConversionResultList result = converter.convert(Lists.newArrayList(adl14));
            Archetype archetype = result.getConversionResults().get(0).getArchetype();
            CComplexObject ordinal1 = archetype.itemAtPath("/items[id2]/value[1]");
            assertEquals("DV_ORDINAL", ordinal1.getRmTypeName());
            CComplexObject scale1 = archetype.itemAtPath("/items[id6]/value[1]");
            assertEquals("DV_SCALE", scale1.getRmTypeName());
            assertEquals(Lists.newArrayList("value", "symbol"), scale1.getAttributeTuples().get(0).getMemberNames());
            assertEquals(3, scale1.getAttributeTuples().get(0).getTuples().size());
            assertEquals("{1.0}", scale1.getAttributeTuples().get(0).getTuples().get(0).getMember(0).toString());
            assertEquals("{[at7]}", scale1.getAttributeTuples().get(0).getTuples().get(0).getMember(1).toString());

            CComplexObject scale2 = archetype.itemAtPath("/items[id10]/value[1]");
            assertEquals("DV_SCALE", scale2.getRmTypeName());

            assertEquals(Lists.newArrayList("value", "symbol"), scale2.getAttributeTuples().get(0).getMemberNames());
            assertEquals(4, scale2.getAttributeTuples().get(0).getTuples().size());
            assertEquals("{1}", scale2.getAttributeTuples().get(0).getTuples().get(0).getMember(0).toString());
            assertEquals("{[at11]}", scale2.getAttributeTuples().get(0).getTuples().get(0).getMember(1).toString());
            assertEquals("{2.5}", scale2.getAttributeTuples().get(0).getTuples().get(2).getMember(0).toString());
            assertEquals("{[at12]}", scale2.getAttributeTuples().get(0).getTuples().get(2).getMember(1).toString());


            CComplexObject scale3 = archetype.itemAtPath("/items[id14]/value[1]");
            assertEquals("DV_SCALE", scale3.getRmTypeName());
            CComplexObject scale4 = archetype.itemAtPath("/items[id21]/value[1]");
            assertEquals("DV_SCALE", scale4.getRmTypeName());

            CComplexObject scaleNormal1 = archetype.itemAtPath("/items[id18]/value[id171]");
            assertEquals("DV_SCALE", scaleNormal1.getRmTypeName());
            CComplexObject scaleNormal2 = archetype.itemAtPath("/items[id18]/value[id172]");
            assertEquals("DV_SCALE", scaleNormal2.getRmTypeName());

            CComplexObject scaleUnconstrained = archetype.itemAtPath("/items[id23]/value[1]");
            assertEquals("DV_SCALE", scaleUnconstrained.getRmTypeName());
            assertTrue(scaleUnconstrained.getAttributes().isEmpty() && scaleUnconstrained.getAttributeTuples().isEmpty());
        }

    }
}
