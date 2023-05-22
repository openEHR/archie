package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.AdminEntry;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import com.nedap.archie.text.serializers.LocatableSerializer;

public class AdminEntrySerializer implements RmSerializer<AdminEntry> {
    @Override
    public void serialize(AdminEntry data, RmToTextSerializer serializer) {
        serializer.append("### ");
        new LocatableSerializer().serialize(data, serializer);
        serializer.append(data.getData());
        serializer.append("\n");
    }

    @Override
    public Class getSerializedClass() {
        return AdminEntry.class;
    }
}
