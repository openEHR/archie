package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.datetime.DateTimeFormatters;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDuration;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDate;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvDurationSerializer implements RmSerializer<DvDuration> {
    @Override
    public void serialize(DvDuration data, RmToTextSerializer serializer) {
        if(data.getValue() == null) {
            serializer.append("data niet gevuld");
            return;
        }
        serializer.append(DateTimeSerializerFormatters.serializeDuration(data.getValue()));
    }

    @Override
    public Class getSerializedClass() {
        return DvDuration.class;
    }
}
