package com.nedap.archie.text.serializers.action;

import com.nedap.archie.rm.composition.Activity;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import com.nedap.archie.text.serializers.LocatableUtil;
import org.openehr.utils.message.I18n;

public class ActivitySerializer implements RmSerializer<Activity> {
    @Override
    public void serialize(Activity data, RmToMarkdownSerializer serializer) {
        serializer.append("##### ");
        LocatableUtil.serialize(data, serializer);
        serializer.appendIfNotNull(I18n.t("Description"), data.getDescription());
        serializer.appendIfNotNull(I18n.t("Archetype for action"), data.getActionArchetypeId());
    }

    @Override
    public Class getSerializedClass() {
        return Activity.class;
    }
}
