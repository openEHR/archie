package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Item;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class ItemTreeSerializer implements RmSerializer<ItemTree> {

    @Override
    public void serialize(ItemTree data, RmToTextSerializer serializer) {
        serializer.append("#### ");
        serializer.writeToText(data.getName());
        serializer.append("\n");
        for(Item item: data.getItems()) {
            serializer.writeToText(item);
            serializer.append("\n");
        }
    }

    @Override
    public Class getSerializedClass() {
        return ItemTree.class;
    }
}
