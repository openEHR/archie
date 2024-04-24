package com.nedap.archie.rm.support.identification;

import org.junit.Test;
import org.openehr.rm.support.identification.ArchetypeID;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ArchetypeIDTest {

    @Test
    public void buildValidArchetypeID() {
        ArchetypeID archetypeID = new ArchetypeID(
                "archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3-rc.42");
        assertEquals("archie.test.namespace",archetypeID.getNamespace());
        assertEquals("testRmPublisher",archetypeID.getRmOriginator());
        assertEquals("testRmPackage",archetypeID.getRmName());
        assertEquals("testRmClass",archetypeID.getRmEntity());
        assertEquals("testConceptId",archetypeID.getDomainConcept());
        assertEquals("1.2.3-rc.42",archetypeID.getVersionId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1.2.3-rc.42",
                archetypeID.getFullId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v1",
                archetypeID.getSemanticId());

        archetypeID = new ArchetypeID(
                "archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v");
        assertEquals("archie.test.namespace",archetypeID.getNamespace());
        assertEquals("testRmPublisher",archetypeID.getRmOriginator());
        assertEquals("testRmPackage",archetypeID.getRmName());
        assertEquals("testRmClass",archetypeID.getRmEntity());
        assertEquals("testConceptId",archetypeID.getDomainConcept());
        assertEquals("",archetypeID.getVersionId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v",
                archetypeID.getFullId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId.v",
                archetypeID.getSemanticId());


        archetypeID = new ArchetypeID(
                "archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId");
        assertEquals("archie.test.namespace",archetypeID.getNamespace());
        assertEquals("testRmPublisher",archetypeID.getRmOriginator());
        assertEquals("testRmPackage",archetypeID.getRmName());
        assertEquals("testRmClass",archetypeID.getRmEntity());
        assertEquals("testConceptId",archetypeID.getDomainConcept());
        assertNull(archetypeID.getVersionId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeID.getFullId());
        assertEquals("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeID.getSemanticId());


        archetypeID = new ArchetypeID(
                "testRmPublisher-testRmPackage-testRmClass.testConceptId");
        assertNull(archetypeID.getNamespace());
        assertEquals("testRmPublisher",archetypeID.getRmOriginator());
        assertEquals("testRmPackage",archetypeID.getRmName());
        assertEquals("testRmClass",archetypeID.getRmEntity());
        assertEquals("testConceptId",archetypeID.getDomainConcept());
        assertNull(archetypeID.getVersionId());
        assertEquals("testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeID.getFullId());
        assertEquals("testRmPublisher-testRmPackage-testRmClass.testConceptId",
                archetypeID.getSemanticId());

    }

    @Test
    public void buildInvalidArchetypeID() {
        List<String> invalidArchetypeIDs = new ArrayList<>();
        invalidArchetypeIDs.add("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass.v1.2.3-rc.42");
        invalidArchetypeIDs.add("archie.test.namespace::testRmPublisher-testRmPackage-testRmClass-someString.testConceptId.v1.2.3");
        invalidArchetypeIDs.add("testRmPublisher_testRmPackage_testRmClass-someString.testConceptId.v1.2.3");
        invalidArchetypeIDs.add("testRmPublisher-testRmPackage-testRmClass-someString..testConceptId.v1.2.3");
        invalidArchetypeIDs.add("testRmPackage-testRmClass.testConceptId.v1.2.3");

        for (String archetypeID : invalidArchetypeIDs) {
            try {
                new ArchetypeID(archetypeID);
                fail("Should not be able to construct ArchetypeHRID from " + archetypeID);
            } catch (IllegalArgumentException e) {
                //Nothing to be done here
            }
        }
    }

}

