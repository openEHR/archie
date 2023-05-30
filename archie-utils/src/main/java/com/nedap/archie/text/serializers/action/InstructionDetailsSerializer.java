package com.nedap.archie.text.serializers.action;

import com.nedap.archie.rm.composition.InstructionDetails;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;

public class InstructionDetailsSerializer implements RmSerializer<InstructionDetails> {
    @Override
    public void serialize(InstructionDetails data, RmToMarkdownSerializer serializer) {
        serializer.append(data.getWfDetails());
        //TODO: reference to instruction?
    }

    @Override
    public Class getSerializedClass() {
        return InstructionDetails.class;
    }
}
