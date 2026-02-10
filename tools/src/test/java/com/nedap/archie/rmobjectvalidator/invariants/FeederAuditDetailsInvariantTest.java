package com.nedap.archie.rmobjectvalidator.invariants;

import com.nedap.archie.rm.archetyped.FeederAuditDetails;
import org.junit.jupiter.api.Test;

public class FeederAuditDetailsInvariantTest {
    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new FeederAuditDetails("some_system"));
    }

    @Test
    public void invalidSystemId() {
        InvariantTestUtil.assertInvariantInvalid(new FeederAuditDetails(""), "System_id_valid", "/");
    }

}
