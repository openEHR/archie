package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.DvCount;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvCountSerializer implements RmSerializer<DvCount> {
    @Override
    public void serialize(DvCount data, RmToTextSerializer serializer) {
        if(data.getMagnitude() == null) {
            serializer.append("data niet gevuld");
            return;
        }
        serializer.append(Long.toString(data.getMagnitude()));
        serializer.appendNewLine();
        DvQuantifiedUtil.serialize(data, serializer);
        //TODO: all the other fields, reference ranges, normal ranges, status, etc.
    }

    @Override
    public Class getSerializedClass() {
        return DvCount.class;
    }
}