package com.nedap.archie.query;

import com.google.common.collect.Lists;
import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import com.nedap.archie.xml.JAXBUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pieter.bos on 03/05/16.
 */
public class RMQueryContextTest {

    private TestUtil testUtil;
    private Archetype archetype;
    private Pathable root;

    @Before
    public void setup() throws Exception {
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
        archetype = new ADLParser(new RMConstraintImposer()).parse(getClass().getResourceAsStream("/basic.adl"));
        testUtil = new TestUtil();
    }

    @Test
    public void simpleTest() throws Exception {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        RMQueryContext queryContext = getQueryContext();
        assertEquals(Lists.newArrayList(composition.getContext()), queryContext.findList("/context"));
        DvText text = (DvText) queryContext.findList("/context/other_context/items[name/value = 'Qualification']/items[id5]/value").get(0);
        assertNotNull(text);
    }

    private RMQueryContext getQueryContext() {
        return new RMQueryContext(ArchieRMInfoLookup.getInstance(), root, JAXBUtil.getArchieJAXBContext());
    }

    @Test
    public void withDotInNodeId() throws Exception {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        ((Locatable) ((Composition) root).getContext().getOtherContext().getItems().get(0)).setArchetypeNodeId("id3.1");
        RMQueryContext queryContext = getQueryContext();
        assertEquals(Lists.newArrayList(composition.getContext()), queryContext.findList("/context"));
        DvText text = (DvText) queryContext.findList("/context/other_context/items[id3.1]/items[id5]/value").get(0);
        assertNotNull(text);
    }

    @Test
    public void multipleItems() throws Exception {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        {
            //add another cluster to the RM Object, with the same archetype id (very possible!)
            Composition composition2 = (Composition) testUtil.constructEmptyRMObject(archetype.getDefinition());
            composition.getContext().getOtherContext().getItems().addAll(composition2.getContext().getOtherContext().getItems());
        }

        RMQueryContext queryContext = getQueryContext();

        ModelInfoLookup modelInfoLookup = ArchieRMInfoLookup.getInstance();

        List<RMObjectWithPath> context = new RMPathQuery("/context").findList(modelInfoLookup, composition);
        assertEquals(1, context.size());
        assertEquals("/context", context.get(0).getPath());

        //now check that retrieving this retrieves more than one, even with the same ID.
        List items = queryContext.findList("/context/other_context[id2]/items");
        assertEquals(2, items.size());


        //and check that retrieving a sub-element also retrieves more than one element
        List<RMObjectWithPath> values = queryContext.findList("/context/other_context[id2]/items[id3]/items[id5]/value");
        assertEquals(2, values.size());
    }

    @Test
    public void withSimplePaths() throws Exception {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        {
            //add another cluster to the RM Object, with the same archetype id (very possible!)
            Composition composition2 = (Composition) testUtil.constructEmptyRMObject(archetype.getDefinition());
            composition.getContext().getOtherContext().getItems().addAll(composition2.getContext().getOtherContext().getItems());
        }

        RMQueryContext queryContext = getQueryContext();

        ModelInfoLookup modelInfoLookup = ArchieRMInfoLookup.getInstance();

        List<RMObjectWithPath> context = new RMPathQuery("/context").findList(modelInfoLookup, composition);
        assertEquals(1, context.size());
        assertEquals("/context", context.get(0).getPath());

        //now check that retrieving this retrieves more than one, even with the same ID.
        List<RMObjectWithPath> items = queryContext.findListWithPaths("/context/other_context[id2]/items");
        assertEquals(2, items.size());
        assertEquals("/context/other_context[id2]/items[id3,1]", items.get(0).getPath());
        assertEquals("/context/other_context[id2]/items[id3,2]", items.get(1).getPath());


        //and check that retrieving a sub-element also retrieves more than one element
        List<RMObjectWithPath> values = queryContext.findListWithPaths("/context/other_context[id2]/items[id3]/items[id5]/value");
        assertEquals("/context/other_context[id2]/items[id3,1]/items[id5,2]/value", values.get(0).getPath());
        assertEquals("/context/other_context[id2]/items[id3,2]/items[id5,2]/value", values.get(1).getPath());
        assertEquals(2, values.size());
    }
}
