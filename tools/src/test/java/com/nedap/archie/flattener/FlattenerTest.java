package com.nedap.archie.flattener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CComplexObjectProxy;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;
import com.nedap.archie.openehr.rminfo.OpenEhrTestRmInfoLookup;

import java.util.List;
import java.util.Stack;

import static org.junit.Assert.*;

/**
 * High level tests of the flattener on a relatively large set of archetypes with many features.
 * Tests basics like object consistency.
 * Created by pieter.bos on 21/10/15.
 */
public class FlattenerTest {

    private static Archetype report;
    private static Archetype device;
    private static Archetype bloodPressureObservation;
    private static Archetype reportResult;
    private static Archetype bloodPressureComposition;
    private static Archetype height;
    private static Archetype heightTemplate;
    private static Archetype reportWithSynopsis;
    private static Archetype clinicalSynopsis;
    private static Archetype bloodPressureWithSynopsis;
    private static SimpleArchetypeRepository repository;

    private ReferenceModels models;


    private Flattener flattener;

    @Before
    public void setup() throws Exception {

        models = AllMetaModelsInitialiser.getNativeRms();

        // reportresult specializes report.
        // blood pressure composition specializes report result.

        // it adds a blood pressure observation
        // it also adds a device
        // it contains specific template overlays for both blood pressure observation and device
        report = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.report.v1.adls"));
        reportResult = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.report-result.v1.adls"));
        device = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-CLUSTER.device.v1.adls"));

        bloodPressureObservation = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.blood_pressure.v1.adls"));
        bloodPressureComposition = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.blood_pressure.v1.0.0.adlt"));

        height = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.height.v1.adls"));
        heightTemplate = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.length.v1.0.0.adlt"));

        reportWithSynopsis = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.report-result-with-synopsis.v1.0.0.adls"));
        clinicalSynopsis = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-EVALUATION.clinical_synopsis.v1.0.0.adls"));
        bloodPressureWithSynopsis = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-COMPOSITION.blood_pressure_with_synopsis.v1.0.0.adlt"));


        repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(report);
        repository.addArchetype(device);
        repository.addArchetype(bloodPressureComposition);
        repository.addArchetype(bloodPressureObservation);
        repository.addArchetype(reportResult);
        repository.addArchetype(height);
        repository.addArchetype(heightTemplate);

        repository.addArchetype(reportWithSynopsis);
        repository.addArchetype(clinicalSynopsis);
        repository.addArchetype(bloodPressureWithSynopsis);
        flattener = new Flattener(repository, models).createOperationalTemplate(true);
    }

    @Test
    public void archetypeSlotReplacement() {
        Archetype flattened = flattener.flatten(bloodPressureWithSynopsis);
        List<CObject> content = flattened.getDefinition().getAttribute("content").getChildren();
        assertEquals(2, content.size());
        //one archetyperoot, one slot. Slot is closed and this is an operational template
        //so it should have been removed. Ordering of original should have been preserved
        //archetype roots should have structure added
        CObject child1 = content.get(0);
        CObject child2 = content.get(1);

        assertEquals("id0.0.2.1", child1.getNodeId());
        assertTrue(child1 instanceof CArchetypeRoot);
        assertEquals("id0.0.3", child2.getNodeId());
        assertTrue(child2 instanceof CArchetypeRoot);
        assertEquals(2, child1.getAttributes().size());
        assertEquals(1, child2.getAttributes().size());
    }

    @Test
    public void reportResult() throws Exception {
        Archetype flattened = flattener.flatten(reportResult);
        //TODO: nice to know there are no exceptions. Now add assertions
    }

