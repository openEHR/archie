package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import com.nedap.archie.rm.datavalues.DvEHRURI;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DvEhrUriInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new DvEHRURI("ehr://something/something"));
    }

    @Test
    public void invalid() {
        InvariantTestUtil.assertInvariantInvalid(new DvEHRURI("https://something/something"), "Scheme_valid", "/");
    }


    @Test
    public void invalid2() {
        RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance(), (templateId) -> null);
        List<RMObjectValidationMessage> messages = validator.validate(new DvEHRURI(""));
        assertEquals(messages.toString(), 2, messages.size());
        assertEquals("Invariant Value_valid failed on type DV_EHR_URI", messages.get(0).getMessage());
        assertEquals("/", messages.get(0).getPath());
        assertEquals("Invariant Scheme_valid failed on type DV_EHR_URI", messages.get(1).getMessage());
        assertEquals("/", messages.get(1).getPath());
    }

    @Test
    public void invalid3() {
        InvariantTestUtil.assertInvariantInvalid(new DvEHRURI("target1"), "Scheme_valid", "/");
    }
}
