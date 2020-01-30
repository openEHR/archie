package com.nedap.archie.adlparser;

import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test APath query with RM Objects
 * Created by pieter.bos on 06/04/16.
 */
public class RMPathQueryTest {


    private TestUtil testUtil;
    private Archetype archetype;
    private Pathable root;

    @Before
    public void setup() throws Exception {
        testUtil = new TestUtil();
        archetype = TestUtil.parseFailOnErrors("/basic.adl");
        new RMConstraintImposer().imposeConstraints(archetype.getDefinition());

    }

    @Test
    public void simpleTest() {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        assertEquals(composition.getContext(), new RMPathQuery("/context")
                .find(ArchieRMInfoLookup.getInstance(), composition));
//        EVENT_CONTEXT[id11] matches {
//            other_context matches {
//                ITEM_TREE[id2] matches {
//                    items matches {
//                        CLUSTER[id3] occurrences matches {0..*} matches {	-- Qualification
//                            items matches {
//                                ELEMENT[id4] occurrences matches {0..1} matches {	-- OrderID
        assertEquals(composition.getContext().getOtherContext().getItems(),
                new RMPathQuery("/context[id11]/other_context[id2]/items")
                        .find(ArchieRMInfoLookup.getInstance(), composition));
                //"/context[id2]/items[id3]/items[id4]"//should be one item

        DvText text = new RMPathQuery("/context[id11]/other_context[id2]/items[id3]/items[id5]/value")
                .find(ArchieRMInfoLookup.getInstance(), composition);
        assertNotNull(text);
    }

    @Test
    public void multipleItems() {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        {
            //add another cluster to the RM Object, with the same archetype id (very possible!)
            Composition composition2 = (Composition) testUtil.constructEmptyRMObject(archetype.getDefinition());
            composition.getContext().getOtherContext().getItems().addAll(composition2.getContext().getOtherContext().getItems());
        }

        ModelInfoLookup modelInfoLookup = ArchieRMInfoLookup.getInstance();

        List<RMObjectWithPath> context = new RMPathQuery("/context")
                .findList(modelInfoLookup, composition);
        assertEquals(1, context.size());
        assertEquals("/context", context.get(0).getPath());

        //now check that retrieving this retrieves more than one, even with the same ID.
        List<RMObjectWithPath> items = new RMPathQuery("/context[id11]/other_context[id2]/items").findList(modelInfoLookup, composition);
        assertEquals(2, items.size());
        assertEquals("/context/other_context[id2]/items[id3, 1]", items.get(0).getPath());
        assertEquals("/context/other_context[id2]/items[id3, 2]", items.get(1).getPath());
        for(RMObjectWithPath value:items) {
            assertEquals(value.getObject(), new RMPathQuery(value.getPath()).findList(modelInfoLookup, composition).get(0).getObject());
        }


        //and check that retrieving a sub-element also retrieves more than one element
        List<RMObjectWithPath> values = new RMPathQuery("/context[id11]/other_context[id2]/items[id3]/items[id5]/value").findList(modelInfoLookup, composition);
        assertEquals(2, values.size());
        assertEquals("/context/other_context[id2]/items[id3, 1]/items[id5, 2]/value", values.get(0).getPath());
        assertEquals("/context/other_context[id2]/items[id3, 2]/items[id5, 2]/value", values.get(1).getPath());
        for(RMObjectWithPath value:values) {
            assertEquals(value.getObject(), new RMPathQuery(value.getPath()).findList(modelInfoLookup, composition).get(0).getObject());
        }
    }


}
