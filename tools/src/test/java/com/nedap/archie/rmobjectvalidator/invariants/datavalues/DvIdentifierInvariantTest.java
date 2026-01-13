package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

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
