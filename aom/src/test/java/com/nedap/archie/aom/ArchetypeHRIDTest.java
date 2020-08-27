package com.nedap.archie.aom;

import com.nedap.archie.definitions.VersionStatus;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ArchetypeHRIDTest {

    @Test
    public void buildValidArchetypeHRID() {
        ArchetypeHRID archetypeHRID = new ArchetypeHRID(
                "archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3-rc.42");
        assertEquals("archie.test.namespace",archetypeHRID.getNamespace());
        assertEquals("testRmPublisher",archetypeHRID.getRmPublisher());
        assertEquals("testRmPackage",archetypeHRID.getRmPackage());
        assertEquals("testRmClass",archetypeHRID.getRmClass());
        assertEquals("testConceptId",archetypeHRID.getConceptId());
        assertEquals("1.2.3",archetypeHRID.getReleaseVersion());
        assertEquals(VersionStatus.RELEASE_CANDIDATE,archetypeHRID.getVersionStatus());
        assertEquals("42",archetypeHRID.getBuildCount());
        assertEquals("1",archetypeHRID.getMajorVersion());
        assertEquals("2",archetypeHRID.getMinorVersion());
        assertEquals("3",archetypeHRID.getPatchVersion());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getIdUpToConcept());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3-rc.42",
                archetypeHRID.getFullId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1",
                archetypeHRID.getSemanticId());

        archetypeHRID = new ArchetypeHRID(
                "archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3+42");
        assertEquals("archie.test.namespace",archetypeHRID.getNamespace());
        assertEquals("testRmPublisher",archetypeHRID.getRmPublisher());
        assertEquals("testRmPackage",archetypeHRID.getRmPackage());
        assertEquals("testRmClass",archetypeHRID.getRmClass());
        assertEquals("testConceptId",archetypeHRID.getConceptId());
        assertEquals("1.2.3",archetypeHRID.getReleaseVersion());
        assertEquals(VersionStatus.BUILD,archetypeHRID.getVersionStatus());
        assertEquals("42",archetypeHRID.getBuildCount());
        assertEquals("1",archetypeHRID.getMajorVersion());
        assertEquals("2",archetypeHRID.getMinorVersion());
        assertEquals("3",archetypeHRID.getPatchVersion());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getIdUpToConcept());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3+42",
                archetypeHRID.getFullId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1",
                archetypeHRID.getSemanticId());

        archetypeHRID = new ArchetypeHRID(
                "archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3-alpha");
        assertEquals("archie.test.namespace",archetypeHRID.getNamespace());
        assertEquals("testRmPublisher",archetypeHRID.getRmPublisher());
        assertEquals("testRmPackage",archetypeHRID.getRmPackage());
        assertEquals("testRmClass",archetypeHRID.getRmClass());
        assertEquals("testConceptId",archetypeHRID.getConceptId());
        assertEquals("1.2.3",archetypeHRID.getReleaseVersion());
        assertEquals(VersionStatus.ALPHA,archetypeHRID.getVersionStatus());
        assertEquals("",archetypeHRID.getBuildCount());
        assertEquals("1",archetypeHRID.getMajorVersion());
        assertEquals("2",archetypeHRID.getMinorVersion());
        assertEquals("3",archetypeHRID.getPatchVersion());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getIdUpToConcept());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3-alpha",
                archetypeHRID.getFullId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1",
                archetypeHRID.getSemanticId());

        archetypeHRID = new ArchetypeHRID(
                "archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3");
        assertEquals("archie.test.namespace",archetypeHRID.getNamespace());
        assertEquals("testRmPublisher",archetypeHRID.getRmPublisher());
        assertEquals("testRmPackage",archetypeHRID.getRmPackage());
        assertEquals("testRmClass",archetypeHRID.getRmClass());
        assertEquals("testConceptId",archetypeHRID.getConceptId());
        assertEquals("1.2.3",archetypeHRID.getReleaseVersion());
        assertEquals(VersionStatus.RELEASED,archetypeHRID.getVersionStatus());
        assertEquals("",archetypeHRID.getBuildCount());
        assertEquals("1",archetypeHRID.getMajorVersion());
        assertEquals("2",archetypeHRID.getMinorVersion());
        assertEquals("3",archetypeHRID.getPatchVersion());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getIdUpToConcept());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3",
                archetypeHRID.getFullId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1",
                archetypeHRID.getSemanticId());

        archetypeHRID = new ArchetypeHRID(
                "archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v");
        assertEquals("archie.test.namespace",archetypeHRID.getNamespace());
        assertEquals("testRmPublisher",archetypeHRID.getRmPublisher());
        assertEquals("testRmPackage",archetypeHRID.getRmPackage());
        assertEquals("testRmClass",archetypeHRID.getRmClass());
        assertEquals("testConceptId",archetypeHRID.getConceptId());
        assertEquals("",archetypeHRID.getReleaseVersion());
        assertNull(archetypeHRID.getMajorVersion());
        assertNull(archetypeHRID.getMinorVersion());
        assertNull(archetypeHRID.getPatchVersion());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getIdUpToConcept());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v",
                archetypeHRID.getFullId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v",
                archetypeHRID.getSemanticId());

        archetypeHRID = new ArchetypeHRID(
                "archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId");
        assertEquals("archie.test.namespace",archetypeHRID.getNamespace());
        assertEquals("testRmPublisher",archetypeHRID.getRmPublisher());
        assertEquals("testRmPackage",archetypeHRID.getRmPackage());
        assertEquals("testRmClass",archetypeHRID.getRmClass());
        assertEquals("testConceptId",archetypeHRID.getConceptId());
        assertNull(archetypeHRID.getReleaseVersion());
        assertNull(archetypeHRID.getMajorVersion());
        assertNull(archetypeHRID.getMinorVersion());
        assertNull(archetypeHRID.getPatchVersion());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getIdUpToConcept());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getFullId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getSemanticId());


        archetypeHRID = new ArchetypeHRID(
                "testRmPublisher-testRmPackage-testRmClass.testConceptId");
        assertNull(archetypeHRID.getNamespace());
        assertEquals("testRmPublisher",archetypeHRID.getRmPublisher());
        assertEquals("testRmPackage",archetypeHRID.getRmPackage());
        assertEquals("testRmClass",archetypeHRID.getRmClass());
        assertEquals("testConceptId",archetypeHRID.getConceptId());
        assertNull(archetypeHRID.getReleaseVersion());
        assertNull(archetypeHRID.getMajorVersion());
        assertNull(archetypeHRID.getMinorVersion());
        assertNull(archetypeHRID.getPatchVersion());
        assertEquals("testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getIdUpToConcept());
        assertEquals("testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getFullId());
        assertEquals("testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeHRID.getSemanticId());

    }

    @Test
    public void buildInvalidArchetypeHRID() {
        List<String> invalidArchetypeHRIDs = new ArrayList<>();
        invalidArchetypeHRIDs.add("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.v1.2.3-rc.42");
        invalidArchetypeHRIDs.add("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass-someString.testConceptId.v1.2.3");
        invalidArchetypeHRIDs.add("testRmPublisher_testRmPackage_testRmClass-someString.testConceptId.v1.2.3");
        invalidArchetypeHRIDs.add("testRmPublisher-testRmPackage-testRmClass-someString..testConceptId.v1.2.3");
        invalidArchetypeHRIDs.add("testRmPackage-testRmClass.testConceptId.v1.2.3");

        for (String archetypeHRID : invalidArchetypeHRIDs) {
            try {
                new ArchetypeHRID(archetypeHRID);
                fail("Should not be able to construct ArchetypeHRID from " + archetypeHRID);
            } catch (IllegalArgumentException e) {
                //Nothing to be done here
            }
        }
    }

}
