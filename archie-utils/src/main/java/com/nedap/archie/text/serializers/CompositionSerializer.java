package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.composition.ContentItem;
import com.nedap.archie.rm.composition.Section;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Item;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class CompositionSerializer implements RmSerializer<Composition> {

    @Override
    public void serialize(Composition data, RmToTextSerializer serializer) {
        serializer.append("# ");
        serializer.writeToText(data.getName());
        serializer.append("\n");
        for(ContentItem item: data.getContent()) {
            serializer.writeToText(item);
            serializer.append("\n");
        }
    }

    @Override
    public Class getSerializedClass() {
        return Composition.class;
    }
}
