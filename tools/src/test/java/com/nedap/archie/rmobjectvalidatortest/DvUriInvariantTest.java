package com.nedap.archie.rmobjectvalidatortest;

import com.nedap.archie.rm.datavalues.DvEHRURI;
import com.nedap.archie.rm.datavalues.DvURI;
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
