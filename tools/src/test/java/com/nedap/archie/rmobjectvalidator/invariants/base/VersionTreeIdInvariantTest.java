package com.nedap.archie.rmobjectvalidator.invariants.base;

import com.nedap.archie.rm.support.identification.VersionTreeId;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.jupiter.api.Test;

public class VersionTreeIdInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new VersionTreeId("1"));
        InvariantTestUtil.assertValid(new VersionTreeId("1.2.3"));
        InvariantTestUtil.assertValid(new VersionTreeId("1.2.3"));
    }

    @Test
    public void invalid() {
        InvariantTestUtil.assertInvariantInvalid(new VersionTreeId(""), "Value_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new VersionTreeId("a"), "Value_format_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new VersionTreeId("1.2"), "Value_format_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new VersionTreeId("1.2.a"), "Value_format_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new VersionTreeId("1/2/2"), "Value_format_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new VersionTreeId("-1"), "Value_format_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new VersionTreeId("1.-1.1"), "Value_format_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new VersionTreeId("1.1.-1"), "Value_format_valid", "/");

    }
}
