package com.nedap.archie.datetime;

import org.junit.Test;

import org.threeten.bp.*;
import org.threeten.bp.temporal.TemporalAccessor;

import static org.junit.Assert.assertEquals;

public class DateTimeSerializerFormattersTest {

    @Test
    public void serializeLocalDate() {
        assertEquals("2015-01-01", serializeDate(LocalDate.of(2015, 1, 1)));
        assertEquals("2019-01-14", serializeDate(LocalDate.of(2019, 1, 14)));
    }

    @Test
    public void serializeLocalDateTime() {
        assertEquals("2015-01-01T12:00:00", serializeDateTime(LocalDateTime.of(2015, 1, 1, 12, 0, 0, 0)));
        assertEquals("2015-01-01T12:01:00", serializeDateTime(LocalDateTime.of(2015, 1, 1, 12, 1, 0, 0)));
        assertEquals("2015-01-01T12:01:01", serializeDateTime(LocalDateTime.of(2015, 1, 1, 12, 1, 1, 0)));
        assertEquals("2015-01-01T12:01:01,1", serializeDateTime(LocalDateTime.of(2015, 1, 1, 12, 1, 1, 100000000)));
    }

    @Test
    public void serializeOffsetDateTime() {
        assertEquals("2015-01-01T12:00:00+01:00", serializeDateTime(OffsetDateTime.of(2015, 1, 1, 12, 0, 0, 0, ZoneOffset.of("+0100"))));
        assertEquals("2015-01-01T12:01:01+01:00", serializeDateTime(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 0, ZoneOffset.of("+0100"))));
        assertEquals("2015-01-01T12:01:01,1+01:00", serializeDateTime(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 100000000, ZoneOffset.of("+0100"))));
        assertEquals("2015-01-01T12:01:01,123+01:00", serializeDateTime(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 123000000, ZoneOffset.of("+0100"))));
        assertEquals("2015-01-01T12:01:01,123-02:00", serializeDateTime(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 123000000, ZoneOffset.of("-0200"))));
        assertEquals("2015-01-01T12:01:01Z", serializeDateTime(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 0, ZoneOffset.of("Z"))));
        assertEquals("2015-01-01T12:01:01,123Z", serializeDateTime(OffsetDateTime.of(2015, 1, 1, 12, 1, 1, 123000000, ZoneOffset.of("Z"))));
        assertEquals("2015-12-02T17:41:56,809Z", serializeDateTime(OffsetDateTime.of(2015, 12, 02, 17, 41, 56, 809000000, ZoneOffset.of("Z"))));
        assertEquals("2019-01-14T18:36:49,294666Z", serializeDateTime(OffsetDateTime.of(2019, 01, 14, 18, 36, 49, 294666666, ZoneOffset.of("Z"))));
    }

    @Test
    public void serializeYear() {
        assertEquals("2015", serializeDate(Year.of(2015)));
    }

    @Test
    public void serializeYearMonth() {
        assertEquals("2015-01", serializeDate(YearMonth.of(2015, 1)));
    }

    private String serializeDate(TemporalAccessor value) {
        return DateTimeSerializerFormatters.ISO_8601_DATE.format(value);
    }

    private String serializeDateTime(TemporalAccessor value) {
        return DateTimeSerializerFormatters.ISO_8601_DATE_TIME.format(value);
    }
}