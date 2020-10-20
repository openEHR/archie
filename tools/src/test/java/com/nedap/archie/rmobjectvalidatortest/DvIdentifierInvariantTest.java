package com.nedap.archie.rmobjectvalidatortest;

import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DvIdentifierInvariantTest {

    @Test
    public void valid() {
        DvIdentifier identifier = new DvIdentifier();
        identifier.setId("something");
        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        assertTrue(rmObjectValidator.validate(identifier).isEmpty());
    }

    @Test
    public void idInvalid() {
        DvIdentifier identifier = new DvIdentifier();
        identifier.setId("");
        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        assertFalse(rmObjectValidator.validate(identifier).isEmpty());
    }
}
