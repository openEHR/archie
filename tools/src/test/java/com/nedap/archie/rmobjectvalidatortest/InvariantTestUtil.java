package com.nedap.archie.rmobjectvalidatortest;

import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InvariantTestUtil {

    public static void assertValid(Object object) {
        RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = validator.validate(object);
        assertTrue("object should be valid, was not: " + messages, messages.isEmpty());
    }

    public static void assertInvariantInvalid(Object object, String invariantName, String path) {
        RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = validator.validate(object);
        assertEquals(messages.toString(), 1, messages.size());
        assertEquals("Invariant " + invariantName + " failed on type " + ArchieRMInfoLookup.getInstance().getTypeInfo(object.getClass()).getRmName(), messages.get(0).getMessage());
        assertEquals(path, messages.get(0).getPath());
    }
}
