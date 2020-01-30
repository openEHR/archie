package com.nedap.archie.adlparser;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by pieter.bos on 15/10/15.
 */
public class MetadataTest {

    private Archetype archetype;

    @Before
    public void setup() throws Exception {
        archetype = TestUtil.parseFailOnErrors("/basic.adl");;
    }

    @Test
    public void idTest() throws Exception {

        assertEquals("openEHR-EHR-COMPOSITION.annotations_rm_path.v1.0.0", archetype.getArchetypeId().getFullId());
        assertEquals(null, archetype.getArchetypeId().getNamespace());
        assertEquals("COMPOSITION", archetype.getArchetypeId().getRmClass());
        assertEquals("openEHR", archetype.getArchetypeId().getRmPublisher());
        assertEquals("EHR", archetype.getArchetypeId().getRmPackage());
        assertEquals("annotations_rm_path", archetype.getArchetypeId().getConceptId());
        assertEquals("1.0.0", archetype.getArchetypeId().getReleaseVersion());
    }

    @Test
    public void metadataTest() throws Exception {
        //(adl_version=2.0.5; rm_release=1.0.2)
        assertEquals("2.0.5", archetype.getAdlVersion());
        assertEquals("1.0.2", archetype.getRmRelease());
    }

}
