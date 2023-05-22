package com.nedap.archie.text.serializers.audit;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class FeederAuditSerializer implements RmSerializer<FeederAudit> {
    @Override
    public void serialize(FeederAudit data, RmToTextSerializer serializer) {
        if(data.getOriginatingSystemAudit() != null) {
            serializer.append("##### audit uit bronsysteem");
            serializer.append("\n");
            serializer.append(data.getOriginatingSystemAudit());
        }
        if(data.getFeederSystemAudit() != null) {
            serializer.append("#### audit uit tussensysteem");
            serializer.append("\n");
            serializer.append(data.getFeederSystemAudit());
        }
    }

    @Override
    public Class getSerializedClass() {
        return FeederAudit.class;
    }
}
