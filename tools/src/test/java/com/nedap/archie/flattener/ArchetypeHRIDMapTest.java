package com.nedap.archie.flattener;

import com.nedap.archie.aom.ArchetypeHRID;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author vera.prinsen
 * Created on 10/06/2020
 */
public class ArchetypeHRIDMapTest {

    @Test
    public void getLatestMajorVersion() {
        ArchetypeHRIDMap<Integer> map = new ArchetypeHRIDMap<>();
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3"), 0);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.1.4"), 1);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.1.3"), 2);
        assertEquals(Integer.valueOf(0), map.getLatestVersion("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1"));
    }

    @Test
    public void getLatestMinorVersion() {
        ArchetypeHRIDMap<Integer> map = new ArchetypeHRIDMap<>();
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3"), 0);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.1.4"), 1);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.1.3"), 2);
        assertEquals(Integer.valueOf(1), map.getLatestVersion("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.1"));
    }

    @Test
    public void getLatestPatchVersion() {
        ArchetypeHRIDMap<Integer> map = new ArchetypeHRIDMap<>();
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3"), 0);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.1.4"), 1);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.1.3"), 2);
        assertEquals(Integer.valueOf(2), map.getLatestVersion("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.1.3"));
    }

    @Test
    public void getLatestVersionInvalid() {
        ArchetypeHRIDMap<Integer> map = new ArchetypeHRIDMap<>();
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3"), 0);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4"), 1);
        assertNull(map.getLatestVersion("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.5"));
    }

    @Test
    public void getLatestVersionAlpha() {
        ArchetypeHRIDMap<Integer> map = new ArchetypeHRIDMap<>();
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3"), 0);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-alpha"), 1);
        assertEquals(Integer.valueOf(1), map.getLatestVersion("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4"));
    }

    @Test
    public void getLatestVersionBeta() {
        ArchetypeHRIDMap<Integer> map = new ArchetypeHRIDMap<>();
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-alpha"), 0);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-beta"), 1);
        assertEquals(Integer.valueOf(1), map.getLatestVersion("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4"));
    }

    @Test
    public void getLatestVersionReleaseCandidate() {
        ArchetypeHRIDMap<Integer> map = new ArchetypeHRIDMap<>();
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-alpha"), 0);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-beta"), 1);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-rc"), 2);
        assertEquals(Integer.valueOf(2), map.getLatestVersion("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4"));
    }

    @Test
    public void getLatestVersionReleased() {
        ArchetypeHRIDMap<Integer> map = new ArchetypeHRIDMap<>();
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-alpha"), 0);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-beta"), 1);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-rc"), 2);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4"), 3);
        assertEquals(Integer.valueOf(3), map.getLatestVersion("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4"));
    }

    @Test
    public void getLatestVersionBuild() {
        ArchetypeHRIDMap<Integer> map = new ArchetypeHRIDMap<>();
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-alpha"), 0);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-beta"), 1);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4-rc"), 2);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4"), 3);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4+24"), 4);
        map.put(new ArchetypeHRID("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4+32"), 5);
        assertEquals(Integer.valueOf(5), map.getLatestVersion("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.4"));
    }
}
