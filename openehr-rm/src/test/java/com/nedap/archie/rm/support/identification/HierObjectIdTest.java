package com.nedap.archie.rm.support.identification;

import org.junit.Test;
import org.openehr.rm.support.identification.HierObjectId;

import static org.junit.Assert.*;

public class HierObjectIdTest {

    @Test
    public void getRoot() {
        HierObjectId id = new HierObjectId("fd0c55b8-a396-4ca1-8928-e12b88148359::local.archie.org::1");
        assertEquals("fd0c55b8-a396-4ca1-8928-e12b88148359", id.getRoot().getValue());

        HierObjectId id2 = new HierObjectId("fd0c55b8-a396-4ca1-8928-e12b88148359");
        assertEquals("fd0c55b8-a396-4ca1-8928-e12b88148359", id2.getRoot().getValue());
    }

    @Test
    public void getExtension() {
        HierObjectId id = new HierObjectId("fd0c55b8-a396-4ca1-8928-e12b88148359::local.archie.org::1");
        assertEquals("local.archie.org::1", id.getExtension());

        HierObjectId id2 = new HierObjectId("fd0c55b8-a396-4ca1-8928-e12b88148359");
        assertEquals("", id2.getExtension());
    }

    @Test
    public void hasExtension() {
        HierObjectId id = new HierObjectId("fd0c55b8-a396-4ca1-8928-e12b88148359::local.archie.org::1");
        assertTrue(id.hasExtension());

        HierObjectId id2 = new HierObjectId("fd0c55b8-a396-4ca1-8928-e12b88148359");
        assertFalse(id2.hasExtension());
    }
}