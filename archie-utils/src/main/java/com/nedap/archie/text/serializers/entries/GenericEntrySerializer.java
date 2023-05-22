package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.integration.GenericEntry;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import com.nedap.archie.text.serializers.LocatableSerializer;

public class GenericEntrySerializer implements RmSerializer<GenericEntry> {
    @Override
    public void serialize(GenericEntry data, RmToTextSerializer serializer) {
        serializer.append("### ");
        new LocatableSerializer().serialize(data, serializer);
        serializer.append(data.getData());
        serializer.append("\n");
    }

    @Override
    public Class getSerializedClass() {
        return GenericEntry.class;
    }
}
