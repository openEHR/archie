package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.profile.AomProfiles;
import com.nedap.archie.openehrtestrm.TestRMInfoLookup;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleMetaModelProviderTest {

    @Test
    public void testConstructors() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new SimpleMetaModelProvider(null, null));
        assertEquals("Either referenceModels or bmmRepository must be provided", ex.getMessage());

        ex = assertThrows(IllegalArgumentException.class, () -> new SimpleMetaModelProvider(null, null, new AomProfiles()));
        assertEquals("Either referenceModels or bmmRepository must be provided", ex.getMessage());
    }

    @Test
    public void testGetMetaModel() {
        MetaModelProvider metaModelProvider = new SimpleMetaModelProvider(
                BuiltinReferenceModels.getAvailableModelInfoLookups(),
                BuiltinReferenceModels.getBmmRepository(),
                BuiltinReferenceModels.getAomProfiles()
        );

        Archetype archetype = new Archetype();
        archetype.setArchetypeId(new ArchetypeHRID("openEHR-EHR-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.3");

        MetaModel metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof ArchieRMInfoLookup);
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.3", metaModel.getBmmModel().getRmRelease());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNotNull(metaModel.getOdinInputObjectMapper());
        assertNotNull(metaModel.getOdinOutputObjectMapper());
        assertNotNull(metaModel.getJsonObjectMapper());

        archetype.setRmRelease("1.0.4");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof ArchieRMInfoLookup);
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.4", metaModel.getBmmModel().getRmRelease());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNotNull(metaModel.getOdinInputObjectMapper());
        assertNotNull(metaModel.getOdinOutputObjectMapper());
        assertNotNull(metaModel.getJsonObjectMapper());

        archetype.setRmRelease("1.99.1");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof ArchieRMInfoLookup);
        assertNull(metaModel.getBmmModel());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNotNull(metaModel.getOdinInputObjectMapper());
        assertNotNull(metaModel.getOdinOutputObjectMapper());
        assertNotNull(metaModel.getJsonObjectMapper());

        archetype.setArchetypeId(new ArchetypeHRID("openEHR-TEST_PKG-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.2");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof TestRMInfoLookup);
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("TEST_PKG", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.2", metaModel.getBmmModel().getRmRelease());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNull(metaModel.getOdinInputObjectMapper());
        assertNull(metaModel.getOdinOutputObjectMapper());
        assertNull(metaModel.getJsonObjectMapper());

        archetype.setArchetypeId(new ArchetypeHRID("other-OTHER-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("0.9.0");

        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class, () -> metaModelProvider.getMetaModel(archetype));
        assertEquals("model for other.OTHER version 0.9.0 not found", ex.getMessage());
    }

    @Test
    public void testGetMetaModelModelInfoLookupOnly() {
        MetaModelProvider metaModelProvider = new SimpleMetaModelProvider(
                BuiltinReferenceModels.getAvailableModelInfoLookups(),
                null,
                BuiltinReferenceModels.getAomProfiles()
        );

        Archetype archetype = new Archetype();
        archetype.setArchetypeId(new ArchetypeHRID("openEHR-EHR-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.3");

        MetaModel metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof ArchieRMInfoLookup);
        assertNull(metaModel.getBmmModel());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNotNull(metaModel.getOdinInputObjectMapper());
        assertNotNull(metaModel.getOdinOutputObjectMapper());
        assertNotNull(metaModel.getJsonObjectMapper());

        archetype.setRmRelease("1.0.4");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof ArchieRMInfoLookup);
        assertNull(metaModel.getBmmModel());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNotNull(metaModel.getOdinInputObjectMapper());
        assertNotNull(metaModel.getOdinOutputObjectMapper());
        assertNotNull(metaModel.getJsonObjectMapper());

        archetype.setRmRelease("1.99.1");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof ArchieRMInfoLookup);
        assertNull(metaModel.getBmmModel());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNotNull(metaModel.getOdinInputObjectMapper());
        assertNotNull(metaModel.getOdinOutputObjectMapper());
        assertNotNull(metaModel.getJsonObjectMapper());

        archetype.setArchetypeId(new ArchetypeHRID("openEHR-TEST_PKG-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.2");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof TestRMInfoLookup);
        assertNull(metaModel.getBmmModel());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNull(metaModel.getOdinInputObjectMapper());
        assertNull(metaModel.getOdinOutputObjectMapper());
        assertNull(metaModel.getJsonObjectMapper());

        archetype.setArchetypeId(new ArchetypeHRID("other-OTHER-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("0.9.0");

        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class, () -> metaModelProvider.getMetaModel(archetype));
        assertEquals("model for other.OTHER version 0.9.0 not found", ex.getMessage());
    }

    @Test
    public void testGetMetaModelBmmOnly() {
        MetaModelProvider metaModelProvider = new SimpleMetaModelProvider(
                null,
                BuiltinReferenceModels.getBmmRepository(),
                BuiltinReferenceModels.getAomProfiles()
        );

        Archetype archetype = new Archetype();
        archetype.setArchetypeId(new ArchetypeHRID("openEHR-EHR-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.3");

        MetaModel metaModel = metaModelProvider.getMetaModel(archetype);
        assertNull(metaModel.getModelInfoLookup());
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.3", metaModel.getBmmModel().getRmRelease());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNull(metaModel.getOdinInputObjectMapper());
        assertNull(metaModel.getOdinOutputObjectMapper());
        assertNull(metaModel.getJsonObjectMapper());

        archetype.setRmRelease("1.0.4");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertNull(metaModel.getModelInfoLookup());
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.4", metaModel.getBmmModel().getRmRelease());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNull(metaModel.getOdinInputObjectMapper());
        assertNull(metaModel.getOdinOutputObjectMapper());
        assertNull(metaModel.getJsonObjectMapper());

        archetype.setRmRelease("1.99.1");

        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class, () -> metaModelProvider.getMetaModel(archetype));
        assertEquals("model for openEHR.EHR version 1.99.1 not found", ex.getMessage());

        archetype.setArchetypeId(new ArchetypeHRID("openEHR-TEST_PKG-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.2");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertNull(metaModel.getModelInfoLookup());
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("TEST_PKG", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.2", metaModel.getBmmModel().getRmRelease());
        assertEquals("openEHR", metaModel.getAomProfile().getProfileName());
        assertNull(metaModel.getOdinInputObjectMapper());
        assertNull(metaModel.getOdinOutputObjectMapper());
        assertNull(metaModel.getJsonObjectMapper());

        archetype.setArchetypeId(new ArchetypeHRID("other-OTHER-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("0.9.0");

        ex = assertThrows(ModelNotFoundException.class, () -> metaModelProvider.getMetaModel(archetype));
        assertEquals("model for other.OTHER version 0.9.0 not found", ex.getMessage());
    }


    @Test
    public void testGetMetaModelNoAomProfiles() {
        MetaModelProvider metaModelProvider = new SimpleMetaModelProvider(
                BuiltinReferenceModels.getAvailableModelInfoLookups(),
                BuiltinReferenceModels.getBmmRepository()
        );

        Archetype archetype = new Archetype();
        archetype.setArchetypeId(new ArchetypeHRID("openEHR-EHR-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.3");

        MetaModel metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof ArchieRMInfoLookup);
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.3", metaModel.getBmmModel().getRmRelease());
        assertNull(metaModel.getAomProfile());
        assertNotNull(metaModel.getOdinInputObjectMapper());
        assertNotNull(metaModel.getOdinOutputObjectMapper());
        assertNotNull(metaModel.getJsonObjectMapper());

        archetype.setRmRelease("1.0.4");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof ArchieRMInfoLookup);
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.4", metaModel.getBmmModel().getRmRelease());
        assertNull(metaModel.getAomProfile());
        assertNotNull(metaModel.getOdinInputObjectMapper());
        assertNotNull(metaModel.getOdinOutputObjectMapper());
        assertNotNull(metaModel.getJsonObjectMapper());

        archetype.setRmRelease("1.99.1");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof ArchieRMInfoLookup);
        assertNull(metaModel.getBmmModel());
        assertNull(metaModel.getAomProfile());
        assertNotNull(metaModel.getOdinInputObjectMapper());
        assertNotNull(metaModel.getOdinOutputObjectMapper());
        assertNotNull(metaModel.getJsonObjectMapper());

        archetype.setArchetypeId(new ArchetypeHRID("openEHR-TEST_PKG-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.2");

        metaModel = metaModelProvider.getMetaModel(archetype);
        assertTrue(metaModel.getModelInfoLookup() instanceof TestRMInfoLookup);
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("TEST_PKG", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.2", metaModel.getBmmModel().getRmRelease());
        assertNull(metaModel.getAomProfile());
        assertNull(metaModel.getOdinInputObjectMapper());
        assertNull(metaModel.getOdinOutputObjectMapper());
        assertNull(metaModel.getJsonObjectMapper());

        archetype.setArchetypeId(new ArchetypeHRID("other-OTHER-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("0.9.0");

        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class, () -> metaModelProvider.getMetaModel(archetype));
        assertEquals("model for other.OTHER version 0.9.0 not found", ex.getMessage());
    }
}
