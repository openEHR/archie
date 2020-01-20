package com.nedap.archie.base;


import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * A tool to convert Durations from TemporalAmounts in the specific use case of Intervals
 * It handles the unit Month as an exact number of seconds to do a conversion. This is a bit tricky, since
 * a number of months has a lower number of seconds and an upper number of seconds. but we need some way of doing this...
 *
 * This may need a separate method for lower and upper, which returns the lower and upper bounds of the seconds in a month?
 */
class IntervalDurationConverter {

//    private final static long MINIMUM_SECONDS_IN_DAY = 60*60*23;
//    private final static long MINIMUM_SECONDS_IN_MONTH = 28*MINIMUM_SECONDS_IN_DAY;
//    private final static long MINIMUM_SECONDS_IN_WEEK = 7*MINIMUM_SECONDS_IN_DAY;
//
//    private final static long MAXIMUM_SECONDS_IN_DAY = 60*60*25;
//    private final static long MAXIMUM_SECONDS_IN_WEEK = 7*MAXIMUM_SECONDS_IN_DAY;
//    private final static long MAXIMUM_SECONDS_IN_MONTH = 28*MAXIMUM_SECONDS_IN_WEEK;

    /**
     * This is a literal copy of Duration.from, with the only change being that this supports adding
     * estimate TemporalAmounts
     * @param amount
     * @return
     */
    public static Duration from(TemporalAmount amount) {
        Objects.requireNonNull(amount, "amount");
        Duration duration = Duration.ofSeconds(0);
        for (TemporalUnit unit : amount.getUnits()) {
            duration = plus(duration, amount.get(unit), unit);
        }
        return duration;
    }


    /**
     * Returns a copy of this duration with the specified duration added. Works the same as {@link Duration#plus(Duration)},
     * but does work for estimate units such as weeks, months and years

     * @param amountToAdd  the amount to add, measured in terms of the unit, positive or negative
     * @param unit  the unit that the amount is measured in, must have an exact duration, not null
     * @return a {@code Duration} based on this duration with the specified duration added, not null
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    private static Duration plus(Duration value, long amountToAdd, TemporalUnit unit) {
        Objects.requireNonNull(unit, "unit");
        if (unit != DAYS && unit.isDurationEstimated()) {
            Duration duration = unit.getDuration().multipliedBy(amountToAdd);
            return value.plusSeconds(duration.getSeconds()).plusNanos(duration.getNano());
        } else {
            return value.plus(amountToAdd, unit);
        }
    }
}
