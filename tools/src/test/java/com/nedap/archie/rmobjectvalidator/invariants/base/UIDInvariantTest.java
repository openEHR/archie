package com.nedap.archie.rmobjectvalidator.invariants.base;

import org.openehr.rm.support.identification.UUID;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class UIDInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new UUID(java.util.UUID.randomUUID().toString()));
    }

    @Test
    public void invalid() {
        InvariantTestUtil.assertInvariantInvalid(new UUID(""), "Value_valid", "/");
    }
}
