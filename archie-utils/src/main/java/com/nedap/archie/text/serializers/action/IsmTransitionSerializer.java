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
            serializer.writeToText(data.getTransition());
            serializer.append("\n");
        }
        if(data.getCareflowStep() != null) {
            serializer.append("stap: ");
            serializer.writeToText(data.getCareflowStep());
        }
        if(data.getCurrentState() != null) {
            serializer.append("huidige toestand: ");
            serializer.writeToText(data.getCurrentState());
        }
        if(data.getReason() != null) {
            for(DvText reason: data.getReason()) {
                serializer.append("reden: ");
                serializer.writeToText(reason);
            }
        }
    }

    @Override
    public Class getSerializedClass() {
        return IsmTransition.class;
    }
}
