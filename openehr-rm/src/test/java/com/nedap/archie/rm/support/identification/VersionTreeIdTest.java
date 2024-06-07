package com.nedap.archie.rm.support.identification;

import org.junit.Test;
import org.openehr.rm.support.identification.VersionTreeId;

import static org.junit.Assert.*;

public class VersionTreeIdTest {

    @Test
    public void isBranch() {
        VersionTreeId version = new VersionTreeId("1");
        assertEquals(Boolean.FALSE, version.isBranch());

        version = new VersionTreeId("1.1.1");
        assertEquals(Boolean.TRUE, version.isBranch());

        version = new VersionTreeId("123.123.123");
        assertEquals(Boolean.TRUE, version.isBranch());

        version = new VersionTreeId("2.0.0");
        assertEquals(Boolean.TRUE, version.isBranch());

        // invalid entries
        version = new VersionTreeId("0.0.1");
        assertThrows(IllegalArgumentException.class, version::isBranch);

        version = new VersionTreeId("1.");
        assertThrows(IllegalArgumentException.class, version::isBranch);

        version = new VersionTreeId("1.1");
        assertThrows(IllegalArgumentException.class, version::isBranch);

        version = new VersionTreeId("1.1.");
        assertThrows(IllegalArgumentException.class, version::isBranch);

        version = new VersionTreeId(".1.");
        assertThrows(IllegalArgumentException.class, version::isBranch);

        version = new VersionTreeId("..1");
        assertThrows(IllegalArgumentException.class, version::isBranch);
    }

    @Test
    public void getTrunkVersion() {
        VersionTreeId version = new VersionTreeId("1");
        assertEquals("1", version.getTrunkVersion());

        version = new VersionTreeId("43");
        assertEquals("43", version.getTrunkVersion());

        version = new VersionTreeId("1.1.1");
        assertEquals("1", version.getTrunkVersion());

        version = new VersionTreeId("123.456.789");
        assertEquals("123", version.getTrunkVersion());

        // invalid entries
        version = new VersionTreeId("0.0.1");
        assertThrows(IllegalArgumentException.class, version::getTrunkVersion);

        version = new VersionTreeId("1.");
        assertThrows(IllegalArgumentException.class, version::getTrunkVersion);
    }

    @Test
    public void getBranchNumber() {
        VersionTreeId version = new VersionTreeId("1.2.3");
        assertEquals("2", version.getBranchNumber());

        version = new VersionTreeId("123.456.789");
        assertEquals("456", version.getBranchNumber());
    }

    @Test
    public void getBranchVersion() {
        VersionTreeId version = new VersionTreeId("1.2.3");
        assertEquals("3", version.getBranchVersion());

        version = new VersionTreeId("123.456.789");
        assertEquals("789", version.getBranchVersion());
    }
}