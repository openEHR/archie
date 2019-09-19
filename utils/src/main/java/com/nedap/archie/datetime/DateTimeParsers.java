package com.nedap.archie.datetime;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;

/**
 * ISO date time parsers
 */
public class DateTimeParsers {

    public static TemporalAccessor parseDateTimeValue(String text) {
        try {
            return DateTimeFormatters.ISO_8601_DATE_TIME_WITH_OPTIONAL_MICROS.parseBest(text, OffsetDateTime::from, LocalDateTime::from);
        } catch (DateTimeParseException e) {
            try {
                //Not parseable as a standard public object from datetime. We do not implement our own yet (we could!)
                //so fallback to the Parsed object. The Parsed object is package-private, so cannot be added as a reference
                //to the parseBest query, unfortunately.
                return DateTimeFormatters.ISO_8601_DATE_TIME_WITH_OPTIONAL_MICROS.parse(text);
            } catch (DateTimeParseException e1) {
                try {
                    //some more interesting date_time expression without hyphens...
                    return DateTimeFormatters.ISO_8601_DATE_TIME_COMPACT.parseBest(text, OffsetDateTime::from,  LocalDateTime::from);
                } catch (DateTimeParseException e2) {
                    throw new IllegalArgumentException(e2.getMessage() + ":" + text);
                }
            }
        }
    }

    public static TemporalAccessor parseTimeValue(String text) {
        try {
            return DateTimeFormatters.ISO_8601_TIME_COMPACT.parseBest(text, OffsetTime::from, LocalTime::from);
        }
        catch (Exception e0) {
            try {
                return DateTimeFormatters.ISO_8601_TIME.parseBest(text, OffsetTime::from, LocalTime::from);
            } catch (DateTimeParseException e) {
                try {
                    //Not parseable as a standard public object from datetime. We do not implement our own yet (we could!)
                    //so fallback to the Parsed object. The Parsed object is package-private, so cannot be added as a reference
                    //to the parseBest query, unfortunately.
                    return DateTimeFormatters.ISO_8601_TIME.parse(text);
                } catch (DateTimeParseException e1) {
                    throw new IllegalArgumentException(e1.getMessage() + ":" + text);
                }
            }
        }
    }

    public static Temporal parseDateValue(String text) {
        try {
            return (Temporal) DateTimeFormatters.ISO_8601_DATE_COMPACT.parseBest(text, LocalDate::from, YearMonth::from, Year::from);
        } catch (DateTimeParseException e) {
            try {
                return (Temporal) DateTimeFormatters.ISO_8601_DATE.parseBest(text, LocalDate::from, YearMonth::from, Year::from);
            }
            catch (DateTimeParseException e1) {
                throw new IllegalArgumentException(e.getMessage() + ":" + text);
            }
        }
    }

    public static TemporalAmount parseDurationValue(String text) {
        try {
            if(text.startsWith("PT")) {
                return Duration.parse(text);
            } else {
                return Period.parse(text);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(e.getMessage() + ":" + text);
        }
    }
}
