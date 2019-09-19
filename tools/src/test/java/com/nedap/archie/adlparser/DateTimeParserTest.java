package com.nedap.archie.adlparser;

import com.nedap.archie.adlparser.treewalkers.TemporalConstraintParser;
import com.nedap.archie.datetime.DateTimeParsers;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;

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
    }

    @Test
    public void dateTimeISO8601CompactTest() {
        //compact form
        assertEquals(OffsetDateTime.of(2019, 01, 14, 18, 36, 49, 294000000, ZoneOffset.of("Z")), DateTimeParsers.parseDateTimeValue("20190114T183649,294+0000"));
        assertEquals(LocalDate.of(2019, 1, 14), DateTimeParsers.parseDateValue("20190114"));
        assertEquals(LocalTime.of(18, 36, 49, 0), DateTimeParsers.parseTimeValue("183649"));
    }

}
