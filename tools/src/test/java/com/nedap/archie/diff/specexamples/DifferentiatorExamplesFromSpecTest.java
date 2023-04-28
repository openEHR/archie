package com.nedap.archie.diff.specexamples;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.diff.DiffTestUtil;
import com.nedap.archie.diff.Differentiator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.flattener.specexamples.FlattenerTestUtil;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DifferentiatorExamplesFromSpecTest {

    private DiffTestUtil diffTestUtil;

    @Before
    public void setup() throws Exception {
        diffTestUtil = new DiffTestUtil("/com/nedap/archie/flattener/specexamples/", "/com/nedap/archie/diff/specexamples/");
    }

    @Test
    public void specializationPaths() throws Exception {
        diffTestUtil.test("openEHR-EHR-OBSERVATION.lab-test.v1.0.0.adls", "specialization_paths.adls");
    }

    @Test
    public void redefinitionForRefinement() throws Exception {
        diffTestUtil.testWithExplicitExpect("problem.adls", "diagnosis.adls");
    }

    //9.3.2. Redefinition for Specialisation has been fully covered by specialization paths and is not needed in tests

    //9.3.2.1. Specialisation with Cloning
    @Test
    public void specialisationWithCloning() throws Exception {
        diffTestUtil.testWithExplicitExpect("openEHR-EHR-CLUSTER.laboratory_test_panel.v1.0.0.adls", "openEHR-EHR-CLUSTER.lipid_studies_panel.adls");

    }

    //9.4.1 in definitions specs
    @Test
    public void existenceRedefinition() throws Exception {
        diffTestUtil.test("openEHR-EHR-OBSERVATION.empty_observation.v1.0.0.adls", "openEHR-EHR-OBSERVATION.protocol_mandatory.v1.0.0.adls");
        diffTestUtil.test("openEHR-EHR-OBSERVATION.empty_observation.v1.0.0.adls", "openEHR-EHR-OBSERVATION.protocol_exclusion.v1.0.0.adls");
    }


    @Test
    public void cardinalityRedefinition() throws Exception {
        diffTestUtil.testWithExplicitExpect("openEHR-EHR-CLUSTER.cardinality_parent.v1.0.0.adls","openEHR-EHR-CLUSTER.cardinality_specialized.v1.0.0.adls");
    }

    //ordering already fully covered in previous examples
    //node identifiers already fully covered in previous examples
    //occurrences redefinition already fully covered


    @Test
    public void exclusionRemoval() throws Exception {
        diffTestUtil.testWithExplicitExpect("openEHR-EHR-CLUSTER.occurrences_parent.v1.0.0.adls", "openEHR-EHR-CLUSTER.occurrences_specialized.v1.0.0.adls");
    }


    //9.5.6. Internal Reference (Proxy Object) Redefinition
    @Test
    public void internalReferenceRedefinition() throws Exception {
        diffTestUtil.test("openEHR-EHR-ENTRY.reference_redefinition_parent.v1.0.0.adls", "openEHR-EHR-ENTRY.reference_redefinition_specialized.v1.0.0.adls");
    }

    @Test
    public void internalReferenceRedefinitionNoReplacement() throws Exception {
        //diff generates an extra after statement for now, so explicit diff result!
        diffTestUtil.test("openEHR-EHR-ENTRY.reference_redefinition_parent.v1.0.0.adls", "openEHR-EHR-ENTRY.reference_redefinition_no_replacement.v1.0.0.adls");
    }


    @Test
    public void numericPrimitiveRedefinition() throws Exception {
        diffTestUtil.test("openEHR-EHR-ELEMENT.numeric_primitive_parent.v1.0.0.adls", "openEHR-EHR-ELEMENT.numeric_primitive_specialized.v1.0.0.adls");
    }

    @Test
    public void tupleRedefinition() throws Exception {
        diffTestUtil.testWithExplicitExpect("openEHR-EHR-ELEMENT.tuple_parent.v1.0.0.adls","openEHR-EHR-ELEMENT.tuple_specialized.v1.0.0.adls");
    }


    @Test
    public void addTuple() throws Exception {
        diffTestUtil.test("openEHR-EHR-ELEMENT.type_refinement_parent.v1.0.0.adls", "openEHR-EHR-ELEMENT.add_tuple.v1.0.0.adls");
    }

    @Test
    public void valueSets() throws Exception {
        diffTestUtil.test("openEHR-EHR-ELEMENT.internal_value_set_parent.v1.0.0.adls", "openEHR-EHR-ELEMENT.interval_value_set_specialized.v1.0.0.adls");
    }
}
