package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.datetime.DateTimeFormatters;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDate;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvTime;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvTimeSerializer implements RmSerializer<DvTime> {
    @Override
    public void serialize(DvTime data, RmToTextSerializer serializer) {
        if(data.getValue() == null) {
            serializer.append("data niet gevuld");
            return;
        }
        serializer.append(DateTimeSerializerFormatters.ISO_8601_TIME.format(data.getValue()));
    }

    @Override
    public Class getSerializedClass() {
        return DvTime.class;
    }
}
