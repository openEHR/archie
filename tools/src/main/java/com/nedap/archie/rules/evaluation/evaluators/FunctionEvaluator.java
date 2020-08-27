package com.nedap.archie.rules.evaluation.evaluators;

import com.google.common.collect.Lists;
import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.Function;
import com.nedap.archie.rules.evaluation.Evaluator;
import com.nedap.archie.rules.evaluation.FunctionCallException;
import com.nedap.archie.rules.evaluation.FunctionImplementation;
import com.nedap.archie.rules.evaluation.RuleEvaluation;
import com.nedap.archie.rules.evaluation.ValueList;
import com.nedap.archie.rules.evaluation.evaluators.functions.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pieter.bos on 06/04/2017.
 */
public class FunctionEvaluator  implements Evaluator<Function> {

    private Map<String, FunctionImplementation> functions = new LinkedHashMap<>();

    public void registerFunction(FunctionImplementation function) {
        functions.put(function.getName(), function);
    }


    public FunctionEvaluator() {
        registerFunction(new Max());
        registerFunction(new Min());
        registerFunction(new Mean());
        registerFunction(new Sum());
        registerFunction(new FlatSum());
        registerFunction(new ValueWhenUndefined());
        registerFunction(new Round());
        registerFunction(new Ceil());
        registerFunction(new Floor());
    }

    @Override
    public ValueList evaluate(RuleEvaluation evaluation, Function function) {
        List<ValueList> argumentResults = new ArrayList<>();

        for(Expression argument: function.getArguments()) {
            argumentResults.add(evaluation.evaluate(argument));
        }

        FunctionImplementation functionImplementation = functions.get(function.getFunctionName());
        if(functionImplementation != null) {
            try {
                return functionImplementation.evaluate(argumentResults);
            } catch (FunctionCallException e) {
                throw new RuntimeException(e);//TODO: proper exceptions when evaluating rules
            }
        }
        throw new IllegalStateException("unknown function: " +  function.getFunctionName());
    }


    @Override
    public List<Class> getSupportedClasses() {
        return Lists.newArrayList(Function.class);
    }
}
