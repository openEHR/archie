package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ADL14InternalTerminologyTest {

    @Test
    public void internalTerminologyRemoved() throws IOException, ADLParseException {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(AllMetaModelsInitialiser.getMetaModels(), conversionConfiguration);

        try(InputStream stream = getClass().getResourceAsStream("/adl14/openEHR-EHR-OBSERVATION.internal_terminology_test.v0.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(AllMetaModelsInitialiser.getMetaModels()).parse(stream, conversionConfiguration)));
            Archetype converted = result.getConversionResults().get(0).getArchetype();
            // A terminology code is unnecessary if:
            //  - The text.length < 19
            //  - AND the description contains "@ internal @"
            //  - AND If the term is NOT a root node
            //  - AND parent is NOT a container attribute
            Map<String, ArchetypeTerm> adl2Terminology =  converted.getTerminology().getTermDefinitions().get("en");
            assertEquals(3, adl2Terminology.size());
            assertNotNull(adl2Terminology.get("id1")); // Should exist because it's a root node
            assertNotNull(adl2Terminology.get("id3")); // Exists because its parent is a container attribute
            assertNotNull(adl2Terminology.get("id8")); // Exists because the text is longer than 19 characters
        }
    }

}
