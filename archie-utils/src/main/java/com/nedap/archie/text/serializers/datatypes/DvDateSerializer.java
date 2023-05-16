package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.datetime.DateTimeFormatters;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDate;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvDateSerializer implements RmSerializer<DvDate> {
    @Override
    public void serialize(DvDate data, RmToTextSerializer serializer) {
        if(data.getValue() == null) {
            serializer.append("data niet gevuld");
            return;
        }
        serializer.append(DateTimeSerializerFormatters.ISO_8601_DATE.format(data.getValue()));
    }

    @Override
    public Class getSerializedClass() {
        return DvDate.class;
    }
}
