package com.nedap.archie.rm.datastructures;

import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.ItemTable;
import org.openehr.rm.datavalues.DvText;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ItemTableTest {

    @Test
    public void testequals() {

        Element elementOne = new Element("elementOne", new DvText("textElementOne"), new DvText("valueElementOne"));
        Element elementTwo = new Element("elementTwo", new DvText("textElementTwo"), new DvText("valueElementTwo"));

        Cluster clusterOne = new Cluster("cluster", new DvText("Text1"), Arrays.asList(elementOne));
        Cluster clusterTwo = new Cluster("cluster", new DvText("Text1"), Arrays.asList(elementOne, elementTwo));
        Cluster clusterThree = new Cluster("cluster", new DvText("Text1"), null);


        ItemTable itemTableOne = new ItemTable("itemTable", new DvText("Text2"), Arrays.asList(clusterOne, clusterTwo, clusterThree));
        ItemTable itemTableTwo = new ItemTable("itemTable", new DvText("Text2"), Arrays.asList(clusterOne, clusterTwo, clusterThree));

        assertEquals(itemTableOne, itemTableTwo);
    }
}