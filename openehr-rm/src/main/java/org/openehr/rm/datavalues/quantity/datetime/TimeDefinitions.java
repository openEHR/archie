package org.openehr.rm.datavalues.quantity.datetime;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

public class TimeDefinitions {

    //as openehr defines them, different from what java time uses!
    public static double NOMINAL_DAYS_IN_YEAR = 365.24;
    public static double NOMINAL_DAYS_IN_MONTH = 30.42;
    public static int HOURS_IN_DAY = 24;
    public static int MINUTES_IN_HOUR = 60;
    public static int SECONDS_IN_MINUTE = 60;
    public static int SECONDS_IN_DAY = HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE;
    public static double NOMINAL_SECONDS_IN_MONTH = NOMINAL_DAYS_IN_MONTH * (long) SECONDS_IN_DAY;
    public static double NOMINAL_SECONDS_IN_YEAR = NOMINAL_DAYS_IN_YEAR * (long) SECONDS_IN_DAY;

    public static Double convertTemporalAmountToSeconds(TemporalAmount amount) {
        double result = 0d;
        for(TemporalUnit unit:amount.getUnits()) {
            if(unit instanceof ChronoUnit) {
                ChronoUnit chronoUnit = (ChronoUnit) unit;
                switch (chronoUnit) {
                    case MONTHS:
                        result += amount.get(unit) * NOMINAL_SECONDS_IN_MONTH;
                        break;
                    case YEARS:
                        result += amount.get(unit) * NOMINAL_SECONDS_IN_YEAR;
                        break;
                    case DECADES:
                        result += amount.get(unit) * 10l * NOMINAL_SECONDS_IN_YEAR;
                        break;
                    case CENTURIES:
                        result += amount.get(unit) * 100l * NOMINAL_SECONDS_IN_YEAR;
                        break;
                    case MILLENNIA:
                        result += amount.get(unit) * 1000l * NOMINAL_SECONDS_IN_YEAR;
                        break;
                    default:
                        double toAdd = chronoUnit.getDuration().getSeconds();
                        toAdd += chronoUnit.getDuration().getNano() / 1e9;
                        result += amount.get(unit) * toAdd;
                        break;
                }
            } else {
                double toAdd = unit.getDuration().getSeconds();
                toAdd += unit.getDuration().getNano() / 1000000d;
                result += amount.get(unit) * toAdd;
            }
        }
        return result;
    }
}
