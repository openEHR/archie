package com.nedap.archie.paths;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by stefan.teijgeler on 26/08/16.
 */
public class PathSegmentTest {

    @Test
    public void stringOfNodeName() {
        assertEquals("/data", new PathSegment("data").toString());
    }

    @Test
    public void stringOfNodeNameAndNodeId() {
        assertEquals("/data[id4]", new PathSegment("data", "id4").toString());
    }

    @Test
    public void stringOfNodeNameAndIndex() {
        assertEquals("/data[2]", new PathSegment("data", 2).toString());
    }

    @Test
    public void stringOfNodeNameIdAndIndex() {
        assertEquals("/data[id4,2]", new PathSegment("data", "id4", 2).toString());
    }
}
