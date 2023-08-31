package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvTime;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DvTimeSerializer implements RmSerializer<DvTime> {
    @Override
    public void serialize(DvTime data, RmToMarkdownSerializer serializer) {
        if(data.getValue() == null) {
            serializer.append("data niet gevuld");
            return;
        }
        serializer.append(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(I18n.getCurrentLocale())
                .format(data.getValue()));
        DvQuantifiedUtil.serialize(data, serializer);
    }

    @Override
    public Class getSerializedClass() {
        return DvTime.class;
    }
}
