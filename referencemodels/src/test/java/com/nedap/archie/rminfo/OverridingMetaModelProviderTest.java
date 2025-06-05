package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import static org.junit.Assert.assertEquals;

public class OverridingMetaModelProviderTest {
    @Test
    public void overrideModelVersion() {
        MetaModelProvider metaModelProvider = BuiltinReferenceModels.getMetaModelProvider();
        OverridingMetaModelProvider overridingMetaModelProvider = new OverridingMetaModelProvider(metaModelProvider);

        Archetype archetype = new Archetype();
        archetype.setArchetypeId(new ArchetypeHRID("openEHR-EHR-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.3");
        MetaModel metaModel = overridingMetaModelProvider.getMetaModel(archetype);
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.3", metaModel.getBmmModel().getRmRelease());


        //now overide the version to 1.0.4, and assert
        overridingMetaModelProvider.overrideModelVersion("openEHR", "EHR", "1.0.4");

        metaModel = overridingMetaModelProvider.getMetaModel(archetype);
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.4", metaModel.getBmmModel().getRmRelease());

        //select a specific RM
        metaModel = overridingMetaModelProvider.getMetaModel(archetype, "1.0.2");

        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.2", metaModel.getBmmModel().getRmRelease());

        //remove Override
        overridingMetaModelProvider.removeOverridenModelVersion("openEHR", "EHR");
        metaModel = overridingMetaModelProvider.getMetaModel(archetype);
        assertEquals("openehr", metaModel.getBmmModel().getRmPublisher());
        assertEquals("EHR", metaModel.getBmmModel().getModelName());
        assertEquals("1.0.3", metaModel.getBmmModel().getRmRelease());
    }
}
