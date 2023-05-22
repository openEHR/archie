package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.composition.ContentItem;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class CompositionSerializer implements RmSerializer<Composition> {

    @Override
    public void serialize(Composition data, RmToTextSerializer serializer) {
        serializer.append("# ");
        new LocatableSerializer().serialize(data, serializer);

        for(ContentItem item: data.getContent()) {
            serializer.append(item);
            serializer.append("\n");
        }
        if(data.getComposer() != null) {
            serializer.append("auteur: ");
            serializer.append(data.getComposer());
            serializer.append("\n");
        }
        if(data.getContext() != null) {
            serializer.append(data.getContext());
        }
        //not written for now: language, territory

    }

    @Override
    public Class getSerializedClass() {
        return Composition.class;
    }
}
