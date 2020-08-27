package com.nedap.archie.rules.evaluation.evaluators.functions;

import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.rules.evaluation.FunctionCallException;
import com.nedap.archie.rules.evaluation.FunctionImplementation;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.ArrayList;
import java.util.List;

import static com.nedap.archie.rules.evaluation.evaluators.FunctionUtil.castToDouble;
import static com.nedap.archie.rules.evaluation.evaluators.FunctionUtil.checkAndHandleNull;
import static com.nedap.archie.rules.evaluation.evaluators.FunctionUtil.checkEqualLengthOrOne;

/**
 * Created by pieter.bos on 07/04/2017.
 */
public class Sum implements FunctionImplementation {
    @Override
    public String getName() {
        return "sum";
    }

    @Override
    public ValueList evaluate(List<ValueList> arguments) throws FunctionCallException {
        //if one of the values is null, return null.
        ValueList possiblyNullResult = checkAndHandleNull(arguments);
        if(possiblyNullResult != null) {
            possiblyNullResult.setType(PrimitiveType.Real);
            return possiblyNullResult;
        }

        //check that all valueList are equal length or 1 length
        int length = checkEqualLengthOrOne(arguments);

        ValueList result = new ValueList();

        for(int i = 0; i < length;i++) {
            Double sum = 0.0;
            List<String> paths = new ArrayList<>();
            for (ValueList argument: arguments) {
                if (argument.getValues().size() == 1) {
                    if(argument.getValues().get(0).isNull()) {
                        sum = null;
                        break;
                    }
                    paths.addAll(argument.getValues().get(0).getPaths());
                    Object value = argument.getValues().get(0).getValue();
                    if(value instanceof Number) {
                        sum += ((Number) value).doubleValue();
                    } else {
                        throw new FunctionCallException("argument of sum is not a number");
                    }
                } else {
                    if(argument.getValues().get(i).isNull()) {
                        sum = null;
                        break;
                    }
                    paths.addAll(argument.getValues().get(i).getPaths());
                    Object value = argument.getValues().get(i).getValue();
                    if(value instanceof Number) {
                        sum += ((Number) value).doubleValue();
                    } else {
                        throw new FunctionCallException("argument of sum is not a number");
                    }
                }
            }
            result.addValue(sum, paths);
        }

        result.setType(PrimitiveType.Real);
        return result;
    }
}