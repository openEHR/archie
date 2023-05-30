package com.nedap.archie.text.serializers.action;

import com.nedap.archie.rm.composition.IsmTransition;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class IsmTransitionSerializer implements RmSerializer<IsmTransition> {
    @Override
    public void serialize(IsmTransition data, RmToMarkdownSerializer serializer) {
        serializer.appendIfNotNull(I18n.t("State transition"), data.getTransition());
        serializer.appendIfNotNull(I18n.t("Care flow step"), data.getCareflowStep());
        serializer.appendIfNotNull(I18n.t("Current state"), data.getCurrentState());
        if(data.getReason() != null) {
            for(DvText reason: data.getReason()) {
                serializer.appendIfNotNull(I18n.t("State transition reason"), data.getCurrentState());
            }
        }
    }

    @Override
    public Class getSerializedClass() {
        return IsmTransition.class;
    }
}
