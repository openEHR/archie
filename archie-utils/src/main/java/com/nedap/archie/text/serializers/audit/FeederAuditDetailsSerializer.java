package com.nedap.archie.text.serializers.audit;

import com.nedap.archie.rm.archetyped.FeederAuditDetails;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class FeederAuditDetailsSerializer implements RmSerializer<FeederAuditDetails> {
    @Override
    public void serialize(FeederAuditDetails data, RmToMarkdownSerializer serializer) {
        serializer.appendIfNotNull(I18n.t("Originating system"), data.getSystemId());
        serializer.appendIfNotNull(I18n.t("Location"), data.getLocation());

        if(data.getTime() != null) {
            serializer.append(data.getTime());
            serializer.appendNewLine();
        }
        serializer.appendIfNotNull(I18n.t("Audit provider"), data.getProvider());
        if(data.getOtherDetails() != null) {
            serializer.append(data.getOtherDetails());
            serializer.appendNewLine();
        }
    }

    @Override
    public Class getSerializedClass() {
        return FeederAuditDetails.class;
    }
}
