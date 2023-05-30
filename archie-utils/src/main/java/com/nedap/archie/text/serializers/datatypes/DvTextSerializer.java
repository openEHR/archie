package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;

public class DvTextSerializer implements RmSerializer<DvText> {
    @Override
    public void serialize(DvText data, RmToMarkdownSerializer serializer) {
        serializer.append(data.getValue());
    }

    @Override
    public Class getSerializedClass() {
        return DvText.class;
    }
}
