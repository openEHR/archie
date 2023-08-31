package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.composition.EventContext;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class EventContextSerializer implements RmSerializer<EventContext> {
    @Override
    public void serialize(EventContext data, RmToMarkdownSerializer serializer) {

        serializer.appendIfNotNull(I18n.t("Location"), data.getLocation());
        serializer.appendIfNotNull(I18n.t("Start time"), data.getStartTime());
        serializer.appendIfNotNull(I18n.t("End time"), data.getEndTime());
        serializer.appendIfNotNull(I18n.t("Setting"), data.getSetting());

        if(data.getParticipations() != null && !data.getParticipations().isEmpty()) {

            serializer.append("##### ");
            serializer.append(I18n.t("participations"));
            serializer.appendNewLine();
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
