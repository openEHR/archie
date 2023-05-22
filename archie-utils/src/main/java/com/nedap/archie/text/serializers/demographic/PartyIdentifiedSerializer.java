package com.nedap.archie.text.serializers.demographic;

import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class PartyIdentifiedSerializer implements RmSerializer<PartyIdentified> {
    @Override
    public void serialize(PartyIdentified data, RmToTextSerializer serializer) {
        if(data.getName() != null) {
            serializer.append("naam: ");
            serializer.append(data.getName());
            serializer.append("\n");
        }
        if(data.getIdentifiers() != null) {
            for (DvIdentifier identifier : data.getIdentifiers()) {
                serializer.append(identifier);
                serializer.append("\n");
            }
        }
    }

    @Override
    public Class getSerializedClass() {
        return PartyIdentified.class;
    }
}
