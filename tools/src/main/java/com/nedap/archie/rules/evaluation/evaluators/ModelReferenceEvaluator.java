package com.nedap.archie.rules.evaluation.evaluators;

import com.google.common.collect.Lists;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.rules.ModelReference;
import com.nedap.archie.rules.PrimitiveType;
import com.nedap.archie.rules.evaluation.Evaluator;
import com.nedap.archie.rules.evaluation.RuleEvaluation;
import com.nedap.archie.rules.evaluation.Value;
import com.nedap.archie.rules.evaluation.ValueList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pieter.bos on 04/04/16.
 */
public class ModelReferenceEvaluator implements Evaluator<ModelReference> {
    @Override
    public ValueList evaluate(RuleEvaluation<?> evaluation, ModelReference statement) {


        String variable = statement.getVariableReferencePrefix();
        String pathPrefix = "";
        if(variable != null) {
            //resolve variable and add path prefix
            ValueList value = evaluation.getVariableMap().get(variable);
            if(value == null) {
                throw new RuntimeException("Variable " + variable + " not defined");
            }
            if(value.size() > 1) {
                throw new IllegalStateException("");
            } else if (value.size() == 1) {

                if(value.getType() == PrimitiveType.ObjectReference) {
                    RMObjectWithPath reference = (RMObjectWithPath) value.get(0).getValue();
                    pathPrefix = reference.getPath();
                } else {
                    //TODO: this is not correct
                    if(value.get(0).getPaths().size() > 1) {
                        throw new IllegalStateException("");
                    }

                    pathPrefix = (String) value.get(0).getPaths().get(0);
                }



            } //0: do nothing, empty value, no path prefix
        }

        String path = pathPrefix + statement.getPath();

        List<RMObjectWithPath> rmObjectsWithPath;

        rmObjectsWithPath = evaluation.findListWithPaths(path);

        List<Value<?>> values = rmObjectsWithPath.stream().map(
            rmObjectWithPath ->
                new Value<>(rmObjectWithPath.getObject(), Lists.newArrayList(rmObjectWithPath.getPath())))
            .collect(Collectors.toList());

        return new ValueList(values);
    }

    @Override
    public List<Class<?>> getSupportedClasses() {
        return Lists.newArrayList(ModelReference.class);
    }
}
