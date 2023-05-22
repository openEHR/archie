package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Evaluation;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class EvaluationSerializer implements RmSerializer<Evaluation> {
    @Override
    public void serialize(Evaluation data, RmToTextSerializer serializer) {
        serializer.append("### ");
        serializer.append(data.getName());
        serializer.append("\n");
        serializer.append(data.getData());
        serializer.append("\n");
        serializer.append(data.getProtocol());
    }

    @Override
    public Class getSerializedClass() {
        return Evaluation.class;
    }
}
