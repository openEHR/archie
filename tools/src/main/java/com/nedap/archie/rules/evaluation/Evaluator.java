package com.nedap.archie.rules.evaluation;

import com.nedap.archie.rules.RuleElement;

import java.util.List;

/**
 * Created by pieter.bos on 31/03/16.
 */
public interface Evaluator<T extends RuleElement> {

    ValueList evaluate(RuleEvaluation<?> evaluation, T statement);

    List<Class<?>> getSupportedClasses();
}
