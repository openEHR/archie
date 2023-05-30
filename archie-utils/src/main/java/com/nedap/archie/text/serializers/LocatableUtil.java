package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.text.RmToMarkdownSerializer;

public class LocatableUtil {

    public static void serialize(Locatable data, RmToMarkdownSerializer serializer) {
        serializer.append(data.getName());
        serializer.appendNewLine();
        if(data.getFeederAudit() != null) {
            serializer.append(data.getFeederAudit());
        }
    }
}
