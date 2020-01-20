package com.nedap.archie.base;

import org.junit.Test;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IntervalOfPeriodTest {

    @Test
    public void testMonths() {
        Period oneMonth = Period.of(0,1, 0);
        Period twoMonths = Period.of(0,2, 0);
        Interval interval = new Interval<TemporalAmount>(oneMonth, twoMonths);
        Duration oneAndAHalfMonth = Duration.of(45, ChronoUnit.DAYS);
        assertTrue(interval.has(oneAndAHalfMonth));
        Duration threeMonths = Duration.of(31*3, ChronoUnit.DAYS);
        assertFalse(interval.has(threeMonths));


        assertTrue(interval.has(Period.of(0, 1, 0)));
        assertTrue(interval.has(Period.of(0, 2, 0)));

        //testing edge cases of Duration in hours would be even better, but once makes sense once we have proper
        //converter support, with difference between converting lower and upper values

    }

    @Test
    public void testDurations() {
        Duration oneHour = Duration.of(1, ChronoUnit.HOURS);
        Duration twoHour = Duration.of(2, ChronoUnit.HOURS);
        Interval interval = new Interval<TemporalAmount>(oneHour, twoHour);
        assertTrue(interval.has(Duration.of(60, ChronoUnit.MINUTES)));
        assertTrue(interval.has(Duration.of(120, ChronoUnit.MINUTES)));

        assertFalse(interval.has(Duration.of(59, ChronoUnit.MINUTES)));
        assertFalse(interval.has(Duration.of(121, ChronoUnit.MINUTES)));

        Duration oneAndAHalfMonth = Duration.of(45, ChronoUnit.DAYS);
        assertFalse(interval.has(oneAndAHalfMonth));

    }
}
