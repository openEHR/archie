package com.nedap.archie.opt14;

import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.base.terminology.TerminologyCode;

public class BaseTypesConverter {

    public static Cardinality convertCardinality(CARDINALITY cardinality14) {
        if(cardinality14 == null) {
            return null;
        }
        Cardinality result = new Cardinality();
        result.setInterval(convertMultiplicity(cardinality14.getInterval()));
        result.setOrdered(cardinality14.isIsOrdered());
        result.setUnique(cardinality14.isIsUnique());
        return result;
    }

    public static MultiplicityInterval convertMultiplicity(IntervalOfInteger existence) {
        if(existence == null) {
            return null;
        }
        return new MultiplicityInterval(
                existence.getLower(),
                existence.isLowerIncluded() == null ? true : existence.isLowerIncluded(),
                existence.isLowerUnbounded(),
                existence.getUpper(),
                existence.isUpperIncluded() == null ? true : existence.isUpperIncluded(),
                existence.isUpperUnbounded());
    }

    public static com.nedap.archie.base.Interval<Long> convertInterval(IntervalOfInteger range) {
        if(range == null) {
            return null;
        }
        return new com.nedap.archie.base.Interval<Long>(
                range.getLower() == null ? null : range.getLower().longValue() ,
                range.getUpper() == null ? null : range.getUpper().longValue() ,
                range.isLowerIncluded() == null ? true : range.isLowerIncluded(),
                range.isUpperIncluded() == null ? true : range.isUpperIncluded());
    }

    public static com.nedap.archie.base.Interval<Double> convertInterval(IntervalOfReal range) {
        if(range == null) {
            return null;
        }
        return new com.nedap.archie.base.Interval<Double>(
                range.getLower() == null ? null : range.getLower().doubleValue() ,
                range.getUpper() == null ? null : range.getUpper().doubleValue() ,
                range.isLowerIncluded() == null ? true : range.isLowerIncluded(),
                range.isUpperIncluded() == null ? true : range.isUpperIncluded());
    }

    public static TerminologyCode convert(CODEPHRASE definingCode) {
        if(definingCode == null) {
            return null;
        }
        return TerminologyCode.createFromString(definingCode.getTerminologyId().getValue(), null, definingCode.getCodeString());
    }

}
