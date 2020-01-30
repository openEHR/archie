package com.nedap.archie.adlparser;

import com.nedap.archie.aom.primitives.CDate;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.base.Interval;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAmount;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 30/10/15.
 */
public class TemporalConstraintParserTest extends PrimitivesConstraintParserTest {


    @Before
    public void setup() throws Exception {
        Locale.setDefault(Locale.US);
        super.setup();
    }

    @Test
    public void durationPattern() {
        assertEquals("Pw", this.<CDuration>getAttribute("duration_attr1").getPatternedConstraint());
        //		duration_attr33 matches {PdThms/|P38W..P39W4D|}
        assertDurationInterval(new Interval(Period.ofWeeks(38), Period.ofWeeks(39).plus(Period.ofDays(4))), "duration_attr33");
        assertEquals("PdThms", this.<CDuration>getAttribute("duration_attr33").getPatternedConstraint());
    }

    @Test
    public void singleDuration() {
        assertSingleDuration(Duration.ofSeconds(0), "duration_attr9");
        assertSingleDuration(Period.ofDays(1), "duration_attr11");
        assertSingleDuration(Duration.ofHours(2).plus(Duration.ofMinutes(5)), "duration_attr16");
        assertSingleDuration(Duration.ofHours(1).plus(Duration.ofMinutes(30)), "duration_attr19");
    }

    @Test
    public void intervalDuration() {
        /*duration_attr12 matches {|P38W..P39W4D|}
        duration_attr13 matches {|>P38W..P39W4D|}
        duration_attr14 matches {|P38W..<P39W4D|}
        duration_attr15 matches {|>P38W..<P39W4D|}*/

        assertDurationInterval(
                new Interval<>(Period.ofWeeks(38), Period.ofWeeks(39).plus(Period.ofDays(4))),
                "duration_attr12");
        assertDurationInterval(
                new Interval<>(Period.ofWeeks(38), Period.ofWeeks(39).plus(Period.ofDays(4)), false, true),
                "duration_attr13");
        assertDurationInterval(
                new Interval<>(Period.ofWeeks(38), Period.ofWeeks(39).plus(Period.ofDays(4)), true, false),
                "duration_attr14");
        assertDurationInterval(
                new Interval<>(Period.ofWeeks(38), Period.ofWeeks(39).plus(Period.ofDays(4)), false, false),
                "duration_attr15");

    }

    @Test
    public void relopDuration() {
        //"duration_attr18 matches {|<=PT1H|}"
        //TODO: add more to test file!
        assertDurationInterval(Interval.lowerUnbounded(Duration.ofHours(1), true),
                "duration_attr18");

    }

    @Test
    public void assumedValues() throws Exception {
        archetype = getAssumedValuesArchetype();
        CDate dateAttr4 = getAttribute("date_attr4");
        assertEquals("yyyy-??-XX", dateAttr4.getPatternedConstraint());
        assertEquals(1995, dateAttr4.getAssumedValue().get(ChronoField.YEAR));
        assertEquals(3, dateAttr4.getAssumedValue().get(ChronoField.MONTH_OF_YEAR));


    }

    private void assertDurationInterval(Interval<TemporalAmount> expected, String attribute) {
        CDuration duration = getAttribute(attribute);
        assertEquals(1, duration.getConstraint().size());
        Interval<TemporalAmount> interval = duration.getConstraint().get(0);
        assertEquals(expected, interval);
    }

    private void assertSingleDuration(TemporalAmount amount, String attribute) {
        CDuration duration = getAttribute(attribute);
        assertEquals(1, duration.getConstraint().size());
        Interval<TemporalAmount> interval = duration.getConstraint().get(0);
        assertEquals(amount, interval.getLower());
        assertEquals(interval.getLower(), interval.getUpper());
        assertFalse(interval.isLowerUnbounded());
        assertFalse(interval.isUpperUnbounded());
        assertTrue(interval.isLowerIncluded());
        assertTrue(interval.isUpperIncluded());
    }


}
