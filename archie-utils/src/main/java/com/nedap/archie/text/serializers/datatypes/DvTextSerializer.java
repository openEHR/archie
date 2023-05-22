package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvTextSerializer implements RmSerializer<DvText> {
    @Override
    public void serialize(DvText data, RmToTextSerializer serializer) {
        serializer.append(data.getValue());
    }

    @Override
    public Class getSerializedClass() {
        return DvText.class;
    }
}
