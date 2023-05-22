package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.composition.EventContext;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class EventContextSerializer implements RmSerializer<EventContext> {
    @Override
    public void serialize(EventContext data, RmToTextSerializer serializer) {

        serializer.appendIfNotNull("Location", data.getLocation());
        serializer.appendIfNotNull("Start time", data.getStartTime());
        serializer.appendIfNotNull("End time", data.getEndTime());
        serializer.appendIfNotNull("Setting", data.getSetting());

        if(data.getParticipations() != null && !data.getParticipations().isEmpty()) {
            serializer.append("##### participations");
            for(Participation participation:data.getParticipations()) {
                serializer.append(participation);
                serializer.appendNewLine();
            }
        }
        if(data.getOtherContext() != null) {
            serializer.append(data.getOtherContext());
        }
    }

    @Override
    public Class getSerializedClass() {
        return EventContext.class;
    }
}
