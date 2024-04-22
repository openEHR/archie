package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datavalues.DvEHRURI;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        RMObjectValidator validator = new RMObjectValidator(OpenEhrRmInfoLookup.getInstance(), (templateId) -> null);
        List<RMObjectValidationMessage> messages = validator.validate(new DvEHRURI(""));
        assertEquals(messages.toString(), 2, messages.size());

        Set<String> expectedMessages = new HashSet<>();
        expectedMessages.add("Invariant Scheme_valid failed on type DV_EHR_URI");
        expectedMessages.add("Invariant Value_valid failed on type DV_EHR_URI");
        assertEquals(expectedMessages, messages.stream().map(RMObjectValidationMessage::getMessage).collect(Collectors.toSet()));

        assertEquals("/", messages.get(0).getPath());
        assertEquals("/", messages.get(1).getPath());
    }

    @Test
    public void invalid3() {
        InvariantTestUtil.assertInvariantInvalid(new DvEHRURI("target1"), "Scheme_valid", "/");
    }
}
