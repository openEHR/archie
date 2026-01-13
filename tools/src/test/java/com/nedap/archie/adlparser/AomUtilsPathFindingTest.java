package com.nedap.archie.adlparser;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AomUtilsPathFindingTest {

    @Test
    public void isPathInArchetypeOrRm() throws Exception{
        Archetype archetype = TestUtil.parseFailOnErrors("/basic.adl");
        MetaModel metaModel = BuiltinReferenceModels.getMetaModelProvider().getMetaModel(archetype);
        //AOM path
        assertTrue(AOMUtils.isPathInArchetypeOrRm(metaModel, "/context[id11]", archetype));
        //mixed aom + RM path
        assertTrue(AOMUtils.isPathInArchetypeOrRm(metaModel, "/context[id11]/health_care_facility/name", archetype));
        //path not in AOM, only in RM
        assertTrue(AOMUtils.isPathInArchetypeOrRm(metaModel, "/composer/external_ref", archetype));
        //non-existing path
        assertFalse(AOMUtils.isPathInArchetypeOrRm(metaModel, "/doesnot_exists", archetype));
    }
}
