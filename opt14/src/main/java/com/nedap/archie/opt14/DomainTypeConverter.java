package com.nedap.archie.opt14;

import com.nedap.archie.adl14.aom14.CDVOrdinal;
import com.nedap.archie.adl14.aom14.CDVOrdinalItem;
import com.nedap.archie.adl14.aom14.CDVQuantity;

import com.nedap.archie.adl14.aom14.CDVQuantityAssumedValue;
import com.nedap.archie.adl14.aom14.CDVQuantityItem;
import com.nedap.archie.adl14.treewalkers.Adl14CComplexObjectParser;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.terminology.TerminologyCode;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.nedap.archie.opt14.BaseTypesConverter.convert;

public class DomainTypeConverter {
    public static CObject convertDomainType(CDOMAINTYPE cobject14) {
        if(cobject14 instanceof CDVORDINAL) {
            return convertCDVOrdinal((CDVORDINAL) cobject14);
        } else if (cobject14 instanceof CDVQUANTITY) {
            return convertCDVQuantity((CDVQUANTITY) cobject14);
        } else if (cobject14 instanceof CDVSTATE) {

        } else if (cobject14 instanceof CCODEPHRASE) {

        }
        return null;
    }

    private static CObject convertCDVOrdinal(CDVORDINAL ordinal14) {
        CDVOrdinal ordinal = new CDVOrdinal();
        Map<String, CDVOrdinalItem> items = new LinkedHashMap<>();
        if(ordinal14.getList() != null) {
            Integer i = 0;
            for(DVORDINAL dvordinal14:ordinal14.getList()) {
                CDVOrdinalItem item = new CDVOrdinalItem();
                item.setValue(dvordinal14.getValue());
                if(dvordinal14.getSymbol() != null) {
                    item.setSymbol(convert(dvordinal14.getSymbol().getDefiningCode()));
                }
                items.put(i.toString(), item);
                i++;
            }
        }
        //TODO: no assumed value in our own model, but there is in the OPT form.

        return Adl14CComplexObjectParser.convertCDVOrdinal(ordinal);
    }

    private static CObject convertCDVQuantity(CDVQUANTITY cobject14) {
        CDVQUANTITY quantity14 = cobject14;
        CDVQuantity quantity = new CDVQuantity();
        Map<String, CDVQuantityItem> items = new LinkedHashMap<>();

        if(quantity14.getList() != null) {
            Integer i = 0;
            for(CQUANTITYITEM item14:quantity14.getList()) {
                CDVQuantityItem item = new CDVQuantityItem();
                item.setMagnitude(BaseTypesConverter.convertInterval(item14.getMagnitude()));
                item.setPrecision(BaseTypesConverter.convertInterval(item14.getPrecision()));
                item.setUnits(item14.getUnits());
                items.put(i.toString(), item);
                i++;
            }
        }
        quantity.setList(items);

        quantity.setProperty(convert(quantity14.getProperty()));

        DVQUANTITY assumedValue14 = quantity14.getAssumedValue();
        if(assumedValue14 != null) {
            CDVQuantityAssumedValue assumedValue = new CDVQuantityAssumedValue();
            assumedValue.setMagnitude(assumedValue14.getMagnitude());
            assumedValue.setPrecision(assumedValue14.getPrecision() == null ? null : assumedValue14.getPrecision().longValue());
            assumedValue.setUnits(assumedValue14.getUnits());
            quantity.setAssumedValue(assumedValue);
        }
        return Adl14CComplexObjectParser.convertCDvQuantity(quantity);
    }
}
