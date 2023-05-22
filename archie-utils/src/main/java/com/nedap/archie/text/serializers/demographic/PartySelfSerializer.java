package com.nedap.archie.text.serializers.demographic;

import com.nedap.archie.rm.generic.PartySelf;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import org.openehr.utils.message.I18n;

public class PartySelfSerializer implements RmSerializer<PartySelf> {
    @Override
    public void serialize(PartySelf data, RmToTextSerializer serializer) {
        serializer.append(I18n.t("The person themselves\n"));
    }

    @Override
    public Class getSerializedClass() {
        return PartySelf.class;
    }
}
