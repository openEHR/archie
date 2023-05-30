package com.nedap.archie.text.serializers.entries;

import com.nedap.archie.rm.composition.Evaluation;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;
import com.nedap.archie.text.serializers.LocatableUtil;

public class EvaluationSerializer implements RmSerializer<Evaluation> {
    @Override
    public void serialize(Evaluation data, RmToMarkdownSerializer serializer) {
        serializer.append("### ");
        LocatableUtil.serialize(data, serializer);
        serializer.append(data.getData());
        serializer.appendNewLine();
        serializer.append(data.getProtocol());
    }

    @Override
    public Class getSerializedClass() {
        return Evaluation.class;
    }
}
