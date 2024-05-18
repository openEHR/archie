package com.nedap.archie.rm;

import com.google.common.collect.Lists;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.Observation;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.History;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datastructures.PointEvent;
import org.openehr.rm.datavalues.DvText;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by pieter.bos on 10/11/15.
 */
public class LocatableTest {

    @Test
    public void testItemAtPath() {
        Composition composition = new Composition();
        Observation item = new Observation();
        composition.addContent(item);
        item.setArchetypeNodeId("id1");
        History<ItemStructure> history = new History<>();

        item.setData(history);

        history.setArchetypeNodeId("id2");
        PointEvent<ItemStructure> event = new PointEvent<>();
        history.addEvent(event);
        event.setArchetypeNodeId("id3");
        event.setNameAsString("custom event");
        ItemTree itemTree = new ItemTree();
        event.setData(itemTree);
        itemTree.setArchetypeNodeId("id4");

        Element element = new Element();
        itemTree.addItem(element);
        element.setArchetypeNodeId("id5");
        DvText text = new DvText();
        text.setValue("test");
        element.setValue(text);


        History<ItemStructure> state = new History<>();
        item.setState(state);

        assertEquals(history, composition.itemAtPath("/content[id1]/data[id2]"));
        assertEquals(event, composition.itemAtPath("/content[id1]/data[id2]/events[id3]"));
        assertEquals(itemTree, composition.itemAtPath("/content[id1]/data[id2]/events[id3]/data[id4]"));
        assertEquals(itemTree, composition.itemAtPath("/content[id1]/data[id2]/events[1]/data[id4]"));
        assertEquals(itemTree, composition.itemAtPath("/content[id1]/data[id2]/events[\"custom event\"]/data[id4]"));
        assertEquals(element, composition.itemAtPath("/content[id1]/data[id2]/events[id3]/data[id4]/items[id5]"));
        assertEquals(text, composition.itemAtPath("/content[id1]/data[id2]/events[id3]/data[id4]/items[id5]/value"));

//        state.setArchetypeNodeId("id4");
    }

    @Test
    public void testItemsAtPath() {
        Composition composition = new Composition();
        Observation item = new Observation();
        composition.addContent(item);
        item.setArchetypeNodeId("id1");
        History<ItemStructure> history = new History<>();

        item.setData(history);

        history.setArchetypeNodeId("id2");
        PointEvent<ItemStructure> event = new PointEvent<>();
        history.addEvent(event);
        event.setArchetypeNodeId("id3");
        event.setNameAsString("custom event");
        ItemTree itemTree = new ItemTree();
        event.setData(itemTree);
        itemTree.setArchetypeNodeId("id4");

        Element element = new Element();
        itemTree.addItem(element);
        element.setArchetypeNodeId("id5");
        DvText text = new DvText();
        text.setValue("test");
        element.setValue(text);


        History<ItemStructure> state = new History<>();
        item.setState(state);

        assertEquals(Lists.newArrayList(history), composition.itemsAtPath("/content[id1]/data[id2]"));
        assertEquals(Lists.newArrayList(event), composition.itemsAtPath("/content[id1]/data[id2]/events[id3]"));
        assertEquals(Lists.newArrayList(itemTree), composition.itemsAtPath("/content[id1]/data[id2]/events[id3]/data[id4]"));
        assertEquals(Lists.newArrayList(itemTree), composition.itemsAtPath("/content[id1]/data[id2]/events[1]/data[id4]"));
        assertEquals(Lists.newArrayList(itemTree), composition.itemsAtPath("/content[id1]/data[id2]/events[\"custom event\"]/data[id4]"));
        assertEquals(Lists.newArrayList(element), composition.itemsAtPath("/content[id1]/data[id2]/events[id3]/data[id4]/items[id5]"));
        assertEquals(Lists.newArrayList(text), composition.itemsAtPath("/content[id1]/data[id2]/events[id3]/data[id4]/items[id5]/value"));

//        state.setArchetypeNodeId("id4");
    }
}
