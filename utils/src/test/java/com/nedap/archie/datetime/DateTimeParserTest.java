package com.nedap.archie.datetime;

import org.junit.Test;
import org.threeten.extra.PeriodDuration;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;

import static org.junit.Assert.assertEquals;

/**
 * Created by pieter.bos on 02/03/16.
 */
public class DateTimeParserTest {
    @Test
    public void dateCorrect() {
        assertEquals(LocalDate.of(2015, 1, 1), DateTimeParsers.parseDateValue("2015-1-1"));
        assertEquals(YearMonth.of(2015, 1), DateTimeParsers.parseDateValue("2015-1"));
        assertEquals(Year.of(2015), DateTimeParsers.parseDateValue("2015"));
    }

    @Test
    public void datetimeCorrect() {
        assertEquals(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 100000000, ZoneOffset.of("+0100")), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01,1+0100"));
        assertEquals(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 100000000, ZoneOffset.of("+0100")), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01,1+0100"));
        assertEquals(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 0, ZoneOffset.of("+0100")), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01+0100"));
        assertEquals(LocalDateTime.of(2015, 1, 1, 12, 1, 1, 100000000), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01,1"));
        assertEquals(LocalDateTime.of(2015, 1, 1, 12, 1, 1, 0), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01"));
        assertEquals(LocalDateTime.of(2015, 1, 1, 12, 1, 0, 0), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01"));
        assertEquals(LocalDateTime.of(2015, 1, 1, 12, 0, 0, 0), DateTimeParsers.parseDateTimeValue("2015-1-1T12"));
        assertEquals(OffsetDateTime.of(2015, 1, 1, 12, 0, 0, 0, ZoneOffset.of("+0100")), DateTimeParsers.parseDateTimeValue("2015-1-1T12+0100"));
    }

    @Test
    public void timeCorrect() {
        assertEquals(OffsetTime.of(12, 1, 1, 100000000, ZoneOffset.of("+0100")), DateTimeParsers.parseTimeValue("12:01:01,1+0100"));
        assertEquals(OffsetTime.of(12, 1, 1, 0, ZoneOffset.of("+0100")), DateTimeParsers.parseTimeValue("12:01:01+0100"));
        assertEquals(LocalTime.of(12, 1, 1, 100000000), DateTimeParsers.parseTimeValue("12:01:01,1"));
        assertEquals(LocalTime.of(12, 1, 1, 123456789), DateTimeParsers.parseTimeValue("12:01:01,123456789"));
        assertEquals(LocalTime.of(12, 1, 1, 0), DateTimeParsers.parseTimeValue("12:01:01"));
        assertEquals(LocalTime.of(12, 1), DateTimeParsers.parseTimeValue("12:01"));
        assertEquals(OffsetTime.of(12, 1, 0, 0, ZoneOffset.of("+0100")), DateTimeParsers.parseTimeValue("12:01+0100"));
    }

    //CCH: 17.6.2019 added more compliance with ISO8601 as found commonly in openEHR payloads
    @Test
    public void dateTimeISO8601Test() {
        //use full stop instead of comma in microsecs
        assertEquals(LocalDateTime.of(2015, 1, 1, 12, 1, 1, 100000000), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01.1"));
        assertEquals(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 100000000, ZoneOffset.of("+0100")), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01,1+0100"));
        assertEquals(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 123000000, ZoneOffset.of("+0100")), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01.123+01:00"));
        assertEquals(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 123000000, ZoneOffset.of("-0200")), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01.123-02:00"));
        assertEquals(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 123000000, ZoneOffset.of("Z")), DateTimeParsers.parseDateTimeValue("2015-1-1T12:01:01.123Z"));
        assertEquals(OffsetDateTime.of(2015, 12, 02, 17, 41, 56, 809000000, ZoneOffset.of("Z")), DateTimeParsers.parseDateTimeValue("2015-12-02T17:41:56.809Z"));
        assertEquals(OffsetDateTime.of(2015, 12, 02, 17, 41, 56, 809000123, ZoneOffset.of("Z")), DateTimeParsers.parseDateTimeValue("2015-12-02T17:41:56.809000123Z"));
    }

    @Test
    public void dateTimeISO8601CompactTest() {
        //compact form
        assertEquals(OffsetDateTime.of(2019, 01, 14, 18, 36, 49, 294000000, ZoneOffset.of("Z")), DateTimeParsers.parseDateTimeValue("20190114T183649,294+0000"));
        assertEquals(LocalDate.of(2019, 1, 14), DateTimeParsers.parseDateValue("20190114"));
        assertEquals(LocalTime.of(18, 36, 49, 0), DateTimeParsers.parseTimeValue("183649"));
    }

    @Test
    public void durations() {
        assertEquals(Duration.of(12, ChronoUnit.HOURS), DateTimeParsers.parseDurationValue("PT12H"));
    }

    @Test
    public void negativeDurations() {
        TemporalAmount minusTwoSeconds = DateTimeParsers.parseDurationValue("-PT2S");
        assertEquals(Duration.of(-2, ChronoUnit.SECONDS), minusTwoSeconds);

        TemporalAmount minutsTwelHoursTwoSeconds = DateTimeParsers.parseDurationValue("-PT12H2S");
        assertEquals(Duration.of(-2 - 12*60*60, ChronoUnit.SECONDS), minutsTwelHoursTwoSeconds);

        TemporalAmount minusTwoYears = DateTimeParsers.parseDurationValue("-P2Y");
        assertEquals(Period.of(-2, 0, 0), minusTwoYears);

        TemporalAmount minusOneYear2Hours = DateTimeParsers.parseDurationValue("-P1YT2H");
        assertEquals(PeriodDuration.of(Period.of(-1 ,0, 0), Duration.of(-2, ChronoUnit.HOURS)), minusOneYear2Hours);
    }

}
