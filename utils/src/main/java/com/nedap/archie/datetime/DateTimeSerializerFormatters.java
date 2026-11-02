package com.nedap.archie.datetime;

import org.threeten.extra.PeriodDuration;

import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAmount;

public class DateTimeSerializerFormatters {

    public static final DateTimeFormatter ISO_8601_DATE = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(ChronoField.YEAR)
            .optionalStart()
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .optionalStart()
            .appendLiteral('-')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .optionalEnd()
            .optionalEnd()
            .toFormatter();

    public static final DateTimeFormatter ISO_8601_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .optionalEnd()
            .optionalEnd()
            .optionalStart()
            .appendOffsetId()
            .optionalEnd()
            .toFormatter()
            .withDecimalStyle(DecimalStyle.STANDARD.withDecimalSeparator(','));

    public static final DateTimeFormatter ISO_8601_DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_8601_DATE)
            .appendLiteral('T')
            .append(ISO_8601_TIME)
            .toFormatter()
            .withDecimalStyle(DecimalStyle.STANDARD.withDecimalSeparator(','));

    public static String serializeDuration(TemporalAmount amount) {
        if(amount instanceof Duration) {
            return serializeDuration((Duration) amount);
        } else if (amount instanceof Period) {
            return serializeDuration((Period) amount);
        } else if (amount instanceof PeriodDuration) {
            return serializePeriodDuration((PeriodDuration) amount);
        }
        return amount.toString();
    }

    private static String serializeDuration(Duration duration) {
        if(duration.isNegative()) {
            return "-" + duration.negated();
        }
        return duration.toString();
    }

    private static String serializeDuration(Period period) {
        if(period.isNegative()) {
            return "-" + period.negated();
        }
        return period.toString();
    }

    private static String serializePeriodDuration(PeriodDuration periodDuration) {
        if(periodDuration.getPeriod().isNegative() || periodDuration.getDuration().isNegative()) {
            return "-" + periodDuration.negated();
        }
        return periodDuration.toString();
    }

}
