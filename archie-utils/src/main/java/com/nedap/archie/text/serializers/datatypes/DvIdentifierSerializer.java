package com.nedap.archie.text.serializers.datatypes;

import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class DvIdentifierSerializer implements RmSerializer<DvIdentifier> {

    @Override
    public void serialize(DvIdentifier data, RmToTextSerializer serializer) {
        if(data.getType() != null) {
            serializer.append("Type: ");
            serializer.append(data.getType());
            serializer.appendNewLine();
        }
        if(data.getId() != null) {
            serializer.append("Id: ");
            serializer.append(data.getId());
            serializer.appendNewLine();
        }
        if(data.getIssuer() != null) {
            serializer.append("Uitgever: ");
            serializer.append(data.getIssuer());
            serializer.appendNewLine();
        }
        if(data.getAssigner() != null) {
            serializer.append("Toekenner: ");
            serializer.append(data.getAssigner());
            serializer.appendNewLine();
        }
    }

    @Override
    public Class getSerializedClass() {
        return null;
    }
}
