package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;

public class DvCodedTextSerializer implements RmSerializer<DvCodedText> {
    @Override
    public void serialize(DvCodedText data, RmToMarkdownSerializer serializer) {
        serializer.append(data.getValue());
    }

    @Override
    public Class getSerializedClass() {
        return DvCodedText.class;
    }
}