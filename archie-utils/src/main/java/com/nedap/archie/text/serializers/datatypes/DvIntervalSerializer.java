package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvIntervalSerializer implements RmSerializer<DvInterval> {
    @Override
    public void serialize(DvInterval data, RmToTextSerializer serializer) {
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
