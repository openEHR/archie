package com.nedap.archie.rules.evaluation;

import com.nedap.archie.rules.PrimitiveType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list of values, as evaluated by the rule evaluation.
 *
 * Every value has both a value and a set of paths used in calculating that value. See Value
 * Created by pieter.bos on 31/03/16.
 */
public class ValueList {
    private PrimitiveType type;
    private List<Value<?>> values = new ArrayList<>();

    private static Logger logger = LoggerFactory.getLogger(ValueList.class);

    public ValueList() {

    }

    public ValueList(List<Value<?>> values) {
        setValues(values);
        determineTypeFromValues();
    }

    public void determineTypeFromValues() {
        if(!values.isEmpty()) {
            this.type = PrimitiveType.fromJavaType(values.get(0).getValue().getClass());
        } else{
            this.type = PrimitiveType.Unknown;
        }
    }

    /*
     * Construct a value list of a single value, that does not have a path.
     */
    public ValueList(Object value) {
        this(value, Collections.emptyList());
    }

    /*
     * Construct a value list of a single value, that does not have a path.
     */
    public ValueList(Object value, PrimitiveType type) {
        this(value, Collections.emptyList());
        setType(type);
    }

    /**
     * Construct a value list of a single object, with its paths
     * @param value
     * @param paths
     */
    public ValueList(Object value, List<String> paths){
        if(value == null) {
            this.type = null;
            addValue(value, paths);
        } else {
            addValue(value, paths);

            this.type = PrimitiveType.fromJavaType(value.getClass());
        }
    }

    public PrimitiveType getType() {
        return type;
    }

    public void setType(PrimitiveType type) {
        this.type = type;
    }

    public List<Value<?>> getValues() {
        return values;
    }

    public List<Object> getValueObjects() {
        List<Object> result = new ArrayList<>();
        for(Value<?> value:values) {
            result.add(value.getValue());
        }
        return result;
    }

    public void setValues(List<Value<?>> values) {
        this.values = new ArrayList<>(values.size());
        for(Value<?> o:values) {
            addValue(o);
        }
    }

    public void addValue(Object value,  List<String> paths) {
        values.add(new Value<>(value, paths));
    }

    public void addValue(Value<?> value){
        values.add(value);
    }

    @Override
    public String toString() {
        return type + ": " + values;
    }

    public int size() {
        return values.size();
    }

    public Object getObject(int i) {
        return values.get(i).getValue();
    }

    public List<String> getPaths(int i) {
        return values.get(i).getPaths();
    }

    public Value<?> get(int i) {
        return values.get(i);
    }

    public boolean isEmpty() {
        boolean containsValue = false;
        for(Value<?> value:this.values) {
            if(!value.isNull()) {
                containsValue = true;
            }
        }
        return !containsValue;
    }

    public void addValues(ValueList evaluated) {
        for(Value<?> value:evaluated.getValues()) {
            addValue(value);
        }
    }

    public List<String> getAllPaths() {
        List<String> result = new ArrayList<>();
        for(Value<?> value:values) {
            result.addAll(value.getPaths());
        }
        return result;
    }

    /**
     * If this is a list of booleans and at least one of the values is false, return false. return true if all values are true.
     * Returns false if there are no booleans
     * @return the single boolean result of this value list
     */
    public boolean getSingleBooleanResult() {
        if(isEmpty()) {
            return false;
        }
        for(Value<?> singleResult: values) {
            Boolean singleBoolean = (Boolean) singleResult.getValue();
            if(singleBoolean != null && !singleBoolean) {
                return false;
            }
        }
        return true;
    }

    public boolean containsOnlyNullValues() {
        for(Value<?> value:values) {
            if(!value.isNull()) {
                return false;
            }
        }
        return true;
    }
}
