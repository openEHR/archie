package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvBoolean;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvBooleanSerializer implements RmSerializer<DvBoolean> {
    @Override
    public void serialize(DvBoolean data, RmToTextSerializer serializer) {
        if(data.getValue() == null) {
            serializer.append("data niet gevuld");
            return;
        }
        serializer.append(data.getValue() ? "Ja" : "Nee");
    }

    @Override
    public Class getSerializedClass() {
        return DvBoolean.class;
    }
}