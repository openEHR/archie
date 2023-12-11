package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDuration;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DvDurationSerializer implements RmSerializer<DvDuration> {
    @Override
    public void serialize(DvDuration data, RmToMarkdownSerializer serializer) {
        if(data.getValue() == null) {
            serializer.append(I18n.t("no value"));
            return;
        }
        serializer.append(DateTimeSerializerFormatters.serializeDuration(data.getValue()));
        serializer.appendNewLine();
        DvQuantifiedUtil.serialize(data, serializer);
    }

    @Override
    public Class getSerializedClass() {
        return DvDuration.class;
    }
}
