package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.composition.ContentItem;
import com.nedap.archie.rm.composition.Section;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Item;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class SectionSerializer implements RmSerializer<Section> {

    @Override
    public void serialize(Section data, RmToTextSerializer serializer) {
        serializer.append("## ");
        serializer.writeToText(data.getName());
        serializer.append("\n");
        for(ContentItem item: data.getItems()) {
            serializer.writeToText(item);
            serializer.append("\n");
        }
    }

    @Override
    public Class getSerializedClass() {
        return Section.class;
    }
}
