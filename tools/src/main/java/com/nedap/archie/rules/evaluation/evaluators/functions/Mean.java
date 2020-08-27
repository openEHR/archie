package com.nedap.archie.rules.evaluation.evaluators.functions;

import com.nedap.archie.rules.evaluation.FunctionCallException;
import com.nedap.archie.rules.evaluation.FunctionImplementation;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.List;

/**
 * Created by pieter.bos on 07/04/2017.
 */
public class Mean implements FunctionImplementation {
    @Override
    public String getName() {
        return "mean";
    }

    @Override
    public ValueList evaluate(List<ValueList> arguments) throws FunctionCallException {
        Sum sum = new Sum();
        ValueList values = sum.evaluate(arguments);
        ValueList result = new ValueList();
        for(Value value:values.getValues()) {
            if(value.isNull()) {
                result.addValue(null, value.getPaths());
            } else if(value.getValue() instanceof Number) {
                result.addValue(((Number) value.getValue()).doubleValue()/arguments.size(), value.getPaths());
            }
        }
        return result;
    }
}