package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class DvQuantitySerializer implements RmSerializer<DvQuantity> {
    @Override
    public void serialize(DvQuantity data, RmToMarkdownSerializer serializer) {
        if(data.getMagnitude() == null) {
            serializer.append(I18n.t("No data entered"));
        } else {
            serializer.append(Double.toString(data.getMagnitude()));
        }
        serializer.append(data.getUnits());
        serializer.appendIfNotNull(I18n.t("Units system"), data.getUnitsSystem());
        //TODO: if this is present, replace units, since it will be more human readable?
        serializer.appendIfNotNull(I18n.t("Units display name"), data.getUnitsDisplayName());
        DvQuantifiedUtil.serialize(data, serializer);
    }

    @Override
    public Class getSerializedClass() {
        return DvQuantity.class;
    }
}