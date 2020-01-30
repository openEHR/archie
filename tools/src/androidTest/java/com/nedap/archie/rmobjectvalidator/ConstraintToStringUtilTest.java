package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.base.Interval;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ConstraintToStringUtilTest {

    @Test
    public void testConstraintElementToStringIntervalUnbounded() {
        Interval<Long> interval = new Interval<>(null, null);
        interval.setLowerUnbounded(true);
        interval.setUpperUnbounded(true);

        String actual = ConstraintToStringUtil.constraintElementToString(interval);
        assertEquals("anything", actual);
    }

    @Test
    public void testConstraintElementToStringIntervalLong() {
        assertEquals("equal to 55", ConstraintToStringUtil.constraintElementToString(new Interval<>(55L)));
        assertEquals("at least 0 and at most 100", ConstraintToStringUtil.constraintElementToString(new Interval<>(0L, 100L)));
        assertEquals("greater than 0 and at most 100", ConstraintToStringUtil.constraintElementToString(new Interval<>(0L, 100L, false, true)));
        assertEquals("at least 0 and less than 100", ConstraintToStringUtil.constraintElementToString(new Interval<>(0L, 100L, true, false)));
        assertEquals("greater than 0 and less than 100", ConstraintToStringUtil.constraintElementToString(new Interval<>(0L, 100L, false, false)));
        assertEquals("greater than 10", ConstraintToStringUtil.constraintElementToString(Interval.upperUnbounded(10L, false)));
        assertEquals("less than 10", ConstraintToStringUtil.constraintElementToString(Interval.lowerUnbounded(10L, false)));
        assertEquals("at least 10", ConstraintToStringUtil.constraintElementToString(Interval.upperUnbounded(10L, true)));
        assertEquals("at most 10", ConstraintToStringUtil.constraintElementToString(Interval.lowerUnbounded(10L, true)));
        assertEquals("at least -10 and at most -5", ConstraintToStringUtil.constraintElementToString(new Interval<>(-10L, -5L)));
    }

    @Test
    public void testConstraintElementToStringIntervalDouble() {
        assertEquals("equal to 55", ConstraintToStringUtil.constraintElementToString(new Interval<>(55d)));
        assertEquals("at least 0 and at most 100", ConstraintToStringUtil.constraintElementToString(new Interval<>(0.0, 100.0)));
        assertEquals("greater than 2.3 and less than 7.424", ConstraintToStringUtil.constraintElementToString(new Interval<>(2.3, 7.4242, false, false)));
        assertEquals("at least 2.718 and at most 3.142", ConstraintToStringUtil.constraintElementToString(new Interval<>(Math.E, Math.PI)));
    }

    @Test
    public void testConstraintElementToStringString() {
        assertEquals("\"eggs\"", ConstraintToStringUtil.constraintElementToString("eggs"));
        assertEquals("\"quotes ' and \\\" test\"", ConstraintToStringUtil.constraintElementToString("quotes ' and \" test"));

    }

    @Test
    public void testConstraintListToStringIntervalLong() {
        List<Interval<Long>> longList = new ArrayList<>();
        assertEquals("anything", ConstraintToStringUtil.constraintListToString(longList));

        longList.add(new Interval<>(10L));
        assertEquals(" -\tequal to 10", ConstraintToStringUtil.constraintListToString(longList));

        longList.add(new Interval<>(20L));
        assertEquals(" -\tequal to 10\n" +
                " -\tequal to 20", ConstraintToStringUtil.constraintListToString(longList));

        longList.add(new Interval<>(30L));
        assertEquals(" -\tequal to 10\n" +
                " -\tequal to 20\n" +
                " -\tequal to 30", ConstraintToStringUtil.constraintListToString(longList));

        longList.clear();
        longList.add(Interval.lowerUnbounded(-10L, false));
        longList.add(new Interval<>(0L, 10L, false, true));
        longList.add(Interval.upperUnbounded(20L, true));

        String expected = " -\tless than -10\n" +
                " -\tgreater than 0 and at most 10\n" +
                " -\tat least 20";
        assertEquals(expected, ConstraintToStringUtil.constraintListToString(longList));
    }

    @Test
    public void testConstraintListToStringIntervalDouble() {
        List<Interval<Double>> doubleList = new ArrayList<>();
        assertEquals("anything", ConstraintToStringUtil.constraintListToString(doubleList));

        doubleList.add(new Interval<>(1.42d));
        assertEquals(" -\tequal to 1.42", ConstraintToStringUtil.constraintListToString(doubleList));

        doubleList.add(new Interval<>(Math.E));
        assertEquals(" -\tequal to 1.42\n" +
                " -\tequal to 2.718", ConstraintToStringUtil.constraintListToString(doubleList));

        doubleList.add(new Interval<>(Math.PI));
        assertEquals(" -\tequal to 1.42\n" +
                " -\tequal to 2.718\n" +
                " -\tequal to 3.142", ConstraintToStringUtil.constraintListToString(doubleList));

        doubleList.clear();
        doubleList.add(Interval.lowerUnbounded(-10.5, false));
        doubleList.add(new Interval<>(0.0, 10.0, false, true));
        doubleList.add(Interval.upperUnbounded(20.42, true));

        String expected = " -\tless than -10.5\n" +
                " -\tgreater than 0 and at most 10\n" +
                " -\tat least 20.42";
        assertEquals(expected, ConstraintToStringUtil.constraintListToString(doubleList));
    }

    @Test
    public void testConstraintListToStringString() {
        List<String> stringList = new ArrayList<>();
        assertEquals("anything", ConstraintToStringUtil.constraintListToString(stringList));

        stringList.add("eggs");
        assertEquals(" -\t\"eggs\"", ConstraintToStringUtil.constraintListToString(stringList));

        stringList.add("bacon");
        assertEquals(" -\t\"eggs\"\n" +
                " -\t\"bacon\"", ConstraintToStringUtil.constraintListToString(stringList));

        stringList.add("spam");
        assertEquals(" -\t\"eggs\"\n" +
                " -\t\"bacon\"\n" +
                " -\t\"spam\"", ConstraintToStringUtil.constraintListToString(stringList));
    }
}
