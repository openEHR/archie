package com.nedap.archie.flattener.specexamples;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CComplexObjectProxy;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;
import org.junit.Before;
import org.junit.Test;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.util.List;
import java.util.stream.Collectors;

import static com.nedap.archie.flattener.specexamples.FlattenerTestUtil.*;
import static org.junit.Assert.*;

public class FlattenerExamplesFromSpecTest {


    protected SimpleArchetypeRepository repository;

    protected MetaModels models;

    @Before
    public void setup() throws Exception {
        repository = new SimpleArchetypeRepository();
        models = new MetaModels(BuiltinReferenceModels.getAvailableModelInfoLookups(), (BmmRepository) null);
    }

    @Test
    public void specializationPaths() throws Exception {
        Archetype labTest = parse("openEHR-EHR-OBSERVATION.lab-test.v1.0.0.adls");
        repository.addArchetype(labTest);
        Archetype specializationPaths = parse("specialization_paths.adls");
        Archetype flattened = new Flattener(repository, models).flatten(specializationPaths);
        assertEquals(specializationPaths.getParentArchetypeId(), flattened.getParentArchetypeId());

        CObject originalConstraint = flattened.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id79]");
        CObject firstAddedConstraint = flattened.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id79.2]");
        CObject secondAddedConstraint = flattened.itemAtPath("/data[id2]/events[id3]/data[id4]/items[id79.7]");

        assertNotNull("first constraint should have been added", firstAddedConstraint);
        assertEquals(new MultiplicityInterval(0, 1), firstAddedConstraint.getOccurrences());
        assertNotNull("added constraint should have a value attribute", firstAddedConstraint.getAttribute("value"));

        assertNotNull("second constraint should have been added", secondAddedConstraint);
        assertEquals(new MultiplicityInterval(0, 1), secondAddedConstraint.getOccurrences());
        assertNull("second added constraint should not have a value attribute", secondAddedConstraint.getAttribute("value"));

        //original constraint should not have been closed and still be present
        assertNotNull("original constraint should not have been removed", originalConstraint);
        assertNull("original constraint should not have been closed", originalConstraint.getOccurrences());
    }

    @Test
    public void redefinitionForRefinement() throws Exception {
        Archetype problem = parse("problem.adls");
        repository.addArchetype(problem);
        Archetype diagnosis = parse("diagnosis.adls");
        Archetype flattenedDiagnosis = new Flattener(repository, models).flatten(diagnosis);

        assertEquals("Recording of diagnosis", flattenedDiagnosis.getDefinition().getTerm().getText());

        CObject nodeWithId4 = (CObject) flattenedDiagnosis.itemAtPath("/data[id2]/items[id3]/value[id4]");
        assertEquals("DV_CODED_TEXT", nodeWithId4.getRmTypeName());
        assertTrue("dv coded text should now have a terminology code constraint",
                nodeWithId4.getAttribute("defining_code").getChildren().get(0) instanceof CTerminologyCode);


        CObject firstAddedNode = flattenedDiagnosis.itemAtPath("/data/items[id0.32]");
        CObject secondAddedNode = flattenedDiagnosis.itemAtPath("/data/items[id0.35]");
        CObject thirdAddedNode = flattenedDiagnosis.itemAtPath("/data/items[id0.37]");

        assertEquals("Status", firstAddedNode.getTerm().getText());
        assertEquals("Diag. criteria", secondAddedNode.getTerm().getText());
        assertEquals("Clin. staging", thirdAddedNode.getTerm().getText());

        List<CObject> items = flattenedDiagnosis.getDefinition().getAttribute("data").getChild("id2").getAttribute("items").getChildren();

        //verify the ordering specified by before and after. TODO: or is this operational template only?
        assertEquals("id3", items.get(0).getNodeId());
        assertEquals("id0.32", items.get(1).getNodeId());
        assertEquals("id5", items.get(2).getNodeId());
        assertEquals("id0.37", items.get(items.size() -1).getNodeId());
        assertEquals("id0.35", items.get(items.size() -2).getNodeId());

    }

    //9.3.2. Redefinition for Specialisation has been fully covered by specialization paths and is not needed in tests

    //9.3.2.1. Specialisation with Cloning
    @Test
    public void specialisationWithCloning() throws Exception {
        Archetype labTestPanel = parse("openEHR-EHR-CLUSTER.laboratory_test_panel.v1.0.0.adls");
        repository.addArchetype(labTestPanel);
        Archetype lipidStudiesPanel = parse("openEHR-EHR-CLUSTER.lipid_studies_panel.adls");
        Archetype flattenedLipidStudies = new Flattener(repository, models).flatten(lipidStudiesPanel);

        List<CObject> itemNodes = flattenedLipidStudies.getDefinition().getAttribute("items").getChildren();
        List<String> nodeIds = itemNodes.stream().map(cObject -> cObject.getNodeId()).collect(Collectors.toList());
        assertEquals(Lists.newArrayList("id3", "id3.1", "id3.2", "id3.5", "id14"), nodeIds);

        for(String nodeId: Lists.newArrayList("1", "2", "5")) {

            assertNotNull("node id3." + nodeId + " should have subnode id4",
                    flattenedLipidStudies.itemAtPath(String.format("/items[id3.%s]/items[id4]", nodeId)));

            assertNotNull("node id3." + nodeId + " should have subnode id2." + nodeId,
                    flattenedLipidStudies.itemAtPath(String.format("/items[id3.%s]/items[id2.%s]", nodeId, nodeId)));

            assertNull("node id3." + nodeId + " should have subnode id2." + nodeId,
                    flattenedLipidStudies.itemAtPath(String.format("/items[id3.%s]/items[id2]", nodeId)));

        }

    }

    //9.4.1 in definitions specs
    @Test
    public void existenceRedefinition() throws Exception {
        Archetype emptyObservation = parse("openEHR-EHR-OBSERVATION.empty_observation.v1.0.0.adls");
        repository.addArchetype(emptyObservation);

        Archetype mandatory = parse("openEHR-EHR-OBSERVATION.protocol_mandatory.v1.0.0.adls");
        Archetype exclusion = parse("openEHR-EHR-OBSERVATION.protocol_exclusion.v1.0.0.adls");

        Archetype mandatoryFlat = new Flattener(repository, models).flatten(mandatory);
        Archetype exclusionFlat = new Flattener(repository, models).flatten(exclusion);

        CAttribute mandatoryProtocol = mandatoryFlat.getDefinition().getAttribute("protocol");
        assertTrue(mandatoryProtocol.getExistence().isMandatory());
        assertEquals(1, mandatoryProtocol.getChildren().size());

        CAttribute prohibitedProtocol = exclusionFlat.getDefinition().getAttribute("protocol");
        assertNotNull(prohibitedProtocol); //according to spec, prohibited existence should be logically removed (meaning not actually removed)
        assertTrue(prohibitedProtocol.getChildren().isEmpty());

    }


    @Test
    public void cardinalityRedefinition() throws Exception {
        Archetype cardinalityParent = parse("openEHR-EHR-CLUSTER.cardinality_parent.v1.0.0.adls");
        repository.addArchetype(cardinalityParent);
        Archetype specialized = parse("openEHR-EHR-CLUSTER.cardinality_specialized.v1.0.0.adls");

        Archetype flat = new Flattener(repository, models).flatten(specialized);

        CAttribute items = flat.getDefinition().getAttribute("items").getChildren().get(0).getAttribute("items");
        assertEquals(new Cardinality(3, 10), items.getCardinality());
        assertEquals(11, items.getChildren().size());
        assertEquals(new MultiplicityInterval(1, 1), items.getChild("id12.1").getOccurrences());
        assertEquals(new MultiplicityInterval(1, 1), items.getChild("id12.2").getOccurrences());
        assertEquals(new MultiplicityInterval(1, 1), items.getChild("id12.3").getOccurrences());
        assertEquals(new MultiplicityInterval(0, 1), items.getChild("id12.4").getOccurrences());
    }

    //ordering already fully covered in previous examples
    //node identifiers already fully covered in previous examples
    //occurrences redefinition already fully covered


    @Test
    public void exclusionRemoval() throws Exception {
        Archetype occurrencesParent = parse("openEHR-EHR-CLUSTER.occurrences_parent.v1.0.0.adls");
        repository.addArchetype(occurrencesParent);

        Archetype occurrencesSpecialized = parse("openEHR-EHR-CLUSTER.occurrences_specialized.v1.0.0.adls");

        Archetype flat = new Flattener(repository, models).removeZeroOccurrencesConstraints(true).flatten(occurrencesSpecialized);
        CAttribute attribute = flat.itemAtPath("/items[id3]/value");
        assertNotNull(flat.itemAtPath("/items[id3]/value[id5]"));
        assertNotNull(flat.itemAtPath("/items[id3]/value[id6]"));
        assertNull(flat.itemAtPath("/items[id3]/value[id4]"));
        assertNull(flat.itemAtPath("/items[id3]/value[id7]"));
        assertEquals(2, attribute.getChildren().size());

    }

    @Test
    public void exclusionDefault() throws Exception {
        Archetype occurrencesParent = parse("openEHR-EHR-CLUSTER.occurrences_parent.v1.0.0.adls");
        repository.addArchetype(occurrencesParent);

        Archetype occurrencesSpecialized = parse("openEHR-EHR-CLUSTER.occurrences_specialized.v1.0.0.adls");

        Archetype flat = new Flattener(repository, models).flatten(occurrencesSpecialized);
        CAttribute attribute = flat.itemAtPath("/items[id3]/value");
        assertNotNull(flat.itemAtPath("/items[id3]/value[id5]"));
        assertNotNull(flat.itemAtPath("/items[id3]/value[id6]"));
        assertNotNull(flat.itemAtPath("/items[id3]/value[id4]"));
        assertTrue(((CComplexObject) flat.itemAtPath("/items[id3]/value[id4]")).getOccurrences().isProhibited());
        assertEquals(0, ((CComplexObject) flat.itemAtPath("/items[id3]/value[id4]")).getAttributes().size());

        assertNotNull(flat.itemAtPath("/items[id3]/value[id7]"));
        assertEquals(0, ((CComplexObject) flat.itemAtPath("/items[id3]/value[id7]")).getAttributes().size());
        assertTrue(((CComplexObject) flat.itemAtPath("/items[id3]/value[id7]")).getOccurrences().isProhibited());
        assertEquals(4, attribute.getChildren().size());

    }


    //the spec has an issue here in the given examples which does not validate in the ADL workbench. Which means the test cannot be run yet until the spec issue has been resolved
    //because we don't know what it should do in this case
    @Test
    public void RMTypeRefinement() throws Exception {
        Archetype rmTypeRefinement = parse("openEHR-EHR-ELEMENT.type_refinement_parent.v1.0.0.adls");
        Archetype specialized = parse("openEHR-EHR-ELEMENT.type_refinement_specialized.v1.0.0.adls");
        repository.addArchetype(rmTypeRefinement);

        Archetype flat = new Flattener(repository, models).flatten(specialized);

        CAttribute value = flat.itemAtPath("/value");
        assertEquals(3, value.getChildren().size());

        //value with single constraint, child has three.

        //all three are descendants in the RM Of the value constraint, so they should match and take over any attributes of the parent
        for(CObject child:value.getChildren()) {
            CAttribute accuracyAttribute = child.getAttribute("accuracy");
            assertNotNull(child.getNodeId() + " should have accuraccy != null", accuracyAttribute);
            assertFalse(child.getNodeId() + " should have accuraccy !empty", accuracyAttribute.getChildren().isEmpty());
            CReal accuracy = (CReal) accuracyAttribute.getChildren().get(0);
            CReal parentAccuracy = rmTypeRefinement.itemAtPath("/value/accuracy[1]");
            assertEquals(parentAccuracy.getConstraints(), accuracy.getConstraints());
        }

    }


    //9.5.6. Internal Reference (Proxy Object) Redefinition
    @Test
    public void internalReferenceRedefinition() throws Exception {
        Archetype parent = parse("openEHR-EHR-ENTRY.reference_redefinition_parent.v1.0.0.adls");
        repository.addArchetype(parent);
        Archetype specialized = parse("openEHR-EHR-ENTRY.reference_redefinition_specialized.v1.0.0.adls");

        Archetype flat = new Flattener(repository, models).flatten(specialized);
        assertNotNull(flat.itemAtPath("/data[id3]/items[id4]"));
        assertNotNull(flat.itemAtPath("/data[id3]/items[id0.1]"));
        assertNull(flat.itemAtPath("/data[id2]/items[id0.1]"));
        CObject id3 = flat.itemAtPath("/data[id3]");
        assertEquals("the complex object proxy should have been replaced with a regular complex object", CComplexObject.class, id3.getClass());
    }

    @Test
    public void internalReferenceRedefinitionNoReplacement() throws Exception {
        Archetype parent = parse("openEHR-EHR-ENTRY.reference_redefinition_parent.v1.0.0.adls");
        repository.addArchetype(parent);
        Archetype specialized = parse("openEHR-EHR-ENTRY.reference_redefinition_no_replacement.v1.0.0.adls");
        Archetype flat = new Flattener(repository, models).flatten(specialized);

        ArchetypeModelObject cluster = flat.itemAtPath("/data[id3]");
        assertEquals(CComplexObjectProxy.class, cluster.getClass());

    }


    @Test
    public void numericPrimitiveRedefinition() throws Exception {
        Archetype parent = parse("openEHR-EHR-ELEMENT.numeric_primitive_parent.v1.0.0.adls");
        repository.addArchetype(parent);
        Archetype specialized = parse("openEHR-EHR-ELEMENT.numeric_primitive_specialized.v1.0.0.adls");
        Archetype flat = new Flattener(repository, models).flatten(specialized);

        CReal flatConstraint = flat.itemAtPath("/value[id3]/magnitude[1]");
        assertEquals(4.0d, flatConstraint.getConstraints().get(0).getLower(), 0.0001d);
        assertEquals(6.5d, flatConstraint.getConstraints().get(0).getUpper(), 0.0001d);

        CString units = flat.itemAtPath("/value[id3]/units[1]");
        assertNotNull(units);
        assertEquals(Lists.newArrayList("mmol/ml"), units.getConstraint());
    }

    @Test
    public void tupleRedefinition() throws Exception {
        Archetype parent = parse("openEHR-EHR-ELEMENT.tuple_parent.v1.0.0.adls");
        repository.addArchetype(parent);
        Archetype specialized = parse("openEHR-EHR-ELEMENT.tuple_specialized.v1.0.0.adls");
        Archetype flat = new Flattener(repository, models).flatten(specialized);
        //the tuple should be completely replaced with the new tuple
        //the attributes should be correct
        CComplexObject dvQuantity = flat.itemAtPath("/value[1]");
        assertEquals(1, dvQuantity.getAttributeTuples().size());
        CAttributeTuple tuple = dvQuantity.getAttributeTuples().get(0);
        assertEquals(1, tuple.getTuples().size());//only the mm[Hg] should be left
        assertEquals(Lists.newArrayList("magnitude", "units"), tuple.getMemberNames());
        assertEquals("socParent should have been updated for units", tuple, dvQuantity.getAttribute("units").getSocParent());
        assertEquals("socParent should have been updated for magnitude", tuple, dvQuantity.getAttribute("magnitude").getSocParent());

        assertEquals(1, dvQuantity.getAttribute("magnitude").getChildren().size());
        CReal magnitudeAttr = (CReal) dvQuantity.getAttribute("magnitude").getChildren().get(0);
        assertEquals(50.0d, magnitudeAttr.getConstraints().get(0).getLower(), 0.0001d);
        assertEquals(true, magnitudeAttr.getConstraints().get(0).isUpperUnbounded());

        assertEquals(1, dvQuantity.getAttribute("units").getChildren().size());
        CString unitsAttribute = (CString) dvQuantity.getAttribute("units").getChildren().get(0);
        assertEquals("mm[Hg]", unitsAttribute.getConstraint().get(0));

    }


    @Test
    public void addTuple() throws Exception {
        Archetype parent = parse("openEHR-EHR-ELEMENT.type_refinement_parent.v1.0.0.adls");
        repository.addArchetype(parent);
        Archetype specialized = parse("openEHR-EHR-ELEMENT.add_tuple.v1.0.0.adls");
        Archetype flat = new Flattener(repository, models).flatten(specialized);
        //the tuple should be completely replaced with the new tuple
        //the attributes should be correct
        CComplexObject dvQuantity = flat.itemAtPath("/value[1]");
        assertEquals(1, dvQuantity.getAttributeTuples().size());
        CAttributeTuple tuple = dvQuantity.getAttributeTuples().get(0);
        assertEquals(1, tuple.getTuples().size());//only the mm[Hg] should be left
        assertEquals(Lists.newArrayList("magnitude", "units"), tuple.getMemberNames());
        assertEquals("socParent should have been added for units", tuple, dvQuantity.getAttribute("units").getSocParent());
        assertEquals("socParent should have been added for magnitude", tuple, dvQuantity.getAttribute("magnitude").getSocParent());

        assertEquals(1, dvQuantity.getAttribute("magnitude").getChildren().size());
        CReal magnitudeAttr = (CReal) dvQuantity.getAttribute("magnitude").getChildren().get(0);
        assertEquals(4.0d, magnitudeAttr.getConstraints().get(0).getLower(), 0.0001d);
        assertEquals(8.0d, magnitudeAttr.getConstraints().get(0).getUpper(), 0.0001d);
        assertEquals(false, magnitudeAttr.getConstraints().get(0).isUpperUnbounded());

        assertEquals(1, dvQuantity.getAttribute("units").getChildren().size());
        CString unitsAttribute = (CString) dvQuantity.getAttribute("units").getChildren().get(0);
        assertEquals("mmol/ml", unitsAttribute.getConstraint().get(0));

    }

}
