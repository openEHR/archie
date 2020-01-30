package com.nedap.archie.aom;

import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.base.Interval;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by pieter.bos on 01/11/15.
 */
public class NumberConstraintsTest {

    @Test
    public void integers() {
        CInteger noConstraints = new CInteger();
        for(long i = -100l;i < 100l;i++) {
            assertTrue(noConstraints.isValidValue(i));
        }

        CInteger constantConstraint = new CInteger();
        constantConstraint.addConstraint(new Interval<>(55l));
        assertTrue(constantConstraint.isValidValue(55l));
        assertFalse(constantConstraint.isValidValue(54l));
        assertFalse(constantConstraint.isValidValue(56l));

        CInteger constantConstraints = new CInteger();
        constantConstraints.addConstraint(new Interval(10L));
        constantConstraints.addConstraint(new Interval(20L));
        constantConstraints.addConstraint(new Interval(30L));
        assertTrue(constantConstraints.isValidValue(10l));
        assertTrue(constantConstraints.isValidValue(20l));
        assertTrue(constantConstraints.isValidValue(30l));
        assertFalse(constantConstraints.isValidValue(19l));

        CInteger rangeConstraint = new CInteger();
        rangeConstraint.addConstraint(new Interval<>(0l, 100l));
        assertTrue(rangeConstraint.isValidValue(0l));
        assertTrue(rangeConstraint.isValidValue(100l));
        assertFalse(rangeConstraint.isValidValue(101l));
        assertFalse(rangeConstraint.isValidValue(-1l));

        CInteger rangeLowerNotIncluded = new CInteger();
        rangeLowerNotIncluded.addConstraint(new Interval(0l, 100l, false, true));
        assertFalse(rangeLowerNotIncluded.isValidValue(0l));
        assertTrue(rangeLowerNotIncluded.isValidValue(100l));
        assertFalse(rangeLowerNotIncluded.isValidValue(101l));
        assertFalse(rangeLowerNotIncluded.isValidValue(-1l));


        CInteger rangeUpperNotIncluded = new CInteger();
        rangeUpperNotIncluded.addConstraint(new Interval(0l, 100l, true, false));
        assertTrue(rangeUpperNotIncluded.isValidValue(0l));
        assertFalse(rangeUpperNotIncluded.isValidValue(100l));
        assertFalse(rangeUpperNotIncluded.isValidValue(101l));
        assertFalse(rangeUpperNotIncluded.isValidValue(-1l));

        CInteger rangeUpperUnbounded = new CInteger();
        rangeUpperUnbounded.addConstraint(Interval.upperUnbounded(10l, true));
        assertTrue(rangeUpperUnbounded.isValidValue(1000000l));
        assertTrue(rangeUpperUnbounded.isValidValue(10l));
        assertFalse(rangeUpperUnbounded.isValidValue(9l));

        CInteger rangeLowerUnbounded = new CInteger();
        rangeLowerUnbounded.addConstraint(Interval.lowerUnbounded(10l, true));
        assertFalse(rangeLowerUnbounded.isValidValue(1000000l));
        assertTrue(rangeLowerUnbounded.isValidValue(10l));
        assertTrue(rangeLowerUnbounded.isValidValue(-100000000l));

    }


    /*
    real_attr1 matches {100.0}
        real_attr2 matches {10.0, 20.0, 30.0}
        real_attr3 matches {|0.0..100.0|}
        real_attr4 matches {|>0.0..100.0|}
        real_attr5 matches {|0.0..<100.0|}
        real_attr6 matches {|>0.0..<100.0|}
        real_attr7 matches {|>=10.0|}
        real_attr8 matches {|<=10.0|}
        real_attr9 matches {|>=10.0|}
        real_attr10 matches {|<=10.0|}
        real_attr11 matches {|-10.0..-5.0|}
        real_attr12 matches {10.0}
        real_attr13
     */
    @Test
    public void reals() {

        CReal noConstraints = new CReal();
        for(double i = -100d;i < 100d;i++) {
            assertTrue(noConstraints.isValidValue(i));
        }

        CReal constantConstraint = new CReal();
        /* constant reals are very strange intervals, but let's test them nonetheless. Perhaps they should work with a delta?*/
        constantConstraint.addConstraint(new Interval<>(55d));
        assertTrue(constantConstraint.isValidValue(55d));
        assertFalse(constantConstraint.isValidValue(55.01d));
        assertFalse(constantConstraint.isValidValue(54.99d));

        CReal constantConstraints = new CReal();
        constantConstraints.addConstraint(new Interval(10d));
        constantConstraints.addConstraint(new Interval(20d));
        constantConstraints.addConstraint(new Interval(30d));
        assertTrue(constantConstraints.isValidValue(10d));
        assertTrue(constantConstraints.isValidValue(20d));
        assertTrue(constantConstraints.isValidValue(30d));
        assertFalse(constantConstraints.isValidValue(19.99d));


        CReal rangeConstraint = new CReal();
        rangeConstraint.addConstraint(new Interval<>(0d, 100d));
        assertTrue(rangeConstraint.isValidValue(0d));
        assertTrue(rangeConstraint.isValidValue(100d));
        assertFalse(rangeConstraint.isValidValue(100.1d));
        assertFalse(rangeConstraint.isValidValue(-1d));

        CReal rangeLowerNotIncluded = new CReal();
        rangeLowerNotIncluded.addConstraint(new Interval(0d, 100d, false, true));
        assertFalse(rangeLowerNotIncluded.isValidValue(0d));
        assertTrue(rangeLowerNotIncluded.isValidValue(100d));
        assertFalse(rangeLowerNotIncluded.isValidValue(100.1d));
        assertFalse(rangeLowerNotIncluded.isValidValue(-1d));


        CReal rangeUpperNotIncluded = new CReal();
        rangeUpperNotIncluded.addConstraint(new Interval(0d, 100d, true, false));
        assertTrue(rangeUpperNotIncluded.isValidValue(0d));
        assertFalse(rangeUpperNotIncluded.isValidValue(100d));
        assertFalse(rangeUpperNotIncluded.isValidValue(101d));
        assertFalse(rangeUpperNotIncluded.isValidValue(-1d));

        CReal rangeUpperUnbounded = new CReal();
        rangeUpperUnbounded.addConstraint(Interval.upperUnbounded(10d, true));
        assertTrue(rangeUpperUnbounded.isValidValue(1000000d));
        assertTrue(rangeUpperUnbounded.isValidValue(10d));
        assertFalse(rangeUpperUnbounded.isValidValue(9d));

        CReal rangeLowerUnbounded = new CReal();
        rangeLowerUnbounded.addConstraint(Interval.lowerUnbounded(10d, true));
        assertFalse(rangeLowerUnbounded.isValidValue(1000000d));
        assertTrue(rangeLowerUnbounded.isValidValue(10d));
        assertTrue(rangeLowerUnbounded.isValidValue(-100000000d));
    }
}
