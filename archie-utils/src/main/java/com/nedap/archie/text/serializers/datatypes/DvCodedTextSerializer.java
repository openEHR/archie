package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvCodedTextSerializer implements RmSerializer<DvCodedText> {
    @Override
    public void serialize(DvCodedText data, RmToTextSerializer serializer) {
        serializer.append(data.getValue());
    }

    @Override
    public Class getSerializedClass() {
        return DvCodedText.class;
    }
}