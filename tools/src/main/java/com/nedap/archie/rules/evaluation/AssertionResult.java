package com.nedap.archie.rules.evaluation;

import com.nedap.archie.rules.Expression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pieter.bos on 04/04/16.
 */
public class AssertionResult {

    private String tag;
    private Expression assertion;
    /**
     * The raw result: Did all the separate checks for this assertion pass?
     */
    private ValueList rawResult;
    /**
     * The result: did this assertion pass?
     */
    private boolean result;


    /**
     * Paths that must have a specific value. Will be set even if this path already has the specific value,
     * to let the UI know that this field can NOT manually be changed by the user right now
     */
    private Map<String, Value<?>> setPathValues = new LinkedHashMap<>();
    /**
     * Paths that must exist. Will be set even if it does exist, to let the UI know it should not be removed
     */
    private List<String> pathsThatMustExist = new ArrayList<>();
    /**
     * Paths that must not exist. Will be set even if it does not exist, to let the UI know if should not be added.
     */
    private List<String> pathsThatMustNotExist = new ArrayList<>();

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Expression getAssertion() {
        return assertion;
    }

    public void setAssertion(Expression assertion) {
        this.assertion = assertion;
    }

    public ValueList getRawResult() {
        return rawResult;
    }

    public void setRawResult(ValueList rawResult) {
        this.rawResult = rawResult;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Map<String, Value<?>> getSetPathValues() {
        return setPathValues;
    }

    public void setSetPathValues(Map<String, Value<?>> setPathValues) {
        this.setPathValues = setPathValues;
    }

    public List<String> getPathsThatMustExist() {
        return pathsThatMustExist;
    }

    public void setPathsThatMustExist(List<String> pathsThatMustExist) {
        this.pathsThatMustExist = pathsThatMustExist;
    }

    public List<String> getPathsThatMustNotExist() {
        return pathsThatMustNotExist;
    }

    public void setPathsThatMustNotExist(List<String> pathsThatMustNotExist) {
        this.pathsThatMustNotExist = pathsThatMustNotExist;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("assertion");
        stringBuilder.append(" ");
        if(tag != null) {
            stringBuilder.append(tag);
        } else {
            stringBuilder.append(assertion);
        }
        if(result) {
            stringBuilder.append(" succeeded");
        } else {
            stringBuilder.append(" failed");
        }
        return stringBuilder.toString();
    }

    public void addPathThatMustExist(String path) {
        pathsThatMustExist.add(path);
    }

    public void addPathThatMustNotExist(String path) {
        pathsThatMustNotExist.add(path);
    }

    public void addPathsThatMustNotExist(List<String> path) {
        pathsThatMustNotExist.addAll(path);
    }

    public void setSetPathValue(String path, ValueList values) {
        for(Value<?> value: values.getValues()) {
            //TODO
            setPathValues.put(path, value);
        }

    }
}
