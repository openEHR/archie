package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Evaluation;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;
import com.nedap.archie.text.serializers.LocatableSerializer;

public class EvaluationSerializer implements RmSerializer<Evaluation> {
    @Override
    public void serialize(Evaluation data, RmToTextSerializer serializer) {
        serializer.append("### ");
        new LocatableSerializer().serialize(data, serializer);
        serializer.append(data.getData());
        serializer.append("\n");
        serializer.append(data.getProtocol());
    }

    @Override
    public Class getSerializedClass() {
        return Evaluation.class;
    }
}
