package com.nedap.archie.rules.evaluation;

import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constant;
import com.nedap.archie.rules.ExpressionType;
import com.nedap.archie.rules.OperatorKind;
import org.junit.Test;
import org.threeten.extra.PeriodDuration;

import java.time.*;

import static org.junit.Assert.assertEquals;


/**
 * Created by pieter.bos on 01/04/16.
 */
public class BinaryOperatorTest {

    @Test
    public void plus() {
        testBinaryOperator(7l, ExpressionType.INTEGER, 4l, 3l, OperatorKind.plus);
    }

    @Test
    public void minus() {
        testBinaryOperator(1l, ExpressionType.INTEGER, 4l, 3l, OperatorKind.minus);
    }

    @Test
    public void multiply() {
        testBinaryOperator(12l, ExpressionType.INTEGER, 4l, 3l, OperatorKind.multiply);
    }

    @Test
    public void divide() {
        testBinaryOperator(4l, ExpressionType.INTEGER, 13l, 3l, OperatorKind.divide);
    }

    @Test
    public void modulo() {
        testBinaryOperator(1l, ExpressionType.INTEGER, 13l, 3l, OperatorKind.modulo);
    }

    @Test
    public void exponent() {
        testBinaryOperator(8l, ExpressionType.INTEGER, 2l, 3l, OperatorKind.exponent);
    }

    @Test
    public void plusReal() {
        testBinaryOperator(7d, ExpressionType.REAL, 4d, 3d, OperatorKind.plus);
    }

    @Test
    public void minusReal() {
        testBinaryOperator(1d, ExpressionType.REAL, 4d, 3d, OperatorKind.minus);
    }

    @Test
    public void multiplyReal() {
        testBinaryOperator(12d, ExpressionType.REAL, 4d, 3d, OperatorKind.multiply);
    }

    @Test
    public void divideReal() {
        testBinaryOperator(4d, ExpressionType.REAL, 12d, 3d, OperatorKind.divide);
    }

    @Test
    public void moduloReal() {
        testBinaryOperator(1d, ExpressionType.REAL, 13d, 3d, OperatorKind.modulo);
    }

    @Test
    public void exponentReal() {
        testBinaryOperator(8d, ExpressionType.REAL, 2d, 3d, OperatorKind.exponent);
    }

