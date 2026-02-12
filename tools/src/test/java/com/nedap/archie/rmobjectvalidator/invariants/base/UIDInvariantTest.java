package com.nedap.archie.rmobjectvalidator.invariants.base;

import com.nedap.archie.rm.support.identification.UUID;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.jupiter.api.Test;

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
