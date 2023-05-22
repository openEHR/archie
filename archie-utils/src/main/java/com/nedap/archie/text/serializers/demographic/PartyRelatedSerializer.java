package com.nedap.archie.text.serializers.demographic;

import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.generic.PartyRelated;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class PartyRelatedSerializer implements RmSerializer<PartyRelated> {
    @Override
    public void serialize(PartyRelated data, RmToTextSerializer serializer) {
        if(data.getRelationship() != null) {
            serializer.append("relatie: ");
            serializer.append(data.getRelationship());
            serializer.append("\n");
        }
        if(data.getName() != null) {
            serializer.append("naam: ");
            serializer.append(data.getName());
            serializer.append("\n");
        }
        if(data.getIdentifiers() != null) {
            for(DvIdentifier identifier: data.getIdentifiers()) {
                serializer.append(identifier);
            }
        }
    }

    @Override
    public Class getSerializedClass() {
        return PartyRelated.class;
    }
}
