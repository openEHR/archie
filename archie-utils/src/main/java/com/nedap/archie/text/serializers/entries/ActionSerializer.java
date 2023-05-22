package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Action;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import com.nedap.archie.text.serializers.LocatableSerializer;

public class ActionSerializer implements RmSerializer<Action> {
    @Override
    public void serialize(Action data, RmToTextSerializer serializer) {
        serializer.append("### ");
        new LocatableSerializer().serialize(data, serializer);
        serializer.append(data.getDescription());
        serializer.append("\n");
        serializer.append(data.getProtocol());
        serializer.append("\n");
        serializer.append(data.getInstructionDetails());
        serializer.append("\n");
        serializer.append(data.getIsmTransition());
        serializer.append("\n");
    }

    @Override
    public Class getSerializedClass() {
        return Action.class;
    }
}
