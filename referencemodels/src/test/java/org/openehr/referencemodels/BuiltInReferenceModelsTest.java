package org.openehr.referencemodels;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.rmaccess.ReferenceModelAccess;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuiltInReferenceModelsTest {
    @Test
    public void getValidModels() throws Exception {

        ReferenceModelAccess access = BuiltinReferenceModels.getBMMReferenceModels();
        Map<String, BmmModel> models = access.getValidModels();
        assertTrue(access.getValidator().hasErrors()); //hl7 fihr is missing the Signature type, so this results in an error
        assertEquals(1, access.getValidator().getMessageLogger().getErrorCodes().size());

        //if we don't set the top level schema, it has warnings, apparently. Don't know why, often you would want all of these
        assertTrue(access.getValidator().hasWarnings());
        assertEquals(8, models.size());
        assertEquals(Sets.newHashSet("openehr_adltest_1.0.2",
                "cdisc_core_0.5.0",
                "cen_en13606_0.95",
                "openehr_rm_1.0.2",
                "cimi_rm_clinical_0.0.3",
                "openehr_rm_1.0.4",
                "openehr_rm_1.0.3",
                "openehr_proc_task_planning_1.0.0"), models.keySet());
    }
}
