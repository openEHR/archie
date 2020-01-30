package com.nedap.archie.diff;

import com.google.common.collect.Lists;
import com.nedap.archie.diff.Differentiator;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.SiblingOrder;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.flattener.specexamples.FlattenerTestUtil;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SiblingOrderDiffTest {

    private DiffTestUtil diffTestUtil;

    @Before
    public void setup() throws Exception {
        diffTestUtil = new DiffTestUtil("/com/nedap/archie/flattener/siblingorder/", "/com/nedap/archie/diff/siblingorderexpectations/");
    }


    @Test
    public void testAnchor() throws Exception {
        diffTestUtil.testWithExplicitExpect("openEHR-EHR-CLUSTER.order-parent.v1.0.0.adls", "openEHR-EHR-CLUSTER.test_anchoring.v1.0.0.adls");
    }

    /**
     * A specialized archetype can reorder elements in the parent. Test that.
     *
     * @throws Exception
     */
    @Test
    public void reorderParentNodes() throws Exception {
        diffTestUtil.testWithExplicitExpect("openEHR-EHR-CLUSTER.order-parent.v1.0.0.adls", "openEHR-EHR-CLUSTER.reorder_parent_nodes.v1.0.0.adls");

    }

    /**
     * Test that redefined nodes appear at the same place, and extension nodes at the end
     * @throws Exception
     */
    @Test
    public void redefinitionAtSamePlace() throws Exception {
        diffTestUtil.test("openEHR-EHR-CLUSTER.order-parent.v1.0.0.adls","openEHR-EHR-CLUSTER.redefinition_at_same_place.v1.0.0.adls");
    }

    //The two tricky edge cases in the flattener test are really not interesting here, as the Differentiator will never create such hard to do code
}
