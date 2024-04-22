package com.nedap.archie.rmobjectvalidator.invariants;

import org.openehr.rm.archetyped.FeederAuditDetails;
import org.junit.Test;

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
