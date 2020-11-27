package com.nedap.archie.rmobjectvalidatortest.invariants.datavalues;

import com.nedap.archie.rm.datavalues.quantity.DvCount;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rmobjectvalidatortest.InvariantTestUtil;
import org.junit.Test;

public class DvIntervalInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new DvInterval(new DvCount(1l), new DvCount(5l)));
        InvariantTestUtil.assertValid(new DvInterval(new DvCount(1l), new DvCount(1l)));
    }

    @Test
    public void inconsistentLimits() {
        InvariantTestUtil.assertInvariantInvalid(new DvInterval(new DvCount(5l), new DvCount(1l)), "Limits_consistent", "/");
    }
}
