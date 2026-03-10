package com.nedap.archie.xml.adapters;

import com.nedap.archie.base.Interval;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by pieter.bos on 28/07/16.
 */
public abstract class AbstractIntervalAdapter<ValueType,BoundType> extends XmlAdapter<Interval<ValueType>, Interval<BoundType>> {


    private final XmlAdapter<ValueType,BoundType> innerAdapter;

    public AbstractIntervalAdapter(XmlAdapter<ValueType,BoundType> innerAdapter) {
        this.innerAdapter = innerAdapter;
    }

    @Override
    public Interval<BoundType> unmarshal(Interval<ValueType> v) throws Exception {
        Interval<BoundType> result = new Interval<>();
        result.setLowerIncluded(v.isLowerIncluded());
        result.setUpperIncluded(v.isUpperIncluded());
        result.setLowerUnbounded(v.isLowerUnbounded());
        result.setUpperUnbounded(v.isUpperUnbounded());
        result.setLower(v.getLower() == null ? null : innerAdapter.unmarshal(v.getLower()));
        result.setUpper(v.getUpper() == null ? null : innerAdapter.unmarshal(v.getUpper()));
        return result;
    }

    @Override
    public Interval<ValueType> marshal(Interval<BoundType> v) throws Exception {
        Interval<ValueType> result = new Interval<>();
        result.setLowerIncluded(v.isLowerIncluded());
        result.setUpperIncluded(v.isUpperIncluded());
        result.setLowerUnbounded(v.isLowerUnbounded());
        result.setUpperUnbounded(v.isUpperUnbounded());

        result.setLower(v.getLower() == null ? null : innerAdapter.marshal(v.getLower()));
        result.setUpper(v.getUpper() == null ? null : innerAdapter.marshal(v.getUpper()));
        return result;
    }
}
