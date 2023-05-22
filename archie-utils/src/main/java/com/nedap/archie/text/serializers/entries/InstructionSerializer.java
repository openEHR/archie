package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Activity;
import com.nedap.archie.rm.composition.Instruction;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class InstructionSerializer implements RmSerializer<Instruction> {
    @Override
    public void serialize(Instruction data, RmToTextSerializer serializer) {
        serializer.writeToText(data.getName());
        serializer.append("\n");
        if(data.getActivities() != null) {
            for(Activity activity:data.getActivities()) {
                serializer.writeToText(activity);
                serializer.append("\n");
            }
        }
        if(data.getProtocol() != null) {
            serializer.writeToText(data.getProtocol());
            serializer.append("\n");
        }
        if(data.getExpiryTime() != null) {
            serializer.append("verloopdatum: ");
            serializer.writeToText(data.getExpiryTime());
            serializer.append("\n");
        }
        //TODO: wf definition - but we do not know the format here!
    }

    @Override
    public Class getSerializedClass() {
        return Instruction.class;
    }
}
