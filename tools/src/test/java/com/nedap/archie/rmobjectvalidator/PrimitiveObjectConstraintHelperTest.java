package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.primitives.*;
import com.nedap.archie.base.Interval;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PrimitiveObjectConstraintHelperTest {
    private final PrimitiveObjectConstraintHelper helper = new PrimitiveObjectConstraintHelper(new ValidationConfiguration.Builder().build());
    
    @Test
    public void booleanOnlyFalse() {
        CBoolean test = new CBoolean();
        test.addConstraint(false);
        assertTrue(helper.isValidValue(test, false));
        assertFalse(helper.isValidValue(test, true));
    }

    @Test
    public void booleanOnlyTrue() {
        CBoolean test = new CBoolean();
        test.addConstraint(true);
        assertTrue(helper.isValidValue(test, true));
        assertFalse(helper.isValidValue(test, false));
    }

    @Test
    public void booleanBoth() {
        CBoolean test = new CBoolean();
        test.addConstraint(true);
        test.addConstraint(false);
        assertTrue(helper.isValidValue(test, true));
        assertTrue(helper.isValidValue(test, false));

        CBoolean test2 = new CBoolean();
        assertTrue(helper.isValidValue(test2, true));
        assertTrue(helper.isValidValue(test2, false));
    }

    @Test
    public void dateConstraints() {
        CDate unconstrained = new CDate();
        helper.isValidValue(unconstrained, LocalDate.now());
        helper.isValidValue(unconstrained, LocalDate.of(1972, Month.JANUARY, 1));

        CDate interval = new CDate();
        interval.addConstraint(new Interval<>(LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.DECEMBER, 31)));
        assertTrue(helper.isValidValue(interval, LocalDate.of(2015, Month.JUNE, 1)));
        assertTrue(helper.isValidValue(interval, LocalDate.of(2015, Month.JANUARY, 1)));
        assertTrue(helper.isValidValue(interval, LocalDate.of(2015, Month.DECEMBER, 31)));
        assertFalse(helper.isValidValue(interval, LocalDate.of(2016, Month.JANUARY, 1)));
        assertFalse(helper.isValidValue(interval, LocalDate.of(2014, Month.DECEMBER, 31)));

        CDate twoInterVals = new CDate();
        twoInterVals.addConstraint(new Interval<>(LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.DECEMBER, 31)));
        twoInterVals.addConstraint(new Interval<>(LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.DECEMBER, 31)));
        assertTrue(helper.isValidValue(twoInterVals, LocalDate.of(2015, Month.JUNE, 1)));
        assertTrue(helper.isValidValue(twoInterVals, LocalDate.of(2013, Month.JUNE, 1)));
        assertFalse(helper.isValidValue(twoInterVals, LocalDate.of(2014, Month.JUNE, 1)));
    }

    @Test
    public void integers() {
        CInteger noConstraints = new CInteger();
        for(long i = -100L; i < 100L; i++) {
            assertTrue(helper.isValidValue(noConstraints, i));
        }

        CInteger constantConstraint = new CInteger();
        constantConstraint.addConstraint(new Interval<>(55L));
        assertTrue(helper.isValidValue(constantConstraint, 55L));
        assertFalse(helper.isValidValue(constantConstraint, 54L));
        assertFalse(helper.isValidValue(constantConstraint, 56L));

        CInteger constantConstraints = new CInteger();
        constantConstraints.addConstraint(new Interval<>(10L));
        constantConstraints.addConstraint(new Interval<>(20L));
        constantConstraints.addConstraint(new Interval<>(30L));
        assertTrue(helper.isValidValue(constantConstraints, 10L));
        assertTrue(helper.isValidValue(constantConstraints, 20L));
        assertTrue(helper.isValidValue(constantConstraints, 30L));
        assertFalse(helper.isValidValue(constantConstraints, 19L));

        CInteger rangeConstraint = new CInteger();
        rangeConstraint.addConstraint(new Interval<>(0L, 100L));
        assertTrue(helper.isValidValue(rangeConstraint, 0L));
        assertTrue(helper.isValidValue(rangeConstraint, 100L));
        assertFalse(helper.isValidValue(rangeConstraint, 101L));
        assertFalse(helper.isValidValue(rangeConstraint, -1L));

        CInteger rangeLowerNotIncluded = new CInteger();
        rangeLowerNotIncluded.addConstraint(new Interval<>(0L, 100L, false, true));
        assertFalse(helper.isValidValue(rangeLowerNotIncluded, 0L));
        assertTrue(helper.isValidValue(rangeLowerNotIncluded, 100L));
        assertFalse(helper.isValidValue(rangeLowerNotIncluded, 101L));
        assertFalse(helper.isValidValue(rangeLowerNotIncluded, -1L));


        CInteger rangeUpperNotIncluded = new CInteger();
        rangeUpperNotIncluded.addConstraint(new Interval<>(0L, 100L, true, false));
        assertTrue(helper.isValidValue(rangeUpperNotIncluded, 0L));
        assertFalse(helper.isValidValue(rangeUpperNotIncluded, 100L));
        assertFalse(helper.isValidValue(rangeUpperNotIncluded, 101L));
        assertFalse(helper.isValidValue(rangeUpperNotIncluded, -1L));

        CInteger rangeUpperUnbounded = new CInteger();
        rangeUpperUnbounded.addConstraint(Interval.upperUnbounded(10L, true));
        assertTrue(helper.isValidValue(rangeUpperUnbounded, 1000000L));
        assertTrue(helper.isValidValue(rangeUpperUnbounded, 10L));
        assertFalse(helper.isValidValue(rangeUpperUnbounded, 9L));

        CInteger rangeLowerUnbounded = new CInteger();
        rangeLowerUnbounded.addConstraint(Interval.lowerUnbounded(10L, true));
        assertFalse(helper.isValidValue(rangeLowerUnbounded, 1000000L));
        assertTrue(helper.isValidValue(rangeLowerUnbounded, 10L));
        assertTrue(helper.isValidValue(rangeLowerUnbounded, -100000000L));

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
            assertTrue(helper.isValidValue(noConstraints, i));
        }

        CReal constantConstraint = new CReal();
        /* constant reals are very strange intervals, but let's test them nonetheless. Perhaps they should work with a delta?*/
        constantConstraint.addConstraint(new Interval<>(55d));
        assertTrue(helper.isValidValue(constantConstraint, 55d));
        assertFalse(helper.isValidValue(constantConstraint, 55.01d));
        assertFalse(helper.isValidValue(constantConstraint, 54.99d));

        CReal constantConstraints = new CReal();
        constantConstraints.addConstraint(new Interval<>(10d));
        constantConstraints.addConstraint(new Interval<>(20d));
        constantConstraints.addConstraint(new Interval<>(30d));
        assertTrue(helper.isValidValue(constantConstraints, 10d));
        assertTrue(helper.isValidValue(constantConstraints, 20d));
        assertTrue(helper.isValidValue(constantConstraints, 30d));
        assertFalse(helper.isValidValue(constantConstraints, 19.99d));


        CReal rangeConstraint = new CReal();
        rangeConstraint.addConstraint(new Interval<>(0d, 100d));
        assertTrue(helper.isValidValue(rangeConstraint, 0d));
        assertTrue(helper.isValidValue(rangeConstraint, 100d));
        assertFalse(helper.isValidValue(rangeConstraint, 100.1d));
        assertFalse(helper.isValidValue(rangeConstraint, -1d));

        CReal rangeLowerNotIncluded = new CReal();
        rangeLowerNotIncluded.addConstraint(new Interval<>(0d, 100d, false, true));
        assertFalse(helper.isValidValue(rangeLowerNotIncluded, 0d));
        assertTrue(helper.isValidValue(rangeLowerNotIncluded, 100d));
        assertFalse(helper.isValidValue(rangeLowerNotIncluded, 100.1d));
        assertFalse(helper.isValidValue(rangeLowerNotIncluded, -1d));


        CReal rangeUpperNotIncluded = new CReal();
        rangeUpperNotIncluded.addConstraint(new Interval<>(0d, 100d, true, false));
        assertTrue(helper.isValidValue(rangeUpperNotIncluded, 0d));
        assertFalse(helper.isValidValue(rangeUpperNotIncluded, 100d));
        assertFalse(helper.isValidValue(rangeUpperNotIncluded, 101d));
        assertFalse(helper.isValidValue(rangeUpperNotIncluded, -1d));

        CReal rangeUpperUnbounded = new CReal();
        rangeUpperUnbounded.addConstraint(Interval.upperUnbounded(10d, true));
        assertTrue(helper.isValidValue(rangeUpperUnbounded, 1000000d));
        assertTrue(helper.isValidValue(rangeUpperUnbounded, 10d));
        assertFalse(helper.isValidValue(rangeUpperUnbounded, 9d));

        CReal rangeLowerUnbounded = new CReal();
        rangeLowerUnbounded.addConstraint(Interval.lowerUnbounded(10d, true));
        assertFalse(helper.isValidValue(rangeLowerUnbounded, 1000000d));
        assertTrue(helper.isValidValue(rangeLowerUnbounded, 10d));
        assertTrue(helper.isValidValue(rangeLowerUnbounded, -100000000d));
    }


    @Test
    public void stringConstants() {
        CString noConstraint = new CString();
        assertTrue(helper.isValidValue(noConstraint, "a"));
        assertTrue(helper.isValidValue(noConstraint, "b"));
        assertTrue(helper.isValidValue(noConstraint, "The complete works of William Shakespeare"));

        CString oneConstraint = new CString();
        oneConstraint.addConstraint("very specific secret string");
        assertTrue(helper.isValidValue(oneConstraint, "very specific secret string"));
        assertFalse(helper.isValidValue(oneConstraint, "not specific at all not secret string"));


        CString moreConstraints = new CString();
        moreConstraints.addConstraint("very specific secret string");
        moreConstraints.addConstraint("another string");
        assertTrue(helper.isValidValue(moreConstraints, "very specific secret string"));
        assertTrue(helper.isValidValue(moreConstraints, "another string"));
        assertFalse(helper.isValidValue(moreConstraints, "not specific at all not secret string"));


    }

    @Test
    public void stringRegexps() {

        CString oneConstraint = new CString();
        oneConstraint.addConstraint("/a+b*/");
        assertTrue(helper.isValidValue(oneConstraint, "a"));
        assertTrue(helper.isValidValue(oneConstraint, "aa"));
        assertTrue(helper.isValidValue(oneConstraint, "aaaaaaaabbbbb"));
        assertFalse(helper.isValidValue(oneConstraint, "aaaaaaaabbbbbc"));

        CString moreConstraints = new CString();
        moreConstraints.addConstraint("/a+b*/");
        moreConstraints.addConstraint("^dbca+^");
        assertTrue(helper.isValidValue(moreConstraints, "ab"));
        assertTrue(helper.isValidValue(moreConstraints, "aabb"));
        assertTrue(helper.isValidValue(moreConstraints, "dbcaa"));
        assertFalse(helper.isValidValue(moreConstraints, "Something else"));

        CString mixedConstraints = new CString();
        mixedConstraints.addConstraint("/a+b*/");
        mixedConstraints.addConstraint("^dbca+^");
        mixedConstraints.addConstraint("Something else");
        assertTrue(helper.isValidValue(mixedConstraints, "ab"));
        assertTrue(helper.isValidValue(mixedConstraints, "aabb"));
        assertTrue(helper.isValidValue(mixedConstraints, "dbcaa"));
        assertTrue(helper.isValidValue(mixedConstraints, "Something else"));
        assertFalse(helper.isValidValue(mixedConstraints, "what more?"));
    }

    @Test
    public void timeConstraints() {
        CTime unconstrained = new CTime();
        helper.isValidValue(unconstrained, LocalTime.now());
        helper.isValidValue(unconstrained, LocalTime.of(12, 0));

        CTime interval = new CTime();
        interval.addConstraint(new Interval<>(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        assertTrue(helper.isValidValue(interval, LocalTime.of(11, 0)));
        assertTrue(helper.isValidValue(interval, LocalTime.of(12, 0)));
        assertFalse(helper.isValidValue(interval, LocalTime.of(10, 59)));
        assertFalse(helper.isValidValue(interval, LocalTime.of(12, 1)));

        CTime twoInterVals = new CTime();
        twoInterVals.addConstraint(new Interval<>(LocalTime.of(11, 0), LocalTime.of(12, 0)));
        twoInterVals.addConstraint(new Interval<>(LocalTime.of(10, 0), LocalTime.of(11, 0)));
        assertTrue(helper.isValidValue(twoInterVals, LocalTime.of(11, 0)));
        assertTrue(helper.isValidValue(twoInterVals, LocalTime.of(12, 0)));
        assertTrue(helper.isValidValue(twoInterVals, LocalTime.of(10, 59)));
        assertFalse(helper.isValidValue(twoInterVals, LocalTime.of(12, 1)));
    }
}
