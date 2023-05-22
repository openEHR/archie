package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.datetime.DateTimeFormatters;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvDateTimeSerializer implements RmSerializer<DvDateTime> {
    @Override
    public void serialize(DvDateTime data, RmToTextSerializer serializer) {
        if(data.getValue() == null) {
            serializer.append("data niet gevuld");
            return;
        }
        serializer.append(DateTimeSerializerFormatters.ISO_8601_DATE_TIME.format(data.getValue()));
        DvQuantifiedUtil.serialize(data, serializer);
    }

    @Override
    public Class getSerializedClass() {
        return DvDateTime.class;
    }
}
