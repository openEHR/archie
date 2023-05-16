package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvQuantitySerializer implements RmSerializer<DvQuantity> {
    @Override
    public void serialize(DvQuantity data, RmToTextSerializer serializer) {
        if(data.getMagnitude() == null) {
            serializer.append("data niet gevuld");
        } else {
            serializer.append(Double.toString(data.getMagnitude()));
        }
        serializer.append(" ");
        serializer.append(data.getUnits());
        //TODO: all the other fields, reference ranges, normal ranges, status, etc.
    }

    @Override
    public Class getSerializedClass() {
        return DvQuantity.class;
    }
}