package com.nedap.archie.text.serializers.audit;

import com.nedap.archie.rm.archetyped.FeederAuditDetails;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class FeederAuditDetailsSerializer implements RmSerializer<FeederAuditDetails> {
    @Override
    public void serialize(FeederAuditDetails data, RmToTextSerializer serializer) {
        if(data.getOtherDetails() != null) {
            serializer.append(data.getOtherDetails());
            serializer.append("\n");
        }
        if(data.getSystemId() != null) {
            serializer.append("bronsysteem: ");
            serializer.append(data.getSystemId());
            serializer.append("\n");
        }
        if(data.getLocation() != null) {
            serializer.append("locatie: " );
            serializer.append(data.getLocation());
            serializer.append("\n");
        }
        if(data.getTime() != null) {
            serializer.append(data.getTime());
            serializer.append("\n");
        }
        if(data.getProvider() != null) {
            serializer.append("zorgaanbieder: " );
            serializer.append(data.getProvider());
            serializer.append("\n");
        }
    }

    @Override
    public Class getSerializedClass() {
        return FeederAuditDetails.class;
    }
}
