package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Action;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import com.nedap.archie.text.serializers.LocatableUtil;
import org.openehr.utils.message.I18n;

public class ActionSerializer implements RmSerializer<Action> {
    @Override
    public void serialize(Action data, RmToMarkdownSerializer serializer) {
        serializer.append("### ");
        LocatableUtil.serialize(data, serializer);
        serializer.appendIfNotNull(I18n.t("Description"), data.getDescription());
        serializer.appendIfNotNull(I18n.t("Protocol"), data.getProtocol());
        serializer.appendIfNotNull(I18n.t("Instruction details"), data.getInstructionDetails());
        serializer.appendWithHeaderIfNotNull("##### ", I18n.t("State transition"), data.getIsmTransition());
    }

    @Override
    public Class getSerializedClass() {
        return Action.class;
    }
}
