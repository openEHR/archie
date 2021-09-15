package com.nedap.archie.opt14;

import com.nedap.archie.adl14.aom14.CDVOrdinalItem;
import com.nedap.archie.opt14.schema.DVBOOLEAN;
import com.nedap.archie.opt14.schema.DVCODEDTEXT;
import com.nedap.archie.opt14.schema.DVCOUNT;
import com.nedap.archie.opt14.schema.DVIDENTIFIER;
import com.nedap.archie.opt14.schema.DVORDINAL;
import com.nedap.archie.opt14.schema.DVQUANTITY;
import com.nedap.archie.opt14.schema.DVTEXT;
import com.nedap.archie.opt14.schema.DVURI;
import com.nedap.archie.opt14.schema.TERMMAPPING;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DataValue;
import com.nedap.archie.rm.datavalues.DvBoolean;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.DvURI;
import com.nedap.archie.rm.datavalues.TermMapping;
import com.nedap.archie.rm.datavalues.quantity.DvCount;
import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;

import java.util.ArrayList;
import java.util.List;

import static com.nedap.archie.opt14.BaseTypesConverter.convert;

public class DataValuesConverter {

    public static DvCodedText convert(DVCODEDTEXT symbol) {
        if(symbol == null) {
            return null;
        }
        DvCodedText codedText = new DvCodedText();
        if(symbol.getDefiningCode() != null) {
            CodePhrase codePhrase = BaseTypesConverter.convertToCodePhrase(symbol.getDefiningCode());
            codedText.setDefiningCode(codePhrase);
        }
        codedText.setEncoding(BaseTypesConverter.convertToCodePhrase(symbol.getEncoding()));
        codedText.setFormatting(symbol.getFormatting());
        codedText.setHyperlink(convert(symbol.getHyperlink()));
        codedText.setLanguage(BaseTypesConverter.convertToCodePhrase(symbol.getLanguage()));
        codedText.setMappings(convert(symbol.getMappings()));
        codedText.setValue(symbol.getValue());

        return codedText;
    }

    public static DvText convert(DVTEXT text) {
        if(text == null) {
            return null;
        }
        DvText convertedText = new DvText();
        convertedText.setEncoding(BaseTypesConverter.convertToCodePhrase(text.getEncoding()));
        convertedText.setFormatting(text.getFormatting());
        convertedText.setHyperlink(convert(text.getHyperlink()));
        convertedText.setLanguage(BaseTypesConverter.convertToCodePhrase(text.getLanguage()));
        convertedText.setMappings(convert(text.getMappings()));
        convertedText.setValue(text.getValue());
        return convertedText;
    }


    private static List<TermMapping> convert(List<TERMMAPPING> mappings14) {
        if(mappings14 == null) {
            return null;
        }
        List<TermMapping> mappings = new ArrayList<>();
        for(TERMMAPPING mapping14:mappings14) {
            TermMapping mapping = new TermMapping(BaseTypesConverter.convertToCodePhrase(mapping14.getTarget()),
                    mapping14.getMatch() == null || mapping14.getMatch().isEmpty() ? null : mapping14.getMatch().charAt(0),
                    convert(mapping14.getPurpose()));
            mappings.add(mapping);
        }
        return mappings;
    }

    public static DvURI convert(DVURI hyperlink) {
        return hyperlink == null ? null : new DvURI(hyperlink.getValue());
    }

    public static DvIdentifier convert(DVIDENTIFIER dvidentifier) {
        if(dvidentifier == null) {
            return null;
        }
        DvIdentifier converted = new DvIdentifier();
        converted.setAssigner(dvidentifier.getAssigner());
        converted.setId(dvidentifier.getId());
        converted.setIssuer(dvidentifier.getIssuer());
        converted.setType(dvidentifier.getType());
        return converted;
    }

    public static DvOrdinal convert(DVORDINAL dvordinal) {
        if(dvordinal == null) {
            return null;
        }

        DvOrdinal converted = new DvOrdinal();
        converted.setValue((long) dvordinal.getValue());
        converted.setSymbol(convert(dvordinal.getSymbol()));

        //TODO: dvordinal.getNormalRange();
        converted.setNormalStatus(BaseTypesConverter.convertToCodePhrase(dvordinal.getNormalStatus()));
        //TODO: dvordinal.getOtherReferenceRanges();
        return converted;

   }

    public static DvQuantity convert(DVQUANTITY dvquantity) {
        if(dvquantity == null) {
            return null;
        }

        DvQuantity converted = new DvQuantity();
        converted.setMagnitude((double) dvquantity.getMagnitude());
        converted.setUnits(dvquantity.getUnits());
        converted.setPrecision(dvquantity.getPrecision() == null ? null : dvquantity.getPrecision().longValue());
        converted.setNormalStatus(BaseTypesConverter.convertToCodePhrase(dvquantity.getNormalStatus()));
        //TODO: normal range, other reference ranges

        return converted;

    }

    public static DvBoolean convert(DVBOOLEAN dvboolean) {
        if(dvboolean == null) {
            return null;
        }
        DvBoolean converted = new DvBoolean();
        converted.setValue(dvboolean.isValue());
        return converted;
    }

    public static DvCount convert(DVCOUNT dvcount) {
        if(dvcount == null) {
            return null;
        }
        DvCount converted = new DvCount();
        converted.setMagnitude(dvcount.getMagnitude());
        converted.setAccuracy(dvcount.getAccuracy() == null ? null : dvcount.getAccuracy().doubleValue());
        converted.setAccuracyIsPercent(dvcount.isAccuracyIsPercent());
        converted.setMagnitudeStatus(dvcount.getMagnitudeStatus());
        converted.setNormalStatus(BaseTypesConverter.convertToCodePhrase(dvcount.getNormalStatus()));
        //TODO: normal range, other reference ranges
        return converted;
    }
}
