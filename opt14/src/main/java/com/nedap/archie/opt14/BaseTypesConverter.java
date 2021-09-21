package com.nedap.archie.opt14;

import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.DvURI;
import com.nedap.archie.rm.datavalues.TermMapping;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;
import com.nedap.archie.rm.support.identification.TerminologyId;

import com.nedap.archie.opt14.schema.*;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

class BaseTypesConverter {

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
        return new com.nedap.archie.base.Interval<>(
                range.getLower() == null ? null : range.getLower().longValue() ,
                range.getUpper() == null ? null : range.getUpper().longValue() ,
                range.isLowerIncluded() == null ? true : range.isLowerIncluded(),
                range.isUpperIncluded() == null ? true : range.isUpperIncluded());
    }

    public static com.nedap.archie.base.Interval<TemporalAmount> convertInterval(IntervalOfDuration range) {
        if(range == null) {
            return null;
        }
        return new com.nedap.archie.base.Interval<>(
                range.getLower() == null ? null : DateTimeParsers.parseDurationValue(range.getLower()),
                range.getUpper() == null ? null : DateTimeParsers.parseDurationValue(range.getUpper()),
                range.isLowerIncluded() == null ? true : range.isLowerIncluded(),
                range.isUpperIncluded() == null ? true : range.isUpperIncluded());
    }

    public static com.nedap.archie.base.Interval<Temporal> convertInterval(IntervalOfDate range) {
        if(range == null) {
            return null;
        }
        return new com.nedap.archie.base.Interval<>(
                range.getLower() == null ? null : DateTimeParsers.parseDateValue(range.getLower()),
                range.getUpper() == null ? null : DateTimeParsers.parseDateValue(range.getUpper()),
                range.isLowerIncluded() == null ? true : range.isLowerIncluded(),
                range.isUpperIncluded() == null ? true : range.isUpperIncluded());
    }

    public static com.nedap.archie.base.Interval<TemporalAccessor> convertInterval(IntervalOfDateTime range) {
        if(range == null) {
            return null;
        }
        return new com.nedap.archie.base.Interval<>(
                range.getLower() == null ? null : DateTimeParsers.parseDateTimeValue(range.getLower()),
                range.getUpper() == null ? null : DateTimeParsers.parseDateTimeValue(range.getUpper()),
                range.isLowerIncluded() == null ? true : range.isLowerIncluded(),
                range.isUpperIncluded() == null ? true : range.isUpperIncluded());
    }

    public static com.nedap.archie.base.Interval<TemporalAccessor> convertInterval(IntervalOfTime range) {
        if(range == null) {
            return null;
        }
        return new com.nedap.archie.base.Interval<>(
                range.getLower() == null ? null : DateTimeParsers.parseTimeValue(range.getLower()),
                range.getUpper() == null ? null : DateTimeParsers.parseTimeValue(range.getUpper()),
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

    public static CodePhrase convertToCodePhrase(CODEPHRASE codePhrase) {
        if(codePhrase == null) {
            return null;
        }
        //cannot use the codephrase to terminology code conversion
        return new CodePhrase(codePhrase.getTerminologyId() != null ? new TerminologyId(codePhrase.getTerminologyId().getValue()) : new TerminologyId("local"),
                codePhrase.getCodeString());
    }


}
