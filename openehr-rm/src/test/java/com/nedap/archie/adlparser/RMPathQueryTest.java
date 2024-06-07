package com.nedap.archie.adlparser;

import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMPathQuery;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datavalues.DvText;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

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
        testUtil = new TestUtil(OpenEhrRmInfoLookup.getInstance());
        archetype = TestUtil.parseFailOnErrors(this.getClass(),"/basic.adl");
        new RMConstraintImposer().imposeConstraints(archetype.getDefinition());

    }

    @Test
    public void simpleTest() {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        assertEquals(composition.getContext(), new RMPathQuery("/context")
                .find(OpenEhrRmInfoLookup.getInstance(), composition));
//        EVENT_CONTEXT[id11] matches {
//            other_context matches {
//                ITEM_TREE[id2] matches {
//                    items matches {
//                        CLUSTER[id3] occurrences matches {0..*} matches {	-- Qualification
//                            items matches {
//                                ELEMENT[id4] occurrences matches {0..1} matches {	-- OrderID
        assertEquals(composition.getContext().getOtherContext().getItems(),
                new RMPathQuery("/context[id11]/other_context[id2]/items")
                        .find(OpenEhrRmInfoLookup.getInstance(), composition));
                //"/context[id2]/items[id3]/items[id4]"//should be one item

        DvText text = new RMPathQuery("/context[id11]/other_context[id2]/items[id3]/items[id5]/value")
                .find(OpenEhrRmInfoLookup.getInstance(), composition);
        assertNotNull(text);
    }

    @Test
    public void multipleItems() {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        {
            //add another cluster to the RM Object, with the same archetype id (very possible!)
            Composition composition2 = (Composition) testUtil.constructEmptyRMObject(archetype.getDefinition());
            ItemTree otherContext = (ItemTree) composition.getContext().getOtherContext();
            otherContext.getItems().addAll(composition2.getContext().getOtherContext().getItems());
        }

        ModelInfoLookup modelInfoLookup = OpenEhrRmInfoLookup.getInstance();

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

        // check that one result is returned when specifying an index, and with the correct path
        values = new RMPathQuery("/context[id11]/other_context[id2]/items[id3,1]/items[id5]/value").findList(modelInfoLookup, composition);
        assertEquals(1, values.size());
        assertEquals("/context/other_context[id2]/items[id3, 1]/items[id5, 2]/value", values.get(0).getPath());
        for(RMObjectWithPath value:values) {
            assertEquals(value.getObject(), new RMPathQuery(value.getPath()).findList(modelInfoLookup, composition).get(0).getObject());
        }
    }

    @Test
    public void findMatchSpecialisedNodes() throws Exception {
        InMemoryFullArchetypeRepository inMemoryFullArchetypeRepository = new InMemoryFullArchetypeRepository();
        inMemoryFullArchetypeRepository.addArchetype(archetype);
        Archetype archetype_specialised = TestUtil.parseFailOnErrors(this.getClass(),"/basic_specialised.adls");
        inMemoryFullArchetypeRepository.addArchetype(archetype_specialised);
        Flattener flattener = new Flattener(inMemoryFullArchetypeRepository, AllMetaModelsInitialiser.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
        OperationalTemplate opt = (OperationalTemplate) flattener.flatten(archetype_specialised);
        root = (Pathable) testUtil.constructEmptyRMObject(opt.getDefinition());

        Element element = new RMPathQuery("/context/other_context[id2]/items[id3]/items[id5]", true).find(OpenEhrRmInfoLookup.getInstance(), root);
        assertNotNull(element);
        assertEquals("/context/other_context[id2]/items[id3]/items[id5.1]", element.getPath());
    }

    @Test
    public void findListMatchSpecialisedNodes() throws Exception {
        InMemoryFullArchetypeRepository inMemoryFullArchetypeRepository = new InMemoryFullArchetypeRepository();
        inMemoryFullArchetypeRepository.addArchetype(archetype);
        Archetype archetype_specialised = TestUtil.parseFailOnErrors(this.getClass(),"/basic_specialised.adls");
        inMemoryFullArchetypeRepository.addArchetype(archetype_specialised);
        Flattener flattener = new Flattener(inMemoryFullArchetypeRepository, AllMetaModelsInitialiser.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
        OperationalTemplate opt = (OperationalTemplate) flattener.flatten(archetype_specialised);
        root = (Pathable) testUtil.constructEmptyRMObject(opt.getDefinition());

        List<RMObjectWithPath> list = new RMPathQuery("/context/other_context[id2]/items[id3]/items[id5]", true).findList(OpenEhrRmInfoLookup.getInstance(), root);
        assertEquals(1, list.size());
        assertEquals("/context/other_context[id2]/items[id3]/items[id5.1]", ((Element) list.get(0).getObject()).getPath());
    }

    @Test
    public void findListMatchTwiceSpecialisedNodes() throws Exception {
        InMemoryFullArchetypeRepository inMemoryFullArchetypeRepository = new InMemoryFullArchetypeRepository();
        inMemoryFullArchetypeRepository.addArchetype(archetype);
        Archetype archetype_specialised = TestUtil.parseFailOnErrors(this.getClass(),"/basic_specialised.adls");
        Archetype archetype_specialised_twice = TestUtil.parseFailOnErrors(this.getClass(),"/basic_specialised2.adls");
        inMemoryFullArchetypeRepository.addArchetype(archetype_specialised);
        inMemoryFullArchetypeRepository.addArchetype(archetype_specialised_twice);
        Flattener flattener = new Flattener(inMemoryFullArchetypeRepository, AllMetaModelsInitialiser.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
        OperationalTemplate opt = (OperationalTemplate) flattener.flatten(archetype_specialised_twice);
        root = (Pathable) testUtil.constructEmptyRMObject(opt.getDefinition());

        List<RMObjectWithPath> listId5 = new RMPathQuery("/context/other_context[id2]/items[id3]/items[id5]", true).findList(OpenEhrRmInfoLookup.getInstance(), root);
        assertEquals(2, listId5.size());
        assertEquals("/context/other_context[id2]/items[id3]/items[id5.1]", ((Element) listId5.get(0).getObject()).getPath());
        assertEquals("/context/other_context[id2]/items[id3]/items[id5.1.1]", ((Element) listId5.get(1).getObject()).getPath());

        List<RMObjectWithPath> listId6 = new RMPathQuery("/context/other_context[id2]/items[id3]/items[id6]", true).findList(OpenEhrRmInfoLookup.getInstance(), root);
        assertEquals(2, listId6.size());
        assertEquals("/context/other_context[id2]/items[id3]/items[id6]", ((Element) listId6.get(0).getObject()).getPath());
        assertEquals("/context/other_context[id2]/items[id3]/items[id6.0.1]", ((Element) listId6.get(1).getObject()).getPath());
    }

}
