package com.nedap.archie.paths;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathUtilTest {

    @Test
    public void primitiveNodeIdsAreLeftOut() {
        assertEquals("/items[id2]/value", PathUtil.getPath(Arrays.asList(new PathSegment("items", "id2"), new PathSegment("value", "id9999"))));
        assertEquals("/items[at0002]/value", PathUtil.getPath(Arrays.asList(new PathSegment("items", "at0002"), new PathSegment("value", "at9999"))));
    }
}
