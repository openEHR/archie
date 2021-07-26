package com.nedap.archie.opt14;

import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.DvURI;
import com.nedap.archie.rm.datavalues.TermMapping;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;
import com.nedap.archie.rm.support.identification.TerminologyId;

import com.nedap.archie.opt14.schema.*;

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

    public static DvCodedText convert(DVCODEDTEXT symbol) {
        if(symbol == null) {
            return null;
        }
        DvCodedText codedText = new DvCodedText();
        if(symbol.getDefiningCode() != null) {
            CodePhrase codePhrase = convertToCodePhrase(symbol.getDefiningCode());
            codedText.setDefiningCode(codePhrase);
        }
        codedText.setEncoding(convertToCodePhrase(symbol.getEncoding()));
        codedText.setFormatting(symbol.getFormatting());
        codedText.setHyperlink(convert(symbol.getHyperlink()));
        codedText.setLanguage(convertToCodePhrase(symbol.getLanguage()));
        codedText.setMappings(convert(symbol.getMappings()));
        codedText.setValue(symbol.getValue());

        return codedText;
    }

    public static DvText convert(DVTEXT text) {
        if(text == null) {
            return null;
        }
        DvText convertedText = new DvText();
        convertedText.setEncoding(convertToCodePhrase(text.getEncoding()));
        convertedText.setFormatting(text.getFormatting());
        convertedText.setHyperlink(convert(text.getHyperlink()));
        convertedText.setLanguage(convertToCodePhrase(text.getLanguage()));
        convertedText.setMappings(convert(text.getMappings()));
        convertedText.setValue(text.getValue());
        return convertedText;
    }

    public static List<TermMapping> convert(List<TERMMAPPING> mappings14) {
        if(mappings14 == null) {
            return null;
        }
        List<TermMapping> mappings = new ArrayList<>();
        for(TERMMAPPING mapping14:mappings14) {
            TermMapping mapping = new TermMapping(convertToCodePhrase(mapping14.getTarget()),
                    mapping14.getMatch() == null || mapping14.getMatch().isEmpty() ? null : mapping14.getMatch().charAt(0),
                    convert(mapping14.getPurpose()));
            mappings.add(mapping);
        }
        return mappings;
    }

    public static DvURI convert(DVURI hyperlink) {
        return hyperlink == null ? null : new DvURI(hyperlink.getValue());
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
