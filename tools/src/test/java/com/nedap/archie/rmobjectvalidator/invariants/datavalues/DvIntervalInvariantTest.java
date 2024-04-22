package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datavalues.quantity.DvCount;
import org.openehr.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class DvIntervalInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new DvInterval(new DvCount(1l), new DvCount(5l)));
        InvariantTestUtil.assertValid(new DvInterval(new DvCount(1l), new DvCount(1l)));
    }

    @Test
    public void inconsistentLimits() {
        InvariantTestUtil.assertInvariantInvalid(new DvInterval(new DvCount(5l), new DvCount(1l)), "Limits_consistent", "INTERVAL", "/interval");
    }

    @Test
    public void intervalInvariantsChecked() {
        DvInterval interval = new DvInterval(new DvCount(1l), new DvCount(4l));
        interval.setLowerIncluded(true);
        interval.setLowerUnbounded(true);
        InvariantTestUtil.assertInvariantInvalid(interval, "Lower_included_valid", "INTERVAL", "/interval");
    }
}
