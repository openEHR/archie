package com.nedap.archie.query;

import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datavalues.DvText;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RMPathQueryTest {

    private Cluster cluster;
    private Element elementId2;
    private Element elementId3_1;
    private Element elementId3_2;
    private Element elementNoId_1;
    private Element elementNoId_2;
    private Element elementArchetypeId;

    @Before
    public void setup() {
        cluster = new Cluster();
        cluster.setArchetypeNodeId("id1");
        elementId2 = new Element();
        elementId2.setArchetypeNodeId("id2");
        elementId3_1 = new Element();
        elementId3_1.setArchetypeNodeId("id3.1");
        elementId3_2 = new Element();
        elementId3_2.setArchetypeNodeId("id3.1");
        elementNoId_1 = new Element();
        elementNoId_2 = new Element();
        elementArchetypeId = new Element();
        elementArchetypeId.setArchetypeNodeId("openEHR-EHR-ELEMENT.element.v1.0.0");

        cluster.addItem(elementNoId_1);
        cluster.addItem(elementId2);
        cluster.addItem(elementId3_1);
        cluster.addItem(elementId3_2);
        cluster.addItem(elementNoId_2);
        cluster.addItem(elementArchetypeId);
    }

    @Test
    public void adl2NodeIdMatch() {
        assertEquals(elementId2, cluster.itemAtPath("/items[id2]"));
        assertEquals(elementId3_1, cluster.itemAtPath("/items[id3.1]"));
        assertEquals(elementNoId_1, cluster.itemAtPath("/items[1]"));
        assertEquals(elementArchetypeId, cluster.itemAtPath("/items[openEHR-EHR-ELEMENT.element.v1.0.0]"));
    }

    @Test
    public void adl2NodeIdMatchList() {
        assertEquals(Collections.singletonList(elementId2), cluster.itemsAtPath("/items[id2]"));
        assertEquals(Arrays.asList(elementId3_1, elementId3_2), cluster.itemsAtPath("/items[id3.1]"));
        assertEquals(Arrays.asList(elementNoId_1, elementId2, elementId3_1, elementId3_2, elementNoId_2, elementArchetypeId), cluster.itemsAtPath("/items"));
        assertEquals(Collections.singletonList(elementArchetypeId), cluster.itemsAtPath("/items[openEHR-EHR-ELEMENT.element.v1.0.0]"));
    }

    @Test
    public void adl14NodeIdMatch() {
        Cluster cluster = new Cluster();
        cluster.setArchetypeNodeId("at0000");
        Element elementAt0001 = new Element();
        elementAt0001.setArchetypeNodeId("at0001");
        Element elementAt0002_3 = new Element();
        elementAt0002_3.setArchetypeNodeId("at0002.3");

        cluster.addItem(elementAt0001);
        cluster.addItem(elementAt0002_3);
        assertEquals(elementAt0001, cluster.itemAtPath("/items[at0001]"));
        assertEquals(elementAt0002_3, cluster.itemAtPath("/items[at0002.3]"));
    }

    @Test
    public void andNameIs() {
        Cluster cluster = new Cluster();
        cluster.setArchetypeNodeId("at0000");
        cluster.setName(new DvText("my name"));
        Element elementAt0001 = new Element();
        elementAt0001.setName(new DvText("some element"));
        elementAt0001.setArchetypeNodeId("at0001");
        Element elementAt0001_2 = new Element();
        elementAt0001_2.setName(new DvText("some other element"));
        elementAt0001_2.setArchetypeNodeId("at0001");

        cluster.addItem(elementAt0001);
        cluster.addItem(elementAt0001_2);
        APathQuery query = new APathQuery("/items[at0001 and name/value='some element']");
        assertEquals(elementAt0001, cluster.itemAtPath("/items[at0001 and name/value='some element']"));
        assertEquals(elementAt0001_2, cluster.itemAtPath("/items[at0001 and name/value='some other element']"));
    }
}
