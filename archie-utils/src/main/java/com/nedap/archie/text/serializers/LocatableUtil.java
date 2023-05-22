package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class LocatableUtil {

    public static void serialize(Locatable data, RmToTextSerializer serializer) {
        serializer.append(data.getName());
        serializer.append("\n");
        if(data.getFeederAudit() != null) {
            serializer.append(data.getFeederAudit());
        }
    }
}
