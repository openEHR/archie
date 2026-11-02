package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import com.nedap.archie.rm.datavalues.DvURI;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.jupiter.api.Test;

public class DvUriInvariantTest {
    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new DvURI("ehr://something/something"));
    }

    @Test
    public void valueInvalid() {
        InvariantTestUtil.assertInvariantInvalid(new DvURI(""), "Value_valid", "/");
    }
}
