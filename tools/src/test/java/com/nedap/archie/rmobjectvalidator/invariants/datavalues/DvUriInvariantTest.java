package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datavalues.DvURI;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

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
