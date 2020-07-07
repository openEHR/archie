package org.openehr.referencemodels;

import com.google.common.collect.Sets;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.rminfo.MetaModels;
import org.junit.Test;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuiltInReferenceModelsTest {

    @Test
    public void bmmRepository() throws Exception {

        BmmRepository bmmRepository = BuiltinReferenceModels.getBmmRepository();

        for(BmmValidationResult validation:bmmRepository.getInvalidModels()) {
            System.out.println("validation " + validation.getSchemaId() + " contains errors:");
            System.out.println(validation.getLogger().toString());

        }
        assertEquals(27, bmmRepository.getPersistentSchemas().size());
        assertEquals(27, bmmRepository.getModels().size());
        assertEquals(27, bmmRepository.getValidModels().size());
        assertEquals(1, bmmRepository.getInvalidModels().size());
    }

    @Test
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
