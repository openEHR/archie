package com.nedap.archie.adlparser;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Created by pieter.bos on 15/10/15.
 */
public class MetadataTest {

    @Test
    public void idTest() throws IOException, ADLParseException {
        Archetype archetype = TestUtil.parseFailOnErrors("/com/nedap/archie/adlparser/openEHR-EHR-COMPOSITION.metadata_locatable_uid.v1.0.0.adls");

        assertEquals("openEHR-EHR-COMPOSITION.metadata_locatable_uid.v1.0.0", archetype.getArchetypeId().getFullId());
        assertNull(archetype.getArchetypeId().getNamespace());
        assertEquals("COMPOSITION", archetype.getArchetypeId().getRmClass());
        assertEquals("openEHR", archetype.getArchetypeId().getRmPublisher());
        assertEquals("EHR", archetype.getArchetypeId().getRmPackage());
        assertEquals("metadata_locatable_uid", archetype.getArchetypeId().getConceptId());
        assertEquals("1.0.0", archetype.getArchetypeId().getReleaseVersion());
    }

    @Test
    public void metadataTest() throws IOException, ADLParseException {
        Archetype archetype = TestUtil.parseFailOnErrors("/com/nedap/archie/adlparser/openEHR-EHR-COMPOSITION.metadata_locatable_uid.v1.0.0.adls");

        // (adl_version=2.0.5-alpha; rm_release= 1.0.2; generated; controlled; uid =a2bc5e00-c67e-4d7e-bb87-b3b74cdefd00;
        // build_uid = B430138C-1DD2-42EF-B54C-633909437054;reviewed_by ; Pieter=007; Bos=0.0.7; bbb007=b-b-b0-0-7)
        assertEquals("2.0.5-alpha", archetype.getAdlVersion());
        assertEquals("1.0.2", archetype.getRmRelease());
        assertTrue(archetype.getGenerated());
        assertTrue(archetype.getControlled());
        assertEquals("a2bc5e00-c67e-4d7e-bb87-b3b74cdefd00", archetype.getUid());
        assertEquals("B430138C-1DD2-42EF-B54C-633909437054", archetype.getBuildUid());
        Map<String, String> otherMetadata = archetype.getOtherMetaData();
        assertNull(otherMetadata.get("reviewed_by"));
        assertEquals("007", otherMetadata.get("Pieter"));
        assertEquals("0.0.7", otherMetadata.get("Bos"));
        assertEquals("b-b-b0-0-7", otherMetadata.get("bbb007"));
    }

    @Test
    public void locatableTest() throws IOException, ADLParseException {
        Archetype archetype = TestUtil.parseFailOnErrors("/com/nedap/archie/adlparser/openEHR-EHR-COMPOSITION.metadata_locatable_uid.v1.0.0.adls");

        // Test uid tag within locatable as attribute
        CComplexObject definition = archetype.getDefinition();
        // uid existence matches {1}
        assertTrue(definition.getAttribute("uid").isMandatory());
        // uid existence matches {0}
        assertFalse(definition.getAttribute("content").getChild("id2").getAttribute("uid").isMandatory());
    }

    @Test
    public void testInvalidMetadata() throws IOException {
        try {
            TestUtil.parseFailOnErrors("/com/nedap/archie/adlparser/openEHR-EHR-COMPOSITION.invalid_metadata.v1.0.0.adls");
        } catch (ADLParseException ex) {
            assertEquals("Error: Encountered metadata tag 'adl_version' with an invalid version id: null\n" +
                            "Error: Encountered metadata tag 'rm_release' with an invalid version id: 1-1-1-1-1\n" +
                            "Error: Encountered metadata tag 'generated' with a value assignment while expecting none\n" +
                            "Error: Encountered metadata tag 'controlled' with a value assignment while expecting none\n" +
                            "Error: Encountered metadata tag 'uid' with an invalid guid: 1.0.0-rc\n" +
                            "Error: Encountered metadata tag 'build_uid' with an invalid guid: null\n",
                    ex.getMessage());
        }
    }
}
