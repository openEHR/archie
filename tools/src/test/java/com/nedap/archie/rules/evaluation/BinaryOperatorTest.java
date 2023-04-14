package com.nedap.archie.rules.evaluation;

import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constant;
import com.nedap.archie.rules.ExpressionType;
import com.nedap.archie.rules.OperatorKind;
import com.nedap.archie.xml.JAXBUtil;
import org.junit.Test;

import java.time.*;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.Date;

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
        LocalDate d1 = LocalDate.of(2022, 1, 1);
        LocalDate d2 = LocalDate.of(2022, 1, 2);
        testBinaryOperator(false, ExpressionType.DATE, d1, d2, OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DATE, d2, d1, OperatorKind.gt);

        // Time
        LocalTime t1 = LocalTime.of(12, 0);
        LocalTime t2 = LocalTime.of(12, 15);
        testBinaryOperator(false, ExpressionType.TIME, t1, t2, OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.TIME, t2, t1, OperatorKind.gt);

        // DateTime
        LocalDateTime dt1 = LocalDateTime.of(d1, t1);
        LocalDateTime dt2 = LocalDateTime.of(d1, t2);
        testBinaryOperator(false, ExpressionType.DATETIME, dt1, dt2, OperatorKind.gt);
        testBinaryOperator(true, ExpressionType.DATETIME, dt2, dt1, OperatorKind.gt);
    }

    @Test
    public void greaterEqual() {

    }

    @Test
    public void lesserThan() {

    }

    @Test
    public void lesserEqual() {

    }

    @Test
    public void equal() {

    }

    @Test
    public void notEqual() {

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
        RuleEvaluation<?> eval = new RuleEvaluation<>(ArchieRMInfoLookup.getInstance(), null);//should be archetype, not very relevant here
        assertEquals(expected, eval.evaluate(operator).getValueObjects().get(0));
    }

}
