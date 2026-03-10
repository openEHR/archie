package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import com.nedap.archie.rm.datavalues.quantity.DvCount;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.jupiter.api.Test;

public class DvIntervalInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new DvInterval<>(new DvCount(1L), new DvCount(5L)));
        InvariantTestUtil.assertValid(new DvInterval<>(new DvCount(1L), new DvCount(1L)));
    }

    @Test
    public void inconsistentLimits() {
        InvariantTestUtil.assertInvariantInvalid(new DvInterval<>(new DvCount(5L), new DvCount(1L)), "Limits_consistent", "INTERVAL", "/interval");
    }

    @Test
    public void intervalInvariantsChecked() {
        DvInterval<DvCount> interval = new DvInterval<>(new DvCount(1L), new DvCount(4L));
        interval.setLowerIncluded(true);
        interval.setLowerUnbounded(true);
        InvariantTestUtil.assertInvariantInvalid(interval, "Lower_included_valid", "INTERVAL", "/interval");
    }
}
