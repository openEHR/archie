package com.nedap.archie.flattener;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.specexamples.FlattenerTestUtil;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SiblingOrderFlattenerTest {

    private static InMemoryFullArchetypeRepository repository;

    private Archetype parentArchetype;

    @Before
    public void setup() throws Exception {
        repository = new InMemoryFullArchetypeRepository();
        parentArchetype = parse("openEHR-EHR-CLUSTER.order-parent.v1.0.0.adls");
        ReferenceModels models = BuiltinReferenceModels.getAvailableModelInfoLookups();
        ValidationResult validationResult = new ArchetypeValidator(models).validate(parentArchetype, repository);
        assertTrue(validationResult.getErrors().toString(), validationResult.passes());
        repository.addArchetype(parentArchetype);

    }


    /**
     * A specialized archetype can reorder elements in the parent. Test that.
     *
     * @throws Exception
     */
    @Test
    public void reorderParentNodes() throws Exception {
        Archetype flat = parseAndFlatten("openEHR-EHR-CLUSTER.reorder_parent_nodes.v1.0.0.adls");
        List<CObject> children = flat.getDefinition().getAttribute("items").getChildren();
        List<String> nodeIds = children.stream().map((cobject) -> cobject.getNodeId()).collect(Collectors.toList());
        assertEquals(
                Lists.newArrayList("id4", "id3.1", "id5", "id2.1"),
                nodeIds
        );
    }

    /**
     * If an after element occurs, it should affect the ordering of all next elements, until the next sibling order
     * Same with before
     * @throws Exception
     */
    @Test
    public void anchoring() throws Exception {
        Archetype flat =  parseAndFlatten("openEHR-EHR-CLUSTER.test_anchoring.v1.0.0.adls");
        List<CObject> children = flat.getDefinition().getAttribute("items").getChildren();
        List<String> nodeIds = children.stream().map((cobject) -> cobject.getNodeId()).collect(Collectors.toList());
        assertEquals(
                Lists.newArrayList("id2", "id3", "id0.1", "id0.2", "id4", "id0.3", "id0.4", "id5"),
                nodeIds
        );
    }


    /**
     * Test that redefined nodes appear at the same place, and extension nodes at the end
     * TODO: spec does not mention if the original node should be before or after any specialized nodes!
     * @throws Exception
     */
    @Test
    public void redefinitionAtSamePlace() throws Exception {
        Archetype flat =  parseAndFlatten("openEHR-EHR-CLUSTER.redefinition_at_same_place.v1.0.0.adls");
        List<CObject> children = flat.getDefinition().getAttribute("items").getChildren();
        List<String> nodeIds = children.stream().map((cobject) -> cobject.getNodeId()).collect(Collectors.toList());
        assertEquals(
                Lists.newArrayList("id2.1", "id3", "id3.1", "id3.2", "id4", "id5", "id0.6"),
                nodeIds
        );

    }


    /**
     * Test an edge case where a before[id3] and a ELEMENT[id3.1] appear in the same child
     * This is unlikely to happen, but is valid ADL
     * @throws Exception
     */
    @Test
    public void trickyEdgeCase() throws Exception {
        Archetype flat =  parseAndFlatten("openEHR-EHR-CLUSTER.tricky_edge_case.v1.0.0.adls");
        List<CObject> children = flat.getDefinition().getAttribute("items").getChildren();
        List<String> nodeIds = children.stream().map((cobject) -> cobject.getNodeId()).collect(Collectors.toList());
        assertEquals(
                Lists.newArrayList("id0.6", "id3.1", "id0.5", "id0.7", "id2", "id4", "id5"),
                nodeIds
        );
    }

    /**
     * Same edge case as trickyEdgeCase, but with id3.1 in the reference instead of id3 :)
     * @throws Exception
     */
    @Test
    public void trickyEdgeCase2() throws Exception {
        Archetype flat =  parseAndFlatten("openEHR-EHR-CLUSTER.sibling_order_redefined_node_id.v1.0.0.adls");
        List<CObject> children = flat.getDefinition().getAttribute("items").getChildren();
        List<String> nodeIds = children.stream().map((cobject) -> cobject.getNodeId()).collect(Collectors.toList());
        assertEquals(
                Lists.newArrayList("id0.6", "id3.1", "id0.5", "id0.7", "id2", "id4", "id5"),
                nodeIds
        );
    }

    private Archetype parse(String fileName) throws IOException {
        return FlattenerTestUtil.parse("/com/nedap/archie/flattener/siblingorder/" + fileName);
    }

    private Archetype parseAndFlatten(String fileName) throws IOException {
        Archetype result = parse(fileName);
        ReferenceModels models = new ReferenceModels();
        models.registerModel(ArchieRMInfoLookup.getInstance());
        ValidationResult validationResult = new ArchetypeValidator(models).validate(result, repository);
        assertTrue(validationResult.getErrors().toString(), validationResult.passes());

        return new Flattener(repository, BuiltinReferenceModels.getAvailableModelInfoLookups()).flatten(parse(fileName));
    }
}
