package com.nedap.archie.datetime;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;
import org.threeten.bp.Period;
import org.threeten.bp.Year;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.temporal.Temporal;
import org.threeten.bp.temporal.TemporalAccessor;
import org.threeten.bp.temporal.TemporalAmount;

/**
 * ISO date time parsers
 */
public class DateTimeParsers {

    public static TemporalAccessor parseDateTimeValue(String text) {
        try {
            return DateTimeFormatters.ISO_8601_DATE_TIME.parseBest(text, OffsetDateTime::from, LocalDateTime::from);
        } catch (DateTimeParseException e) {
            try {
                //Not parseable as a standard public object from datetime. We do not implement our own yet (we could!)
                //so fallback to the Parsed object. The Parsed object is package-private, so cannot be added as a reference
                //to the parseBest query, unfortunately.
                return DateTimeFormatters.ISO_8601_DATE_TIME.parse(text);
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