    @Test
    public void greaterThan() {
        // Date
        LocalDate ld1 = LocalDate.of(2023, 1, 1);
        LocalDate ld2 = LocalDate.of(2023, 1, 2);
        testBinaryOperator(false, ExpressionType.DATE, ld1, ld2, OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DATE, ld2, ld1, OperatorKind.gt);

        // Time
        LocalTime t1 = LocalTime.of(12, 0);
        LocalTime t2 = LocalTime.of(12, 15);
        testBinaryOperator(false, ExpressionType.TIME, t1, t2, OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.TIME, t2, t1, OperatorKind.gt);

        // DateTime
        LocalDateTime dt1 = LocalDateTime.of(ld1, t1);
        LocalDateTime dt2 = LocalDateTime.of(ld1, t2);
        testBinaryOperator(false, ExpressionType.DATETIME, dt1, dt2, OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DATETIME, dt2, dt1, OperatorKind.gt);

        // Period
        Period p1 = Period.of(1, 2, 3);
        Period p2 = Period.of(3, 2, 1);
        testBinaryOperator(false, ExpressionType.DURATION, p1, p2, OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DURATION, p2, p1, OperatorKind.gt);

        // Duration
        Duration d1 = Duration.ofHours(1);
        Duration d2 = d1.plus(Duration.ofSeconds(1));
        testBinaryOperator(false, ExpressionType.DURATION, d1, d2, OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DURATION, d2, d1, OperatorKind.gt);

        // PeriodDuration
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p1, d1), PeriodDuration.of(p1, d2), OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1, d2), PeriodDuration.of(p1, d1), OperatorKind.gt);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(p2), OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p2), PeriodDuration.of(p1), OperatorKind.gt);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(d1), PeriodDuration.of(d2), OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d2), PeriodDuration.of(d1), OperatorKind.gt);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(d2), PeriodDuration.of(p1), OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(d2), OperatorKind.gt);
    }

    @Test
    public void greaterEqual() {
        // Date
        LocalDate ld1 = LocalDate.of(2023, 1, 1);
        LocalDate ld2 = LocalDate.of(2023, 1, 1);
        LocalDate ld3 = LocalDate.of(2023, 1, 2);
        testBinaryOperator(false, ExpressionType.DATE, ld1, ld3, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DATE, ld3, ld1, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DATE, ld1, ld2, OperatorKind.ge);

        // Time
        LocalTime t1 = LocalTime.of(12, 0);
        LocalTime t2 = LocalTime.of(12, 0);
        LocalTime t3 = LocalTime.of(12, 15);
        testBinaryOperator(false, ExpressionType.TIME, t1, t3, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.TIME, t3, t1, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.TIME, t1, t2, OperatorKind.ge);

        // DateTime
        LocalDateTime dt1 = LocalDateTime.of(ld1, t1);
        LocalDateTime dt2 = LocalDateTime.of(ld1, t3);
        testBinaryOperator(false, ExpressionType.DATETIME, dt1, dt2, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DATETIME, dt2, dt1, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DATETIME, dt1, LocalDateTime.of(ld1, t2), OperatorKind.ge);

        // Period
        Period p1 = Period.of(1, 2, 3);
        Period p2 = Period.of(0, 14, 3);
        Period p3 = Period.of(3, 2, 1);
        testBinaryOperator(false, ExpressionType.DURATION, p1, p3, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, p3, p1, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, p1, p2, OperatorKind.ge);

        // Duration
        Duration d1 = Duration.ofHours(1);
        Duration d2 = Duration.ofMinutes(60);
        Duration d3 = d1.plus(Duration.ofSeconds(1));
        testBinaryOperator(false, ExpressionType.DURATION, d1, d3, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, d3, d1, OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, d1, d2, OperatorKind.ge);

        // PeriodDuration
        Period p4 = Period.ofDays(1);
        Duration d4 = Duration.ofDays(1);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p1, d1), PeriodDuration.of(p1, d3), OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1, d3), PeriodDuration.of(p1, d2), OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1, d2), PeriodDuration.of(p1, d1), OperatorKind.ge);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(p3), OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(p1), OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(p2), OperatorKind.ge);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(d1), PeriodDuration.of(d3), OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d1), PeriodDuration.of(d2), OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d2), PeriodDuration.of(d1), OperatorKind.ge);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(d2), PeriodDuration.of(p1), OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d4), PeriodDuration.of(p4), OperatorKind.ge);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(d1), OperatorKind.ge);
    }

    @Test
    public void lesserThan() {
        // Date
        LocalDate ld1 = LocalDate.of(2023, 1, 1);
        LocalDate ld2 = LocalDate.of(2023, 1, 2);
        testBinaryOperator(true, ExpressionType.DATE, ld1, ld2, OperatorKind.lt);
        testBinaryOperator(false, ExpressionType.DATE, ld2, ld1, OperatorKind.lt);

        // Time
        LocalTime t1 = LocalTime.of(12, 0);
        LocalTime t2 = LocalTime.of(12, 15);
        testBinaryOperator(true, ExpressionType.TIME, t1, t2, OperatorKind.lt);
        testBinaryOperator(false, ExpressionType.TIME, t2, t1, OperatorKind.lt);

        // DateTime
        LocalDateTime dt1 = LocalDateTime.of(ld1, t1);
        LocalDateTime dt2 = LocalDateTime.of(ld1, t2);
        testBinaryOperator(true, ExpressionType.DATETIME, dt1, dt2, OperatorKind.lt);
        testBinaryOperator(false, ExpressionType.DATETIME, dt2, dt1, OperatorKind.lt);

        // Period
        Period p1 = Period.of(1, 2, 3);
        Period p2 = Period.of(3, 2, 1);
        testBinaryOperator(true, ExpressionType.DURATION, p1, p2, OperatorKind.lt);
        testBinaryOperator(false, ExpressionType.DURATION, p2, p1, OperatorKind.lt);

        // Duration
        Duration d1 = Duration.ofHours(1);
        Duration d2 = d1.plus(Duration.ofSeconds(1));
        testBinaryOperator(true, ExpressionType.DURATION, d1, d2, OperatorKind.lt);
        testBinaryOperator(false, ExpressionType.DURATION, d2, d1, OperatorKind.lt);

        // PeriodDuration
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1, d1), PeriodDuration.of(p1, d2), OperatorKind.lt);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p1, d2), PeriodDuration.of(p1, d1), OperatorKind.lt);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(p2), OperatorKind.lt);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p2), PeriodDuration.of(p1), OperatorKind.lt);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d1), PeriodDuration.of(d2), OperatorKind.lt);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(d2), PeriodDuration.of(d1), OperatorKind.lt);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d2), PeriodDuration.of(p1), OperatorKind.lt);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(d2), OperatorKind.lt);
    }

    @Test
    public void lesserEqual() {
        // Date
        LocalDate ld1 = LocalDate.of(2023, 1, 1);
        LocalDate ld2 = LocalDate.of(2023, 1, 1);
        LocalDate ld3 = LocalDate.of(2023, 1, 2);
        testBinaryOperator(true, ExpressionType.DATE, ld1, ld3, OperatorKind.le);
        testBinaryOperator(false, ExpressionType.DATE, ld3, ld1, OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DATE, ld1, ld2, OperatorKind.le);

        // Time
        LocalTime t1 = LocalTime.of(12, 0);
        LocalTime t2 = LocalTime.of(12, 0);
        LocalTime t3 = LocalTime.of(12, 15);
        testBinaryOperator(true, ExpressionType.TIME, t1, t3, OperatorKind.le);
        testBinaryOperator(false, ExpressionType.TIME, t3, t1, OperatorKind.le);
        testBinaryOperator(true, ExpressionType.TIME, t1, t2, OperatorKind.le);

        // DateTime
        LocalDateTime dt1 = LocalDateTime.of(ld1, t1);
        LocalDateTime dt2 = LocalDateTime.of(ld1, t3);
        testBinaryOperator(true, ExpressionType.DATETIME, dt1, dt2, OperatorKind.le);
        testBinaryOperator(false, ExpressionType.DATETIME, dt2, dt1, OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DATETIME, dt1, LocalDateTime.of(ld1, t2), OperatorKind.le);

        // Period
        Period p1 = Period.of(1, 2, 3);
        Period p2 = Period.of(0, 14, 3);
        Period p3 = Period.of(3, 2, 1);
        testBinaryOperator(true, ExpressionType.DURATION, p1, p3, OperatorKind.le);
        testBinaryOperator(false, ExpressionType.DURATION, p3, p1, OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DURATION, p1, p2, OperatorKind.le);

        // Duration
        Duration d1 = Duration.ofHours(1);
        Duration d2 = Duration.ofMinutes(60);
        Duration d3 = d1.plus(Duration.ofSeconds(1));
        testBinaryOperator(true, ExpressionType.DURATION, d1, d3, OperatorKind.le);
        testBinaryOperator(false, ExpressionType.DURATION, d3, d1, OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DURATION, d1, d2, OperatorKind.le);

        // PeriodDuration
        Period p4 = Period.ofDays(1);
        Duration d4 = Duration.ofDays(1);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1, d1), PeriodDuration.of(p1, d3), OperatorKind.le);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p1, d3), PeriodDuration.of(p1, d2), OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1, d2), PeriodDuration.of(p1, d1), OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(p3), OperatorKind.le);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p3), PeriodDuration.of(p1), OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(p2), OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d1), PeriodDuration.of(d3), OperatorKind.le);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(d3), PeriodDuration.of(d1), OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d1), PeriodDuration.of(d2), OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d2), PeriodDuration.of(p1), OperatorKind.le);
        testBinaryOperator(false, ExpressionType.DURATION, PeriodDuration.of(p1), PeriodDuration.of(d1), OperatorKind.le);
        testBinaryOperator(true, ExpressionType.DURATION, PeriodDuration.of(d4), PeriodDuration.of(p4), OperatorKind.le);
    }

    @Test
    public void equal() {
        // Date
        testBinaryOperator(true, ExpressionType.DATE, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), OperatorKind.eq);
        testBinaryOperator(false, ExpressionType.DATE, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 2), OperatorKind.eq);

        // Time
        testBinaryOperator(true, ExpressionType.TIME, LocalTime.of(10, 0), LocalTime.of(10, 0), OperatorKind.eq);
        testBinaryOperator(false, ExpressionType.TIME, LocalTime.of(10, 0), LocalTime.of(11, 0), OperatorKind.eq);

        // DateTime
        testBinaryOperator(true, ExpressionType.DATETIME, LocalDateTime.of(2023, 1, 1, 10, 0), LocalDateTime.of(2023, 1, 1, 10, 0), OperatorKind.eq);
        testBinaryOperator(false, ExpressionType.DATETIME, LocalDateTime.of(2023, 1, 1, 10, 0), LocalDateTime.of(2023, 1, 1, 10, 1), OperatorKind.eq);

        // Period
        testBinaryOperator(true, ExpressionType.DURATION, Period.of(1, 0, 0), Period.of(0, 12, 0), OperatorKind.eq);
        testBinaryOperator(true, ExpressionType.DURATION, Period.of(1, 0, 0), Period.of(0, 0, 360), OperatorKind.eq);
        testBinaryOperator(false, ExpressionType.DURATION, Period.of(1, 0, 0), Period.of(0, 0, 365), OperatorKind.eq);

        // Duration
        testBinaryOperator(true, ExpressionType.DURATION, Duration.ofHours(1), Duration.ofSeconds(3600), OperatorKind.eq);
        testBinaryOperator(false, ExpressionType.DURATION, Duration.ofHours(1), Duration.ofSeconds(3599), OperatorKind.eq);
    }

    @Test
    public void notEqual() {
        // Date
        testBinaryOperator(false, ExpressionType.DATE, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 1), OperatorKind.ne);
        testBinaryOperator(true, ExpressionType.DATE, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 2), OperatorKind.ne);

        // Time
        testBinaryOperator(false, ExpressionType.TIME, LocalTime.of(10, 0), LocalTime.of(10, 0), OperatorKind.ne);
        testBinaryOperator(true, ExpressionType.TIME, LocalTime.of(10, 0), LocalTime.of(11, 0), OperatorKind.ne);

        // DateTime
        testBinaryOperator(false, ExpressionType.DATETIME, LocalDateTime.of(2023, 1, 1, 10, 0), LocalDateTime.of(2023, 1, 1, 10, 0), OperatorKind.ne);
        testBinaryOperator(true, ExpressionType.DATETIME, LocalDateTime.of(2023, 1, 1, 10, 0), LocalDateTime.of(2023, 1, 1, 10, 1), OperatorKind.ne);

        // Period
        testBinaryOperator(false, ExpressionType.DURATION, Period.of(1, 0, 0), Period.of(0, 12, 0), OperatorKind.ne);
        testBinaryOperator(false, ExpressionType.DURATION, Period.of(1, 0, 0), Period.of(0, 0, 360), OperatorKind.ne);
        testBinaryOperator(true, ExpressionType.DURATION, Period.of(1, 0, 0), Period.of(0, 0, 365), OperatorKind.ne);

        // Duration
        testBinaryOperator(false, ExpressionType.DURATION, Duration.ofHours(1), Duration.ofSeconds(3600), OperatorKind.ne);
        testBinaryOperator(true, ExpressionType.DURATION, Duration.ofHours(1), Duration.ofSeconds(3599), OperatorKind.ne);
    }

    @Test
    public void or() {
        testBinaryOperator(true, ExpressionType.BOOLEAN, null, true, OperatorKind.or);
        testBinaryOperator(true, ExpressionType.BOOLEAN, true, null, OperatorKind.or);
        testBinaryOperator(null, ExpressionType.BOOLEAN, false, null, OperatorKind.or);
        testBinaryOperator(null, ExpressionType.BOOLEAN, null, false, OperatorKind.or);
        testBinaryOperator(null, ExpressionType.BOOLEAN, null, null, OperatorKind.or);
        testBinaryOperator(false, ExpressionType.BOOLEAN, false, false, OperatorKind.or);
        testBinaryOperator(true, ExpressionType.BOOLEAN, true, false, OperatorKind.or);
        testBinaryOperator(true, ExpressionType.BOOLEAN, false, true, OperatorKind.or);
        testBinaryOperator(true, ExpressionType.BOOLEAN, true, true, OperatorKind.or);
    }

    @Test
    public void integerLongConversion() {
        //integers must be automatically converted to Longs in ConstantEvaluator
        testBinaryOperator(8l, ExpressionType.INTEGER, 2, 3l, OperatorKind.exponent);
    }

    @Test
    public void floatToDoubleConversion() {
        //floats must be automatically converted to Doubles in ConstantEvaluator
        testBinaryOperator(8.0d, ExpressionType.INTEGER, 2.0f, 3l, OperatorKind.exponent);
    }


    private void testBinaryOperator(Object expected, ExpressionType type, Object left, Object right, OperatorKind operatorKind) {
        BinaryOperator operator = new BinaryOperator();
        operator.setOperator(operatorKind);
        Constant<?> leftConstant = new Constant<>(type, left);
        Constant<?> rightConstant = new Constant<>(type, right);
        operator.addOperand(leftConstant);
        operator.addOperand(rightConstant);
        RuleEvaluation<?> eval = new RuleEvaluation<>(OpenEhrRmInfoLookup.getInstance(), null);//should be archetype, not very relevant here
        assertEquals(expected, eval.evaluate(operator).getValueObjects().get(0));
    }

}
