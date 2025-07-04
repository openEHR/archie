package org.openehr.referencemodels;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.rminfo.MetaModels;
import org.junit.Test;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import static org.junit.Assert.assertEquals;

public class BuiltInReferenceModelsTest {

    @Test
    public void bmmRepository() throws Exception {
        BmmRepository bmmRepository = BuiltinReferenceModels.getBmmRepository();

        for(BmmValidationResult validation:bmmRepository.getInvalidModels()) {
            System.out.println("validation " + validation.getSchemaId() + " contains errors:");
            System.out.println(validation.getLogger().toString());

        }
        assertEquals(35, bmmRepository.getPersistentSchemas().size());
        assertEquals(35, bmmRepository.getModels().size());
        assertEquals(32, bmmRepository.getValidModels().size());
        assertEquals(3, bmmRepository.getInvalidModels().size());
    }

    @Test
    @Deprecated
    public void overrideModelVersion() throws Exception {
        MetaModels metaModels = BuiltinReferenceModels.getMetaModels();
        Archetype archetype = new Archetype();
        archetype.setArchetypeId(new ArchetypeHRID("openEHR-EHR-CLUSTER.test.v1.0.0"));
        archetype.setRmRelease("1.0.3");
        metaModels.selectModel(archetype);
        assertEquals("openehr", metaModels.getSelectedBmmModel().getRmPublisher());
        assertEquals("EHR", metaModels.getSelectedBmmModel().getModelName());
        assertEquals("1.0.3", metaModels.getSelectedBmmModel().getRmRelease());


        //now overide the version to 1.0.4, and assert
        metaModels.overrideModelVersion("openEHR", "EHR", "1.0.4");

        metaModels.selectModel(archetype);

        metaModels.selectModel(archetype);
        assertEquals("openehr", metaModels.getSelectedBmmModel().getRmPublisher());
        assertEquals("EHR", metaModels.getSelectedBmmModel().getModelName());
        assertEquals("1.0.4", metaModels.getSelectedBmmModel().getRmRelease());

        //select a specific RM
        metaModels.selectModel(archetype, "1.0.2");

        assertEquals("openehr", metaModels.getSelectedBmmModel().getRmPublisher());
        assertEquals("EHR", metaModels.getSelectedBmmModel().getModelName());
        assertEquals("1.0.2", metaModels.getSelectedBmmModel().getRmRelease());

        //remove Override
        metaModels.removeOverridenModelVersion("openEHR", "EHR");
        metaModels.selectModel(archetype);
        assertEquals("openehr", metaModels.getSelectedBmmModel().getRmPublisher());
        assertEquals("EHR", metaModels.getSelectedBmmModel().getModelName());
        assertEquals("1.0.3", metaModels.getSelectedBmmModel().getRmRelease());
    }
}
