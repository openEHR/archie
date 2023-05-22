package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.quantity.DvProportion;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.rm.datavalues.quantity.ProportionKind;
import com.nedap.archie.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import org.openehr.utils.message.I18n;

public class DvProportionSerializer implements RmSerializer<DvProportion> {
    @Override
    public void serialize(DvProportion data, RmToTextSerializer serializer) {
        if(data.getType() == null) {
            serializeFraction(data, serializer);
        } else if (data.getType() == ProportionKind.PERCENT.getPk()) {
            serializer.append(Double.toString(data.getNumerator()));
            serializer.append("%");
        } else {
            serializeFraction(data, serializer);
        }

        DvQuantifiedUtil.serialize(data, serializer);
        //TODO: other formats - maybe just a number sometimes?
    }

    private static void serializeFraction(DvProportion data, RmToTextSerializer serializer) {
        if(data.getNumerator() == null) {
            serializer.append("-");
        } else {
            serializer.append(Double.toString(data.getNumerator()));
        }
        if(data.getDenominator() == null) {
            serializer.append("-");
        } else {
            serializer.append(Double.toString(data.getDenominator()));
        }
    }

    @Override
    public Class getSerializedClass() {
        return DvProportion.class;
    }
}