package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datavalues.DvIdentifier;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
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
