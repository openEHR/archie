package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DvDateTimeSerializer implements RmSerializer<DvDateTime> {
    @Override
    public void serialize(DvDateTime data, RmToMarkdownSerializer serializer) {
        if(data.getValue() == null) {
            serializer.append(I18n.t("no value"));
            return;
        }

        serializer.append(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(I18n.getCurrentLocale())
                .format(data.getValue()));
        DvQuantifiedUtil.serialize(data, serializer);
    }

    @Override
    public Class getSerializedClass() {
        return DvDateTime.class;
    }
}
