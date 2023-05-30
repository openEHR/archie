package com.nedap.archie.text.serializers.audit;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import org.openehr.utils.message.I18n;

public class FeederAuditSerializer implements RmSerializer<FeederAudit> {
    @Override
    public void serialize(FeederAudit data, RmToMarkdownSerializer serializer) {
        serializer.append("##### ");
        serializer.appendIfNotNull(
                I18n.t("Originating system audit"),
                data.getOriginatingSystemAudit());

        serializer.appendIfNotNull(
                I18n.t("Feeder system audit"),
                data.getFeederSystemAudit());
    }

    @Override
    public Class getSerializedClass() {
        return FeederAudit.class;
    }
}
