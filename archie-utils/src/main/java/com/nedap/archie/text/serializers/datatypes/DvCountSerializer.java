package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.quantity.DvCount;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class DvCountSerializer implements RmSerializer<DvCount> {
    @Override
    public void serialize(DvCount data, RmToMarkdownSerializer serializer) {
        if(data.getMagnitude() == null) {
            serializer.append(I18n.t("no magnitude"));
            return;
        }
        serializer.append(Long.toString(data.getMagnitude()));
        serializer.appendNewLine();
        DvQuantifiedUtil.serialize(data, serializer);
        //TODO: all the other fields, reference ranges, normal ranges, status, etc.
    }

    @Override
    public Class getSerializedClass() {
        return DvCount.class;
    }
}