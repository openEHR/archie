package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import com.nedap.archie.rm.datavalues.DvEHRURI;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class DvEhrUriInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new DvEHRURI("ehr://something/something"));
    }

    @Test
    public void invalid() {
        InvariantTestUtil.assertInvariantInvalid(new DvEHRURI("https://something/something"), "Scheme_valid", "/");
    }
}
