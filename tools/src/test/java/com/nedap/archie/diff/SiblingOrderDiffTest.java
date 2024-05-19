package com.nedap.archie.diff;

import com.nedap.archie.testutil.DiffTestUtil;
import org.junit.Before;
import org.junit.Test;

public class SiblingOrderDiffTest {

    private DiffTestUtil diffTestUtil;

    @Before
    public void setup() throws Exception {
        diffTestUtil = new DiffTestUtil("/com/nedap/archie/flattener/siblingorder/", "/com/nedap/archie/diff/siblingorderexpectations/");
    }


    @Test
    public void testAnchor() throws Exception {
        diffTestUtil.testWithExplicitExpect(this.getClass(),"openEHR-EHR-CLUSTER.order-parent.v1.0.0.adls", "openEHR-EHR-CLUSTER.test_anchoring.v1.0.0.adls");
    }

    /**
     * A specialized archetype can reorder elements in the parent. Test that.
     *
     * @throws Exception
     */
    @Test
    public void reorderParentNodes() throws Exception {
        diffTestUtil.testWithExplicitExpect(this.getClass(),"openEHR-EHR-CLUSTER.order-parent.v1.0.0.adls", "openEHR-EHR-CLUSTER.reorder_parent_nodes.v1.0.0.adls");

    }

    /**
     * Test that redefined nodes appear at the same place, and extension nodes at the end
     * @throws Exception
     */
    @Test
    public void redefinitionAtSamePlace() throws Exception {
        diffTestUtil.test(this.getClass(),"openEHR-EHR-CLUSTER.order-parent.v1.0.0.adls","openEHR-EHR-CLUSTER.redefinition_at_same_place.v1.0.0.adls");
    }

    //The two tricky edge cases in the flattener test are really not interesting here, as the Differentiator will never create such hard to do code
}
