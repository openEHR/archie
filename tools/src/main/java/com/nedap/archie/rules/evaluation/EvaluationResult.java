package com.nedap.archie.rules.evaluation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pieter.bos on 25/04/16.
 */
public class EvaluationResult {

    private List<AssertionResult> assertionResults = new ArrayList<>();

    public List<AssertionResult> getAssertionResults() {
        return assertionResults;
    }

    public void setAssertionResults(List<AssertionResult> assertionResults) {
        this.assertionResults = assertionResults;
    }


    protected void addAssertionResult(AssertionResult assertionResult) {
        this.assertionResults.add(assertionResult);

    }

    public List<String> getPathsThatMustExist() {
        List<String> result = new ArrayList<>();
        for (AssertionResult assertionResult : assertionResults) {
            result.addAll(assertionResult.getPathsThatMustExist());
        }
        return result;
    }

    public List<String> getPathsThatMustNotExist() {
        List<String> result = new ArrayList<>();
        for (AssertionResult assertionResult : assertionResults) {
            result.addAll(assertionResult.getPathsThatMustNotExist());

        }
        return result;
    }

    public Map<String, Value<?>> getSetPathValues() {
        Map<String, Value<?>> result = new LinkedHashMap<>();
        for (AssertionResult assertionResult : assertionResults) {
            result.putAll(assertionResult.getSetPathValues());
        }
        return result;
    }

    public Map<String, String> getPathsConstrainedToValueSets() {
        Map<String, String> result = new LinkedHashMap<>();
        for (AssertionResult assertionResult : assertionResults) {
            result.putAll(assertionResult.getPathsConstrainedToValueSets());
        }
        return result;
    }
}
