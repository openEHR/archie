package com.nedap.archie.text.serializers.action;

import com.nedap.archie.rm.composition.Activity;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class ActivitySerializer implements RmSerializer<Activity> {
    @Override
    public void serialize(Activity data, RmToTextSerializer serializer) {
        serializer.append(data.getName());
        serializer.append("\n");
        if(data.getDescription() != null) {
            serializer.append(data.getDescription());
            serializer.append("\n");
        }
        if(data.getActionArchetypeId() != null) {
            serializer.append("archetype voor actie: ");
            serializer.append(data.getActionArchetypeId());
            serializer.append("\n");
        }
    }

    @Override
    public Class getSerializedClass() {
        return Activity.class;
    }
}
