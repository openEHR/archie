package com.nedap.archie.rmobjectvalidatortest.invariants.datavalues;

import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.rmobjectvalidatortest.InvariantTestUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DvIdentifierInvariantTest {

    @Test
    public void valid() {
        DvIdentifier identifier = new DvIdentifier();
        identifier.setId("something");
        InvariantTestUtil.assertValid(identifier);
    }

    @Test
    public void idInvalid() {
        DvIdentifier identifier = new DvIdentifier();
        identifier.setId("");
        InvariantTestUtil.assertInvariantInvalid(identifier, "Id_valid", "/");
    }
}
