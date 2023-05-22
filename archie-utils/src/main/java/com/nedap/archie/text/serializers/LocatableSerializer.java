package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class LocatableSerializer implements RmSerializer<Locatable> {
    @Override
    public void serialize(Locatable data, RmToTextSerializer serializer) {
        serializer.append(data.getName());
        serializer.append("\n");
        if(data.getFeederAudit() != null) {
            serializer.append(data.getFeederAudit());
        }
    }

    @Override
    public Class getSerializedClass() {
        return Locatable.class;
    }
}
