package com.nedap.archie.rm.datastructures;

import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datavalues.DvText;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ClusterTest {


    @Test
    public void testEquals() {
        Element elementOne = new Element("elementOne", new DvText("textElementOne"), new DvText("valueElementOne"));
        Element elementTwo = new Element("elementTwo", new DvText("textElementTwo"), new DvText("valueElementTwo"));

        Cluster clusterOne = new Cluster("cluster", new DvText("Text1"), Arrays.asList(elementOne, elementTwo));
        Cluster clusterTwo = new Cluster("cluster", new DvText("Text1"), Arrays.asList(elementOne, elementTwo));
        Cluster clusterThree = new Cluster("cluster", new DvText("Text1"), null);

        assertEquals(clusterOne, clusterTwo);
        assertNotEquals(clusterOne, clusterThree);
        assertNotEquals(clusterThree, clusterTwo);
    }

}