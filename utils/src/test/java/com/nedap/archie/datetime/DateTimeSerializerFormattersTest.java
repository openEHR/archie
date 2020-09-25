package com.nedap.archie.datetime;

import org.junit.Test;
import org.threeten.extra.PeriodDuration;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;

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

    @Test
    public void serializeNegativeDurations() {
        TemporalAmount minusTwoSeconds = Duration.of(-2, ChronoUnit.SECONDS);
        TemporalAmount minutsTwelHoursTwoSeconds = Duration.of(-2 - 12*60*60, ChronoUnit.SECONDS);
        TemporalAmount minusTwoYears = Period.of(-2, 0, 0);
        TemporalAmount minusOneYearOneHour = PeriodDuration.of(Period.of(-1 ,0, 0), Duration.of(-2, ChronoUnit.HOURS));
        TemporalAmount minusTwoHoursPeriodDuration = PeriodDuration.of(Period.ZERO, Duration.of(-2, ChronoUnit.HOURS));
        TemporalAmount minusOneYearPeriodDuration = PeriodDuration.of(Period.of(-1 ,0, 0), Duration.ZERO);

        assertEquals("-PT2S", DateTimeSerializerFormatters.serializeDuration(minusTwoSeconds));
        assertEquals("-P2Y", DateTimeSerializerFormatters.serializeDuration(minusTwoYears));
        assertEquals("-PT12H2S", DateTimeSerializerFormatters.serializeDuration(minutsTwelHoursTwoSeconds));
        assertEquals("-P1YT2H", DateTimeSerializerFormatters.serializeDuration(minusOneYearOneHour));
        assertEquals("-PT2H", DateTimeSerializerFormatters.serializeDuration(minusTwoHoursPeriodDuration));
        assertEquals("-P1Y", DateTimeSerializerFormatters.serializeDuration(minusOneYearPeriodDuration));

    }

    private String serializeDate(TemporalAccessor value) {
        return DateTimeSerializerFormatters.ISO_8601_DATE.format(value);
    }

    private String serializeDateTime(TemporalAccessor value) {
        return DateTimeSerializerFormatters.ISO_8601_DATE_TIME.format(value);
    }
}