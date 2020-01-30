package com.nedap.archie.diff;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NodeIdLCSTest {

    @Test
    public void basicTest() {
        List<String> parent = Lists.newArrayList("id1", "id2", "id3", "id4", "id5");
        List<String> child = Lists.newArrayList("id3", "id2", "id0.4", "id1", "id4.1", "id5", "id0.3");

        NodeIdLCS LCS = new NodeIdLCS(parent, child, 1);
        assertEquals(Lists.newArrayList("id3", "id4", "id5"), LCS.getLCS());
    }
}
