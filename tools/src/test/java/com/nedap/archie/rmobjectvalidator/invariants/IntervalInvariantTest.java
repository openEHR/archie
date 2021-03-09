package com.nedap.archie.rmobjectvalidator.invariants;

import com.nedap.archie.base.Interval;
import org.junit.Test;

public class IntervalInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(Interval.lowerUnbounded(3, true));
        InvariantTestUtil.assertValid(Interval.lowerUnbounded(3, false));
        InvariantTestUtil.assertValid(Interval.upperUnbounded(3, true));
        InvariantTestUtil.assertValid(Interval.upperUnbounded(3, false));
        InvariantTestUtil.assertValid(new Interval<>(1, 3));
        InvariantTestUtil.assertValid(new Interval<>(3, 3));
        //open interval
        InvariantTestUtil.assertValid(Interval.unbounded());

        InvariantTestUtil.assertValid(new Interval<>(2, 3, false, true));
        InvariantTestUtil.assertValid(new Interval<>(2, 3, true, false));
    }

    @Test
    public void limitsConsistent() {
        InvariantTestUtil.assertInvariantInvalid(new Interval(4, 3), "Limits_consistent", "/");
    }

    @Test
    public void lowerIncludedInvalid() {
        Interval interval = new Interval(1, 4);
        interval.setLowerIncluded(true);
        interval.setLowerUnbounded(true);
        InvariantTestUtil.assertInvariantInvalid(interval, "Lower_included_valid", "/");
    }

    @Test
    public void upperIncludedInvalid() {
        Interval interval = new Interval(1, 4);
        interval.setUpperIncluded(true);
        interval.setUpperUnbounded(true);
        InvariantTestUtil.assertInvariantInvalid(interval, "Upper_included_valid", "/");
    }
}
