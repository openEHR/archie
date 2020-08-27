package com.nedap.archie.rules.evaluation.evaluators.functions;

import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.rules.evaluation.FunctionCallException;
import com.nedap.archie.rules.evaluation.FunctionImplementation;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.List;

import static com.nedap.archie.rules.evaluation.evaluators.FunctionUtil.checkAndHandleNull;

public class Ceil implements FunctionImplementation {
    @Override
    public String getName() {
        return "ceil";
    }

    @Override
    public ValueList evaluate(List<ValueList> arguments) throws FunctionCallException {
        if(arguments.size() != 1) {
            throw new FunctionCallException("ceil expects one argument, but got " + arguments.size());
        }

        //if one of the values is null, return null.
        ValueList possiblyNullResult = checkAndHandleNull(arguments);
        if(possiblyNullResult != null) {
            possiblyNullResult.setType(PrimitiveType.Integer);
            return possiblyNullResult;
        }

        ValueList result = new ValueList();
        result.setType(PrimitiveType.Integer);

        for(Value valueObject : arguments.get(0).getValues()) {
            result.addValue(new Value((long) Math.ceil((Double) valueObject.getValue())));
        }

        return result;
    }
}
