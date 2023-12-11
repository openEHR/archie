package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvBoolean;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class DvBooleanSerializer implements RmSerializer<DvBoolean> {
    @Override
    public void serialize(DvBoolean data, RmToMarkdownSerializer serializer) {
        if(data.getValue() == null) {
            serializer.append(I18n.t("no value"));
            return;
        }
        serializer.append(data.getValue() ? I18n.t("Yes") :I18n.t("No"));
    }

    @Override
    public Class getSerializedClass() {
        return DvBoolean.class;
    }
}