package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationResult;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.InputStream;

import static org.junit.Assert.*;

public class AssumedValueConversionTest {

    @Test
    public void testAssumedValueConversion() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(AllMetaModelsInitialiser.getMetaModels(), conversionConfiguration);

        Archetype adl14archetype;
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.height.v2.adl")) {
            adl14archetype = new ADL14Parser(AllMetaModelsInitialiser.getMetaModels()).parse(stream, conversionConfiguration);
        }

        ADL2ConversionResultList result = converter.convert(
                Lists.newArrayList(adl14archetype));
        Archetype archetype = result.getConversionResults().get(0).getArchetype();

        CAttribute cAttribute = archetype.itemAtPath("/data[id2]/events[id3]/state[id14]/items[id15]/value[id9004]/defining_code");
        CTerminologyCode cTerminologyCode = (CTerminologyCode) cAttribute.getChildren().get(0);

        assertNull(cTerminologyCode.getAssumedValue().getTerminologyId());
        assertEquals("at17", cTerminologyCode.getAssumedValue().getCodeString());

        ValidationResult validationResult = new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels()).validate(archetype);

        assertTrue(validationResult.toString(), validationResult.passes());
    }
}
