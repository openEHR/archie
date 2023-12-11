package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;

public class DvOrdinalSerializer implements RmSerializer<DvOrdinal> {
    @Override
    public void serialize(DvOrdinal data, RmToMarkdownSerializer serializer) {
        if(data.getValue() != null) {
            serializer.append(Long.toString(data.getValue()));
            serializer.append(" - ");
        }
        serializer.append(data.getSymbol());
    }

    @Override
    public Class getSerializedClass() {
        return DvOrdinal.class;
    }
}