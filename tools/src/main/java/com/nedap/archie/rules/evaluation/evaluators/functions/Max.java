package com.nedap.archie.rules.evaluation.evaluators.functions;

import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.rules.evaluation.FunctionCallException;
import com.nedap.archie.rules.evaluation.FunctionImplementation;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.ArrayList;
import java.util.List;

import static com.nedap.archie.rules.evaluation.evaluators.FunctionUtil.*;

/**
 * Created by pieter.bos on 07/04/2017.
 */
public class Max implements FunctionImplementation {
    @Override
    public String getName() {
        return "max";
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
        if(length == -1) {
            throw new FunctionCallException("value lists of max operator not the same length");
        }
        ValueList result = new ValueList();
        result.setType(PrimitiveType.Real);
        for(int i = 0; i < length; i++) {
            Double max = null;
            List<String> paths = new ArrayList<>();
            for(ValueList list: arguments) {
                Value value = list.get(i);
                if(!value.isNull() && ((max == null) || castToDouble(value) > max)) {
                    max = castToDouble(value);
                }
                paths.addAll(value.getPaths());
            }
            result.addValue(max, paths);
        }

        return result;
    }


}
