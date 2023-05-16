package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.DvOrdered;
import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvOrdinalSerializer implements RmSerializer<DvOrdinal> {
    @Override
    public void serialize(DvOrdinal data, RmToTextSerializer serializer) {
        if(data.getValue() != null) {
            serializer.append(Long.toString(data.getValue()));
            serializer.append(" - ");
        }
        serializer.writeToText(data.getSymbol());
    }

    @Override
    public Class getSerializedClass() {
        return DvOrdinal.class;
    }
}