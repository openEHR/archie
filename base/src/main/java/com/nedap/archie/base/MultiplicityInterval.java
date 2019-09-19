package com.nedap.archie.base;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by pieter.bos on 15/10/15.
 */
public class MultiplicityInterval extends Interval<Integer> implements Serializable {

    /**
     * Marker to use in string form of interval between limits.
     */
    public static final String MULTIPLICITY_RANGE_MARKER = "..";
    /**
     * Symbol to use to indicate upper limit unbounded.
     */
    public static final Character MULTIPLICITY_UNBOUNDED_MARKER = '*';

    public MultiplicityInterval() {
        super();
    }

    public MultiplicityInterval(int lower, int upper) {
        super(lower, upper);
    }

    public MultiplicityInterval(Integer lower, Boolean lowerIncluded, Boolean lowerUnbounded, Integer upper, Boolean upperIncluded, Boolean upperUnbounded) {
        setLower(lower);
        setLowerIncluded(lowerIncluded);
        setLowerUnbounded(lowerUnbounded);
        setUpper(upper);
        setUpperIncluded(upperIncluded);
        setUpperUnbounded(upperUnbounded);
    }

    /**
     * Equal to '0..*' or '*'
     * @return a new unbounded multiplicity interval
     */
    public static MultiplicityInterval unbounded() {
        MultiplicityInterval result = new MultiplicityInterval();
        result.setLower(0);
        result.setUpperUnbounded(true);
        return result;
    }
    
    public boolean isOpen() {
        return Integer.valueOf(0).equals(getLower()) && isUpperUnbounded() && isLowerIncluded();
    }

    public boolean isOptional() {
        return Integer.valueOf(0).equals(getLower()) && Integer.valueOf(1).equals(getUpper()) && !isUpperUnbounded() && isLowerIncluded() && isUpperIncluded();
    }

    public boolean isMandatory() {
        return !isLowerUnbounded() && getLower() >= 1 ;
    }

    public boolean isProhibited() {
        return Integer.valueOf(0).equals(getLower()) && Integer.valueOf(0).equals(getUpper()) && !isUpperUnbounded();
    }

    /**
     * Creates interval of type [0,inf)
     *
     * @return the created interval
     */
    public static MultiplicityInterval createOpen() {
        return new MultiplicityInterval(0, true, false, null, true, true);
    }

    /**
     * Creates interval of type [0,1]
     *
     * @return the created interval
     */
    public static MultiplicityInterval createOptional() {
        return new MultiplicityInterval(0, true, false, 1, true, false);
    }

    /**
     * Creates interval of type [1,1]
     *
     * @return the created interval
     */
    public static MultiplicityInterval createMandatory() {
        return new MultiplicityInterval(1, true, false, 1, true, false);
    }

    /**
     * Creates interval of type [0,1]
     *
     * @return the created interval
     */
    public static MultiplicityInterval createProhibited() {
        return new MultiplicityInterval(0, true, false, 0, true, false);
    }

    public static MultiplicityInterval createUpperUnbounded(Integer lower) {
        return new MultiplicityInterval(lower, true, false, 0, true, true);
    }

    public static MultiplicityInterval createBounded(int lower, int upper) {
        return new MultiplicityInterval(lower, true, false, upper, true, false);
    }

    @JsonIgnore
    public boolean upperIsOne() {
        return has(1) && !has(2);
    }

    @Override
    public String toString() {
        Integer lower = getLower();
        Integer upper = getUpper();
        StringBuilder result = new StringBuilder();
        if (isLowerUnbounded()) {
            result.append(MULTIPLICITY_UNBOUNDED_MARKER);
        } else {
            if (!isLowerIncluded()) {
                result.append(">");
            }
            result.append(lower);
        }
        result.append(MULTIPLICITY_RANGE_MARKER);
        if (isUpperUnbounded()) {
            result.append(MULTIPLICITY_UNBOUNDED_MARKER);
        } else {
            if(!isUpperIncluded()) {
                result.append("<");
            }
            result.append(upper);
        }
        return result.toString();
    }
}