    @Test
    public void checkParentReplacement() throws Exception {
        Archetype flattened = flattener.flatten(bloodPressureComposition);

        Stack<CObject> worklist = new Stack<>();
        worklist.add(flattened.getDefinition());

        while (!worklist.isEmpty()) {
            CObject object = worklist.pop();
            if (object.getArchetype() != flattened) {
                fail("wrong parent found!");
            }
            for (CAttribute attr : object.getAttributes()) {
                if (attr.getParent() != object) {
                    fail("wrong parent found!");
                }
                worklist.addAll(attr.getChildren());
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        OpenEhrRmJacksonUtil.configureObjectMapper(objectMapper);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        System.out.println(objectMapper.writeValueAsString(flattened));
    }

    @Test
    public void useNodeReplacement() throws Exception {
        Archetype flattened = flattener.flatten(bloodPressureComposition);

        Stack<CObject> worklist = new Stack<>();
        worklist.add(flattened.getDefinition());
        boolean proxyFound = false;
        while (!worklist.isEmpty()) {
            CObject object = worklist.pop();
            if (object.getNodeId().equals("id1065")) {
                assertFalse(object instanceof CComplexObjectProxy);
                proxyFound = true;
                assertNotNull(object.getAttribute("items").getChild("id5.1"));
            }
            for (CAttribute attr : object.getAttributes()) {
                if (attr.getParent() != object) {
                    fail("wrong parent found!");
                }
                worklist.addAll(attr.getChildren());
            }
        }
        assertTrue("a prox object should have been found", proxyFound);
    }

    @Test
    public void testComponentTerminologies() throws Exception {
        Archetype flattened = flattener.flatten(bloodPressureComposition);

        CComplexObject definition = flattened.getDefinition();
        //you definately need component terminologies to translate this :)
        CObject object = (CObject)
                flattened.itemAtPath("/content[openEHR-EHR-OBSERVATION.ovl-blood_pressure-blood_pressure-001.v1.0.0]/protocol[id12]/items[id1011]");
        assertNull(flattened.getTerminology().getTermDefinition("en", "id1011"));
        assertEquals("Diastolic endpoint", flattened.getTerm(object, "en").getText());
    }

    @Test
    public void testTermForArchetypeRoot() {
        Archetype flattened = flattener.flatten(reportWithSynopsis);

        CComplexObject definition = flattened.getDefinition();
        CObject object = (CObject) flattened.itemAtPath("/content[id0.0.3]");
        assertEquals("Summary or evaluation", flattened.getTerm(object, "en").getText());
        assertEquals("Summary or evaluation for the measurement", flattened.getTerm(object, "en").getDescription());
    }

    @Test
    public void height() throws Exception {
        assertTrue(heightTemplate.isDifferential());
        Archetype flattened = flattener.flatten(heightTemplate);
        assertFalse(flattened.isDifferential());
        CObject object = (CObject) flattened.itemAtPath("/content[openEHR-EHR-OBSERVATION.ovl-length-height-001.v1.0.0]");

        assertNotNull(object);
        assertNotNull(flattened.getTerm(object, "nl"));
        assertEquals("Lengte", flattened.getTerm(object, "nl").getText());
/*
data matches {
			HISTORY[id2] matches {
				events cardinality matches {1..*; unordered} matches {
					EVENT[id3] occurrences matches {1..*} matches {	-- Any event
						data matches {
							ITEM_TREE[id4] matches {
								items cardinality matches {1..*; unordered} matches {
									ELEMENT[id5] matches {	-- Height/Length
										value matches {
											DV_QUANTITY[id21] matches {
 */
        CObject quantity = flattened.itemAtPath("/content[openEHR-EHR-OBSERVATION.ovl-length-height-001.v1.0.0]/data[id2]/events[id3]/data[id4]/items[id5]/value[id21]");
        assertEquals("DV_QUANTITY", quantity.getRmTypeName());
    }

    @Test
    public void removeLanguagesFromTerminology() throws Exception {
        flattener.keepLanguages("en", "nl");
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("ru"));
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("nl"));
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("en"));

        Archetype flattened = flattener.flatten(bloodPressureObservation);
        assertFalse(flattened.getTerminology().getTermDefinitions().containsKey("ru"));
        assertTrue(flattened.getTerminology().getTermDefinitions().containsKey("nl"));
        assertTrue(flattened.getTerminology().getTermDefinitions().containsKey("en"));

        //metadata still intact in this test - see removeLanguagesFromMetaData
        assertTrue(flattened.getDescription().getDetails().containsKey("ru"));
        assertTrue(flattened.getTranslations().containsKey("ru"));

        //original should not be modified
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("ru"));
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("nl"));
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("en"));
    }

    @Test
    public void removeLanguagesFromComponentTerminologies() throws Exception {
        OperationalTemplate flattenedWithLanguages = (OperationalTemplate) flattener.flatten(bloodPressureComposition);

        flattener = new Flattener(repository, models).createOperationalTemplate(true).keepLanguages("en", "nl");
        OperationalTemplate flattenedWithoutLanguages = (OperationalTemplate) flattener.flatten(bloodPressureComposition);

        for(String key:flattenedWithoutLanguages.getComponentTerminologies().keySet()) {
            assertFalse(flattenedWithoutLanguages.getComponentTerminologies().get(key).getTermDefinitions().containsKey("es-ar"));
            assertTrue(flattenedWithLanguages.getComponentTerminologies().get(key).getTermDefinitions().containsKey("es-ar"));
        }
        assertEquals(flattenedWithLanguages.getComponentTerminologies().size(), flattenedWithoutLanguages.getComponentTerminologies().size());
    }

    @Test
    public void removeLanguagesFromMetaData() throws Exception {
        flattener.keepLanguages("en", "nl").removeLanguagesFromMetadata(true);

        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("ru"));
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("nl"));
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("en"));
        Archetype flattened = flattener.flatten(bloodPressureObservation);
        assertFalse(flattened.getTerminology().getTermDefinitions().containsKey("ru"));
        assertTrue(flattened.getTerminology().getTermDefinitions().containsKey("nl"));
        assertTrue(flattened.getTerminology().getTermDefinitions().containsKey("en"));
        assertFalse(flattened.getDescription().getDetails().containsKey("ru"));
        assertFalse(flattened.getTranslations().containsKey("ru"));

        //original should not be modified
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("ru"));
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("nl"));
        assertTrue(bloodPressureObservation.getTerminology().getTermDefinitions().containsKey("en"));
    }

    @Test
    public void validate() {
        ReferenceModels models = new ReferenceModels();
        models.registerModel(OpenEhrRmInfoLookup.getInstance());
        models.registerModel(OpenEhrTestRmInfoLookup.getInstance());
        ((InMemoryFullArchetypeRepository) repository).compile(models);
        for(ValidationResult result:((InMemoryFullArchetypeRepository) repository).getAllValidationResults()) {
            assertTrue(result.getArchetypeId() + " had errors or warnings: " + result.getErrors(), result.passes());
        }
        System.out.println(repository);
    }
}
