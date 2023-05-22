package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import org.reflections.serializers.Serializer;

public class ParticipationSerializer implements RmSerializer<Participation> {
    @Override
    public void serialize(Participation data, RmToTextSerializer serializer) {
        if(data.getFunction() != null) {
            serializer.append("functie: ");
            serializer.append(data.getFunction());
            serializer.append("\n");
        }
        if(data.getMode() != null) {
            serializer.append("mode: ");
            serializer.append(data.getFunction());
            serializer.append("\n");
        }
        if(data.getTime() != null) {
            serializer.append("tijd: ");
            serializer.append(data.getTime());
            serializer.append("\n");
        }
        if(data.getPerformer() != null) {
            serializer.append("uitvoerende: ");
            serializer.append(data.getPerformer());
            serializer.append("\n");
        }

    }

    @Override
    public Class getSerializedClass() {
        return Participation.class;
    }
}
