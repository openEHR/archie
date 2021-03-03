package com.nedap.archie.rules.evaluation.evaluators.functions;

import com.nedap.archie.rules.evaluation.FunctionCallException;
import com.nedap.archie.rules.evaluation.FunctionImplementation;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 07/04/2017.
 */
public class ValueWhenUndefined implements FunctionImplementation {
    @Override
    public String getName() {
        return "value_when_undefined";
    }

    @Override
    public ValueList evaluate(List<ValueList> arguments) throws FunctionCallException {
        if(arguments.size() != 2) {
            throw new FunctionCallException("value_when_undefined expects two arguments, but got " + arguments.size());
        }
        ValueList valueWhenUndefinedList = arguments.get(0);
        if(valueWhenUndefinedList.size() != 1) {
            throw new FunctionCallException("The first argument of value_when_undefined must evaluate to a single value, but got " + valueWhenUndefinedList.size());
        }
        Value<?> valueWhenUndefined = valueWhenUndefinedList.get(0);
        ValueList argument = arguments.get(1);

        ValueList result = new ValueList();
        if(argument.isEmpty()) {
            result.addValue(valueWhenUndefined.getValue(), new ArrayList<>(valueWhenUndefined.getPaths()));
            result.setType(valueWhenUndefinedList.getType());
        } else {
            for (Value<?> value : argument.getValues()) {
                if (value.isNull()) {
                    List<String> paths = new ArrayList<>(value.getPaths());
                    paths.addAll(valueWhenUndefined.getPaths());
                    result.addValue(new Value<>(valueWhenUndefined.getValue(), paths));
                } else {
                    result.addValue(value);
                }
            }
            result.setType(argument.getType());
        }

        return result;
    }
}
