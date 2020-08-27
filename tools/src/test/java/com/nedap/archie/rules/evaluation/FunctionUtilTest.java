package com.nedap.archie.rules.evaluation;

import com.nedap.archie.rules.evaluation.evaluators.FunctionUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FunctionUtilTest {

    @Test
    public void checkEqualLengthOrOne() {
        ValueList valueList1 = new ValueList();
        ValueList valueList2 = new ValueList();
        ValueList valueList3 = new ValueList();
        Value<Double> value = new Value<>(1d);
        valueList1.addValue(value);
        valueList2.addValue(value);
        valueList2.addValue(value);
        valueList3.addValue(value);
        valueList3.addValue(value);
        valueList3.addValue(value);
        List<ValueList> arguments1 = new ArrayList<>();
        arguments1.add(valueList1);
        arguments1.add(valueList1);
        assertEquals(1, FunctionUtil.checkEqualLengthOrOne(arguments1));
        List<ValueList> arguments2 = new ArrayList<>();
        arguments2.add(valueList1);
        arguments2.add(valueList2);
        assertEquals(2, FunctionUtil.checkEqualLengthOrOne(arguments2));
        List<ValueList> arguments3 = new ArrayList<>();
        arguments3.add(valueList2);
        arguments3.add(valueList3);
        assertEquals(-1, FunctionUtil.checkEqualLengthOrOne(arguments3));
    }
}
