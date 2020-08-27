package com.nedap.archie.rules.evaluation.evaluators;

import com.google.common.collect.Lists;
import com.nedap.archie.rules.evaluation.FunctionCallException;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 07/04/2017.
 */
public class FunctionUtil {

    public static double castToDouble(Value value) throws FunctionCallException {
        if(value.getValue() instanceof Long) {
            return ((Long) value.getValue()).doubleValue();
        } else if (value.getValue() instanceof Double) {
            return (double) value.getValue();
        }
        throw new FunctionCallException("cannot cast " + value.getValue().getClass() + " to a number");
    }

    /**
     * All arguments should have an equal list size OR list size of 1
     * @return If list sizes equal or 1 return this length, otherwise return -1
     */
    public static int checkEqualLengthOrOne(List<ValueList> arguments) {
        final int allowedLength = 1;
        int otherLength = -1;
        for (ValueList list : arguments) {
            if (list.size() != allowedLength) {
                if (otherLength != -1 && otherLength != list.size()) {
                    return -1;
                }
                otherLength = list.size();
            }
        }
        return otherLength == -1 ? allowedLength : otherLength;
    }

    public static ValueList checkAndHandleNull(ValueList leftValues, ValueList rightValues) {
        return checkAndHandleNull(Lists.newArrayList(leftValues, rightValues));
    }

    public static ValueList checkAndHandleNull(List<ValueList> arguments) {

        if(atLeastOneValueEmpty(arguments)) {
            ValueList result = new ValueList();
            result.addValue(Value.createNull(gatherPaths(arguments)));
            return result;
        }
        return null;
    }

    public static List<String> gatherPaths(List<ValueList> arguments) {
        List<String> result = new ArrayList<>();
        for(ValueList list:arguments) {
            if(!list.isEmpty()) {
                result.addAll(list.getAllPaths());
            }
        }
        return result;
    }

    private static boolean atLeastOneValueEmpty(List<ValueList> arguments) {
        for(ValueList list:arguments) {
            if(list.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
