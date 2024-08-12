package org.s2.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.rminfo.MetaModels;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidationResult;
import org.s2.rminfo.S2RmMetaModelsInitialiser;

import static org.junit.Assert.assertEquals;

public class S2RmMetaModelsInitialiserTest {

    private static S2RmMetaModelsInitialiser mmInitialiser;

    @BeforeClass
    public static void setup()
    {
        mmInitialiser = new S2RmMetaModelsInitialiser();
    }

    @Test
    public void bmmRepository() throws Exception {
        BmmRepository bmmRepository = mmInitialiser.getBmmRepository();

        for(BmmValidationResult validation:bmmRepository.getInvalidModels()) {
            System.out.println("validation " + validation.getSchemaId() + " contains errors:");
            System.out.println(validation.getLogger().toString());

        }
        assertEquals(12, bmmRepository.getPersistentSchemas().size());
        assertEquals(12, bmmRepository.getModels().size());
        assertEquals(12, bmmRepository.getValidModels().size());
        assertEquals(0, bmmRepository.getInvalidModels().size());
    }

    @Test
    public void overrideModelVersion() throws Exception {
        MetaModels metaModels = mmInitialiser.getMetaModels();
        Archetype archetype = new Archetype();
        archetype.setArchetypeId(new ArchetypeHRID("s2-EHR-Node.test.v1.0.0"));
        archetype.setRmRelease("0.8.0");
        metaModels.selectModel(archetype);
        assertEquals("s2", metaModels.getSelectedBmmModel().getRmPublisher());
        assertEquals("EHR", metaModels.getSelectedBmmModel().getModelName());
        assertEquals("0.8.0", metaModels.getSelectedBmmModel().getRmRelease());


        //now override the version to 1.0.4, and assert
        metaModels.overrideModelVersion("s2", "EHR", "0.8.5");

        metaModels.selectModel(archetype);

        metaModels.selectModel(archetype);
        assertEquals("s2", metaModels.getSelectedBmmModel().getRmPublisher());
        assertEquals("EHR", metaModels.getSelectedBmmModel().getModelName());
        assertEquals("0.8.5", metaModels.getSelectedBmmModel().getRmRelease());

        //remove Override
        metaModels.removeOverridenModelVersion("s2", "EHR");
        metaModels.selectModel(archetype);
        assertEquals("s2", metaModels.getSelectedBmmModel().getRmPublisher());
        assertEquals("EHR", metaModels.getSelectedBmmModel().getModelName());
        assertEquals("0.8.0", metaModels.getSelectedBmmModel().getRmRelease());
    }
}
