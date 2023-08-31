package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class ParticipationSerializer implements RmSerializer<Participation> {
    @Override
    public void serialize(Participation data, RmToMarkdownSerializer serializer) {
        serializer.appendIfNotNull(I18n.t("Function of participant"), data.getFunction());
        serializer.appendIfNotNull(I18n.t("Mode of participation"), data.getMode());
        serializer.appendIfNotNull(I18n.t("Time"), data.getTime());
        serializer.appendIfNotNull(I18n.t("Performer"), data.getPerformer());
    }

    @Override
    public Class getSerializedClass() {
        return Participation.class;
    }
}
