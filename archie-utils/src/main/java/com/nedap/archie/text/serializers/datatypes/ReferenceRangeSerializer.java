package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import org.openehr.utils.message.I18n;

public class ReferenceRangeSerializer implements RmSerializer<ReferenceRange> {
    @Override
    public void serialize(ReferenceRange data, RmToTextSerializer serializer) {
        serializer.append(data.getRange());
        if(data.getMeaning() != null) {
            serializer.append("\n");
            serializer.appendIfNotNull(I18n.t("Meaning"), data.getMeaning());
        }

    }

    @Override
    public Class getSerializedClass() {
        return ReferenceRange.class;
    }
}
