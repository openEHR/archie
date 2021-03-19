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

/**
 * flat_sum function which will sum the argument values always return a single value.
 */
public class FlatSum implements FunctionImplementation {
    @Override
    public String getName() {
        return "flat_sum";
    }

    @Override
    public ValueList evaluate(List<ValueList> arguments) throws FunctionCallException {
        //if one of the values is null, return null.
        ValueList possiblyNullResult = checkAndHandleNull(arguments);
        if(possiblyNullResult != null) {
            possiblyNullResult.setType(PrimitiveType.Real);
            return possiblyNullResult;
        }

        double sum = 0.0;
        ValueList result = new ValueList();
        List<String> paths = new ArrayList<>();

        for (ValueList argument: arguments) {
            List<Value<?>> values = argument.getValues();
            for (Value<?> val: values) {
                if (!val.isNull()) {
                    sum += castToDouble(val);
                }
                paths.addAll(val.getPaths());
            }
        }
        result.addValue(sum, paths);
        result.setType(PrimitiveType.Real);
        return result;
    }
}