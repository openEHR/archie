package com.nedap.archie.adl14;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ADL14ToADL2Test {

    private String path = "/com/nedap/archie/adl14/";

    @Test
    public void demoFromAdl14ToAdl2IdCodedTest() throws Exception {
        Archetype adl14;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-OBSERVATION.demo_adl14.v1.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            adl14 = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype expected;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-OBSERVATION.demo_adl2_id.v1.0.0.adls")) {
            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            expected = parser.parse(stream);
        }

        // Configuration defaults to ID_CODED
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), new ADL14ConversionConfiguration());

        ADL2ConversionResultList resultList = converter.convert(Arrays.asList(adl14));
        Archetype result = resultList.getConversionResults().get(0).getArchetype();

        assertEquals(ArchieAOMInfoLookup.ADL_VERSION, result.getAdlVersion());
        assertArchetypes(expected, result);
        assertArchetypes(result, expected); // Also check the reverse, to check if the result doesn't have more elements than the expected archetype
    }

    @Test
    public void demoFromAdl14ToAdl2AtCodedTest() throws Exception {
        Archetype adl14;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-OBSERVATION.demo_adl14.v1.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            adl14 = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype expected;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-OBSERVATION.demo_adl2_at.v1.0.0.adls")) {
            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            expected = parser.parse(stream);
        }

        ADL14ConversionConfiguration configuration = new ADL14ConversionConfiguration();
        configuration.setNodeIdCodeSystem(ADL14ConversionConfiguration.NODE_ID_CODE_SYSTEM.AT_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), configuration);

        ADL2ConversionResultList resultList = converter.convert(Arrays.asList(adl14));
        Archetype result = resultList.getConversionResults().get(0).getArchetype();

        assertEquals(ArchieAOMInfoLookup.ADL_VERSION, result.getAdlVersion());
        assertArchetypes(expected, result);
        assertArchetypes(result, expected); // Also check the reverse, to check if the result doesn't have more elements than the expected archetype
    }

    @Test
    public void examAbdomenFromAdl14ToAdl2IdCodedTest() throws Exception {
        Archetype parent;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam_adl14.v2.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            parent = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype child;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam-abdomen_adl14.v0.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            child = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype parentExpected;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam_adl2_id.v2.1.3.adls")) {
            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            parentExpected = parser.parse(stream);
        }
        Archetype childExpected;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam-abdomen_adl2_id.v0.0.1-alpha.adls")) {
            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            childExpected = parser.parse(stream);
        }

        // Configuration defaults to ID_CODED
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), new ADL14ConversionConfiguration());

        ADL2ConversionResultList resultList = converter.convert(Arrays.asList(parent, child));
        Archetype parentResult = resultList.getConversionResults().get(0).getArchetype();
        Archetype childResult = resultList.getConversionResults().get(1).getArchetype();

        assertEquals(ArchieAOMInfoLookup.ADL_VERSION, parentResult.getAdlVersion());
        assertArchetypes(parentExpected, parentResult);
        assertArchetypes(parentResult, parentExpected); // Also check the reverse, to check if the result doesn't have more elements than the expected archetype

        assertEquals(ArchieAOMInfoLookup.ADL_VERSION, childResult.getAdlVersion());
        assertArchetypes(childExpected, childResult);
        assertArchetypes(childResult, childExpected); // Also check the reverse, to check if the result doesn't have more elements than the expected archetype
    }

    @Test
    public void examAbdomenFromAdl14ToAdl2AtCodedTest() throws Exception {
        Archetype parent;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam_adl14.v2.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            parent = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype child;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam-abdomen_adl14.v0.adl")) {
            ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModels());
            child = parser.parse(stream, ConversionConfigForTest.getConfig());
        }
        Archetype parentExpected;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam_adl2_at.v2.1.3.adls")) {
            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            parentExpected = parser.parse(stream);
        }
        Archetype childExpected;
        try (InputStream stream = getClass().getResourceAsStream(path + "openEHR-EHR-CLUSTER.exam-abdomen_adl2_at.v0.0.1-alpha.adls")) {
            ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            childExpected = parser.parse(stream);
        }

        // Configuration defaults to ID_CODED
        ADL14ConversionConfiguration configuration = new ADL14ConversionConfiguration();
        configuration.setNodeIdCodeSystem(ADL14ConversionConfiguration.NODE_ID_CODE_SYSTEM.AT_CODED);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModels(), configuration);

        ADL2ConversionResultList resultList = converter.convert(Arrays.asList(parent, child));
        Archetype parentResult = resultList.getConversionResults().get(0).getArchetype();
        Archetype childResult = resultList.getConversionResults().get(1).getArchetype();

        assertEquals(ArchieAOMInfoLookup.ADL_VERSION, parentResult.getAdlVersion());
        assertArchetypes(parentExpected, parentResult);
        assertArchetypes(parentResult, parentExpected); // Also check the reverse, to check if the result doesn't have more elements than the expected archetype

        assertEquals(ArchieAOMInfoLookup.ADL_VERSION, childResult.getAdlVersion());
        assertArchetypes(childExpected, childResult);
        assertArchetypes(childResult, childExpected); // Also check the reverse, to check if the result doesn't have more elements than the expected archetype
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
    }
}
