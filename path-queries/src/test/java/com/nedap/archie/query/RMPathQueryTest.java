package com.nedap.archie.query;

import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RMPathQueryTest {

    @Test
    public void adl2NodeIdMatch() {
        Cluster cluster = new Cluster();
        cluster.setArchetypeNodeId("id1");
        Element elementId2 = new Element();
        elementId2.setArchetypeNodeId("id2");
        Element elementId3_1 = new Element();
        elementId3_1.setArchetypeNodeId("id3.1");

        cluster.addItem(elementId2);
        cluster.addItem(elementId3_1);
        assertEquals(elementId2, cluster.itemAtPath("/items[id2]"));
        assertEquals(elementId3_1, cluster.itemAtPath("/items[id3.1]"));
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
}
