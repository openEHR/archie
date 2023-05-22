package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.composition.EventContext;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class EventContextSerializer implements RmSerializer<EventContext> {
    @Override
    public void serialize(EventContext data, RmToTextSerializer serializer) {

        if(data.getLocation() != null) {
            serializer.append("locatie: ");
            serializer.append(data.getLocation());
            serializer.append("\n");
        }
        if(data.getStartTime() != null) {
            serializer.append("starttijd: ");
            serializer.append(data.getStartTime());
            serializer.append("\n");
        }
        if(data.getEndTime() != null) {
            serializer.append("eindtijd: ");
            serializer.append(data.getEndTime());
            serializer.append("\n");
        }
        if(data.getSetting() != null) {
            serializer.append("setting: ");
            serializer.append(data.getSetting());
            serializer.append("\n");
        }
        if(data.getParticipations() != null && !data.getParticipations().isEmpty()) {
            serializer.append("##### participations");
            for(Participation participation:data.getParticipations()) {
                serializer.append(participation);
                serializer.append("\n");
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
