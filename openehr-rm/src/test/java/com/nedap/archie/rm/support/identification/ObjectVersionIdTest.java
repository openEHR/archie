package com.nedap.archie.rm.support.identification;

import org.junit.Test;
import org.openehr.rm.support.identification.ObjectVersionId;

import static org.junit.Assert.*;

public class ObjectVersionIdTest {

    @Test
    public void getObjectId() {
        ObjectVersionId versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359::local.archie.org::1");
        assertEquals("fd0c55b8-a396-4ca1-8928-e12b88148359", versionId.getObjectId().getValue());

        versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359", "local.archie.org", "1");
        assertEquals("fd0c55b8-a396-4ca1-8928-e12b88148359", versionId.getObjectId().getValue());
    }

    @Test
    public void getCreatingSystemId() {
        ObjectVersionId versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359::local.archie.org::1");
        assertEquals("local.archie.org", versionId.getCreatingSystemId().getValue());

        versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359", "local.archie.org", "1");
        assertEquals("local.archie.org", versionId.getCreatingSystemId().getValue());
    }

    @Test
    public void getVersionTreeId() {
        ObjectVersionId versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359::local.archie.org::1");
        assertEquals("1", versionId.getVersionTreeId().getValue());

        versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359", "local.archie.org", "1");
        assertEquals("1", versionId.getVersionTreeId().getValue());
    }

    @Test
    public void isBranch() {
        ObjectVersionId versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359::local.archie.org::1");
        assertEquals(false, versionId.isBranch());

        versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359::local.archie.org::1.1.1");
        assertEquals(true, versionId.isBranch());

        versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359", "local.archie.org", "1");
        assertEquals(false, versionId.isBranch());

        versionId = new ObjectVersionId("fd0c55b8-a396-4ca1-8928-e12b88148359", "local.archie.org", "1.1.1");
        assertEquals(true, versionId.isBranch());
    }
}