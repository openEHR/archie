package com.nedap.archie.aom;

import com.nedap.archie.aom.primitives.CDate;
import com.nedap.archie.base.Interval;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 23/02/16.
 */
public class DateConstraintsTest {

    @Test
    public void dateConstraints() {
        CDate unconstrained = new CDate();
        unconstrained.isValidValue(LocalDate.now());
        unconstrained.isValidValue(LocalDate.of(1972, Month.JANUARY, 1));

        CDate interval = new CDate();
        interval.addConstraint(new Interval<>(LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.DECEMBER, 31)));
        assertTrue(interval.isValidValue(LocalDate.of(2015, Month.JUNE, 1)));
        assertTrue(interval.isValidValue(LocalDate.of(2015, Month.JANUARY, 1)));
        assertTrue(interval.isValidValue(LocalDate.of(2015, Month.DECEMBER, 31)));
        assertFalse(interval.isValidValue(LocalDate.of(2016, Month.JANUARY, 1)));
        assertFalse(interval.isValidValue(LocalDate.of(2014, Month.DECEMBER, 31)));

        CDate twoInterVals = new CDate();
        twoInterVals.addConstraint(new Interval<>(LocalDate.of(2015, Month.JANUARY, 1), LocalDate.of(2015, Month.DECEMBER, 31)));
        twoInterVals.addConstraint(new Interval<>(LocalDate.of(2013, Month.JANUARY, 1), LocalDate.of(2013, Month.DECEMBER, 31)));
        assertTrue(twoInterVals.isValidValue(LocalDate.of(2015, Month.JUNE, 1)));
        assertTrue(twoInterVals.isValidValue(LocalDate.of(2013, Month.JUNE, 1)));
        assertFalse(twoInterVals.isValidValue(LocalDate.of(2014, Month.JUNE, 1)));
    }

    //TODO: date patterns. also the implementation :)
}
