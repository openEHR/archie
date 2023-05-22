package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.AdminEntry;
import com.nedap.archie.rm.composition.Evaluation;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.integration.GenericEntry;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import sun.net.www.content.text.Generic;

public class AdminEntrySerializer implements RmSerializer<AdminEntry> {
    @Override
    public void serialize(AdminEntry data, RmToTextSerializer serializer) {
        serializer.append("### ");
        serializer.writeToText(data.getName());
        serializer.append("\n");
        serializer.writeToText(data.getData());
        serializer.append("\n");
    }

    @Override
    public Class getSerializedClass() {
        return AdminEntry.class;
    }
}
