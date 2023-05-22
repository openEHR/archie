package com.nedap.archie.text.serializers.action;

import com.nedap.archie.rm.composition.IsmTransition;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class IsmTransitionSerializer implements RmSerializer<IsmTransition> {
    @Override
    public void serialize(IsmTransition data, RmToTextSerializer serializer) {
        if(data.getTransition() != null) {
            serializer.append("transitie: ");
            serializer.append(data.getTransition());
            serializer.append("\n");
        }
        if(data.getCareflowStep() != null) {
            serializer.append("stap: ");
            serializer.append(data.getCareflowStep());
        }
        if(data.getCurrentState() != null) {
            serializer.append("huidige toestand: ");
            serializer.append(data.getCurrentState());
        }
        if(data.getReason() != null) {
            for(DvText reason: data.getReason()) {
                serializer.append("reden: ");
                serializer.append(reason);
            }
        }
    }

    @Override
    public Class getSerializedClass() {
        return IsmTransition.class;
    }
}
