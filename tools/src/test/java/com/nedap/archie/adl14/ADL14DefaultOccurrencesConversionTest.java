package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ADL14DefaultOccurrencesConversionTest {

    @Test
    public void testDefaultOccurrencesConversion() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(AllMetaModelsInitialiser.getMetaModels(), conversionConfiguration);
        //apply the first conversion and store the log. It has created an at code to bind to [openehr::124], used in a DV_QUANTITY.property
        try (InputStream stream = getClass().getResourceAsStream("/adl14/entry/evaluation/openEHR-EHR-EVALUATION.goal.v1.adl")) {
            ADL14Parser parser = new ADL14Parser(AllMetaModelsInitialiser.getMetaModels());
            Archetype parsed = parser.parse(stream, conversionConfiguration);
            ADL2ConversionResultList result = converter.convert(Lists.newArrayList(parsed));
            System.out.println(ADLArchetypeSerializer.serialize(result.getConversionResults().get(0).getArchetype()));
            Archetype converted = result.getConversionResults().get(0).getArchetype();
            CAttribute evaluationData = converted.itemAtPath("/data");
            //these two should NOT be mandated if left empty in the CKM
            assertNull(evaluationData.getCardinality());
            assertNull(evaluationData.getExistence());

            CComplexObject goalName = converted.itemAtPath("/data[id2]/items[id3]");
            //was null in ADL 1.4. Should be set to 1..1 by default during conversion
            assertEquals(new MultiplicityInterval(1, 1), goalName.getOccurrences());
            CComplexObject goalNameValueText = converted.itemAtPath("/data[id2]/items[id3]/value[1]");
            //was null in ADL 1.4. Because parent is single valued, this should be left null
            assertNull(goalNameValueText.getOccurrences());
        }
    }
}
