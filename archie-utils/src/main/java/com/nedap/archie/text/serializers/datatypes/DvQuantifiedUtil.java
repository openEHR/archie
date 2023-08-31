package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.quantity.DvQuantified;
import com.nedap.archie.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class DvQuantifiedUtil {

    public static void serialize(DvQuantified<?, ?, ?> data, RmToMarkdownSerializer serializer) {
        serializer.appendIfNotNull(I18n.t("Magnitude status"), data.getMagnitudeStatus());
        serializer.appendIfNotNull(I18n.t("Normal range"), data.getNormalRange());
        serializer.appendIfNotNull(I18n.t("Normal status"), data.getNormalStatus());
        if(data.getOtherReferenceRanges() != null) {
            for(ReferenceRange<?> refRange:data.getOtherReferenceRanges()) {
                serializer.appendIfNotNull(I18n.t("Other Reference ranges"), refRange);
            }
        }
    }
}

