package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import org.openehr.utils.message.I18n;
import org.reflections.serializers.Serializer;

public class ParticipationSerializer implements RmSerializer<Participation> {
    @Override
    public void serialize(Participation data, RmToTextSerializer serializer) {
        serializer.appendIfNotNull(I18n.t("Function of participant"), data.getFunction());
        serializer.appendIfNotNull(I18n.t("Mode of participant"), data.getMode());
        serializer.appendIfNotNull(I18n.t("Time"), data.getTime());
        serializer.appendIfNotNull(I18n.t("Performer"), data.getPerformer());
    }

    @Override
    public Class getSerializedClass() {
        return Participation.class;
    }
}
