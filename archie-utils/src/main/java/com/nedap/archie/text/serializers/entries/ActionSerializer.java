package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Action;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class ActionSerializer implements RmSerializer<Action> {
    @Override
    public void serialize(Action data, RmToTextSerializer serializer) {
        serializer.writeToText(data.getName());
        serializer.append("\n");
        serializer.writeToText(data.getDescription());
        serializer.append("\n");
        serializer.writeToText(data.getProtocol());
        serializer.append("\n");
        serializer.writeToText(data.getInstructionDetails());
        serializer.append("\n");
        serializer.writeToText(data.getIsmTransition());
        serializer.append("\n");
    }

    @Override
    public Class getSerializedClass() {
        return Action.class;
    }
}
