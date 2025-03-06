package com.nedap.archie.adl14;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ADL14ToADL2Test {

    @Test
    public void Adl14ToAdl2IdCodedTest() throws Exception {
        Archetype adl14;
        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.body_weight-adl14.v2.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            adl14 = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype expected;
        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.body_weight-adl2.v2.1.8.adls")) {
            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            expected = parser.parse(stream);
        }

        ADL14ConversionConfiguration configuration = new ADL14ConversionConfiguration();
        configuration.setAdlConfiguration(ADL14ConversionConfiguration.ADL2VERSION.ID_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), configuration);

        ADL2ConversionResultList resultList = converter.convert(List.of(adl14));
        Archetype result = resultList.getConversionResults().get(0).getArchetype();

        Path path = Paths.get("result-id.adls");
        Files.write(path, ADLArchetypeSerializer.serialize(result).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        assertEquals("2.3.0", result.getAdlVersion());
        assertArchetypes(expected, result);
        assertArchetypes(result, expected); // Also check the reverse, to check if the result doesn't have more than the expected archetype
    }

    @Test
    public void Adl14ToAdl2AtCodedTest() throws Exception {
        Archetype adl14;
        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.body_weight-adl14.v2.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            adl14 = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype expected;
        try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.body_weight-adl2plus.v2.1.8.adls")) {
            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            expected = parser.parse(stream);
        }

        ADL14ConversionConfiguration configuration = new ADL14ConversionConfiguration();
        configuration.setAdlConfiguration(ADL14ConversionConfiguration.ADL2VERSION.AT_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), configuration);

        ADL2ConversionResultList resultList = converter.convert(List.of(adl14));
        Archetype result = resultList.getConversionResults().get(0).getArchetype();

        Path path = Paths.get("result-at.adls");
        Files.write(path, ADLArchetypeSerializer.serialize(result).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        assertEquals("2.4.0", result.getAdlVersion());
        assertArchetypes(expected, result);
        assertArchetypes(result, expected); // Also check the reverse, to check if the result doesn't have more than the expected archetype
    }

    private void assertArchetypes(Archetype expected, Archetype actual) {
        ArchetypeTerminology expectedTerminology = expected.getTerminology();
        ArchetypeTerminology actualTerminology = actual.getTerminology();

        // Definition
        Stack<CObject> stack = new Stack<>();
        stack.push(expected.getDefinition());
        while (!stack.isEmpty()) {
            CObject object = stack.pop();
            assertNotNull(actual.itemAtPath(object.getPath()));
            for (CAttribute attribute : object.getAttributes()) {
                for (CObject cObject : attribute.getChildren()) {
                    stack.push(cObject);
                }
            }
        }

        // TermDefinitions
        Map<String, Map<String, ArchetypeTerm>> expectedTermDefinitions = expectedTerminology.getTermDefinitions();
        Map<String, Map<String, ArchetypeTerm>> actualTermDefinitions = actualTerminology.getTermDefinitions();

        assertEquals(expectedTermDefinitions.keySet(), actualTermDefinitions.keySet());
        expectedTermDefinitions.keySet().forEach(key -> {
            assertEquals(expectedTermDefinitions.get(key).keySet(), actualTermDefinitions.get(key).keySet());
            // at0001 (ITEM_TREE), at0002 (HISTORY) en at0008 (ITEM_TREE) moeten er nog uitgefiltert
            expectedTermDefinitions.get(key).keySet().forEach(key2 -> {
                assertEquals(expectedTermDefinitions.get(key).get(key2), expectedTermDefinitions.get(key).get(key2));
            });
        });

        // TermBindings
        Map<String, Map<String, URI>> expectedTermBindings = expectedTerminology.getTermBindings();
        Map<String, Map<String, URI>> actualTermBindings = actualTerminology.getTermBindings();

        assertEquals(expectedTermBindings.keySet(), actualTermBindings.keySet());
        expectedTermBindings.keySet().forEach(key -> {
            assertEquals(expectedTermBindings.get(key).keySet(), actualTermBindings.get(key).keySet());
            expectedTermBindings.get(key).keySet().forEach(key2 -> {
                assertEquals(expectedTermBindings.get(key).get(key2), actualTermBindings.get(key).get(key2));
            });
        });

        // ValueSets
        Map<String, ValueSet> expectedValueSets = expectedTerminology.getValueSets();
        Map<String, ValueSet> actualValueSets = actualTerminology.getValueSets();

        assertEquals(expectedValueSets.keySet(), actualValueSets.keySet());
        expectedValueSets.keySet().forEach(key -> {
            assertEquals(expectedValueSets.get(key), actualValueSets.get(key));
        });

        // In 'details' bij elke taal komt het nog wel eens voor dat er een extra spatie staat in het at-result
        // In het resultaat archetype staan er nog comments achter elke CComplexObject (zoals HISTORY, ITEM_TREE) waar dat in het voorbeeld niet zo is
        // Volgorde van de terminology is anders, omdat dit gebaseerd is op welke als laatste is toegevoegd aan de map
    }
}
