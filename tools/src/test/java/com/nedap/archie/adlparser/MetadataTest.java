package com.nedap.archie.adlparser;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

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
        //(adl_version=2.0.5-alpha; rm_release=1.0.2; generated; controlled; uid=a2bc5e00-c67e-4d7e-bb87-b3b74cdefd00; build_uid=B430138C-1DD2-42EF-B54C-633909437054;
        // adl_version=1.0;rm_release= 2;generated= 3;controlled = 4.4.4;uid=5;build_uid=6.0.0;identifier=a2bc5e00-c67e-4d7e-bb87-b3b74cdefd00)
        assertEquals("2.0.5-alpha", archetype.getAdlVersion());
        assertEquals("1.0.2", archetype.getRmRelease());
        assertTrue(archetype.getGenerated());
        assertTrue(archetype.getControlled());
        assertEquals("a2bc5e00-c67e-4d7e-bb87-b3b74cdefd00", archetype.getUid());
        assertEquals("B430138C-1DD2-42EF-B54C-633909437054", archetype.getBuildUid());
        Map<String, String> otherMetadata = archetype.getOtherMetaData();
        assertEquals("1.0", otherMetadata.get("adl_version"));
        assertEquals("2", otherMetadata.get("rm_release"));
        assertEquals("3", otherMetadata.get("generated"));
        assertEquals("4.4.4", otherMetadata.get("controlled"));
        assertEquals("5", otherMetadata.get("uid"));
        assertEquals("6.0.0", otherMetadata.get("build_uid"));
        assertEquals("a2bc5e00-c67e-4d7e-bb87-b3b74cdefd00", otherMetadata.get("identifier"));
    }

}
