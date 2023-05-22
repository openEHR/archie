package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.composition.ContentItem;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class CompositionSerializer implements RmSerializer<Composition> {

    @Override
    public void serialize(Composition data, RmToTextSerializer serializer) {
        serializer.append("# ");
        serializer.append(data.getName());
        serializer.append("\n");
        for(ContentItem item: data.getContent()) {
            serializer.append(item);
            serializer.append("\n");
        }
    }

    @Override
    public Class getSerializedClass() {
        return Composition.class;
    }
}
