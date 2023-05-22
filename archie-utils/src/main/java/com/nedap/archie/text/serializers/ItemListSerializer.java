package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.datastructures.Item;
import com.nedap.archie.rm.datastructures.ItemList;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToTextSerializer;

public class ItemListSerializer implements RmSerializer<ItemList> {

    @Override
    public void serialize(ItemList data, RmToTextSerializer serializer) {
        serializer.append("#### ");
        new LocatableSerializer().serialize(data, serializer);
        for(Item item: data.getItems()) {
            serializer.append(item);
            serializer.append("\n");
        }
    }

    @Override
    public Class getSerializedClass() {
        return ItemList.class;
    }
}
