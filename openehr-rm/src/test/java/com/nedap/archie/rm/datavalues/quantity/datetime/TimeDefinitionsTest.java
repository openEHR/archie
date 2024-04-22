package com.nedap.archie.rm.datavalues.quantity.datetime;

import org.junit.Test;
import org.openehr.rm.datavalues.quantity.datetime.TimeDefinitions;
import org.threeten.extra.PeriodDuration;

import java.time.Duration;
import java.time.Period;

import static org.junit.Assert.assertEquals;

public class TimeDefinitionsTest {

    @Test
    public void convertPeriod() {
        Period period1 = Period.ofDays(2);
        assertEquals(2*24*60*60d, TimeDefinitions.convertTemporalAmountToSeconds(period1), 0.00001d);
        Period period2 = Period.ofMonths(3);
        assertEquals(3*TimeDefinitions.NOMINAL_DAYS_IN_MONTH*24*60*60d, TimeDefinitions.convertTemporalAmountToSeconds(period2), 0.00001d);
        Period period3 = Period.parse("P10Y2m");
        assertEquals(10*TimeDefinitions.NOMINAL_SECONDS_IN_YEAR  + 2 * TimeDefinitions.NOMINAL_SECONDS_IN_MONTH, TimeDefinitions.convertTemporalAmountToSeconds(period3), 0.00001d);
    }

    @Test
    public void convertDuration() {
        Duration duration1 = Duration.ofDays(20);
        assertEquals(20*24*60*60d, TimeDefinitions.convertTemporalAmountToSeconds(duration1), 0.00001d);
        Duration duration2 = Duration.ofHours(20);
        assertEquals(20*60*60d, TimeDefinitions.convertTemporalAmountToSeconds(duration2), 0.00001d);
        Duration duration3 = Duration.parse("PT20H3M");
        assertEquals(20*60*60d + 3*60d, TimeDefinitions.convertTemporalAmountToSeconds(duration3), 0.00001d);
        Duration duration4 = Duration.ofMillis(10);
        assertEquals(10/1000d, TimeDefinitions.convertTemporalAmountToSeconds(duration4), 0.00001d);
    }

    @Test
    public void convertPeriodDuration() {
        PeriodDuration periodDuration = PeriodDuration.of(Period.ofMonths(3), Duration.ofHours(5));
        assertEquals(3*TimeDefinitions.NOMINAL_SECONDS_IN_MONTH + 5 * 60*60d, TimeDefinitions.convertTemporalAmountToSeconds(periodDuration), 0.00001d);

    }
}
