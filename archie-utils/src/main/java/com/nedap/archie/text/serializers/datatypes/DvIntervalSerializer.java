package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;

public class DvIntervalSerializer implements RmSerializer<DvInterval> {
    @Override
    public void serialize(DvInterval data, RmToMarkdownSerializer serializer) {
        if(data.getInterval() != null) {
            serializer.append(data.getLower());
            serializer.append(" - ");
            serializer.append(data.getUpper());

        }
    }

    @Override
    public Class getSerializedClass() {
        return DvInterval.class;
    }
}
