package com.nedap.archie.text.serializers;

import com.nedap.archie.rm.datastructures.Item;
import com.nedap.archie.rm.datastructures.ItemList;
import com.nedap.archie.text.RmSerializer;
import com.nedap.archie.text.RmToMarkdownSerializer;

public class ItemListSerializer implements RmSerializer<ItemList> {

    @Override
    public void serialize(ItemList data, RmToMarkdownSerializer serializer) {
        //name is rarely used, neither is feeder audit - do not show, is confusing
        //serializer.append("#### ");
        //LocatableUtil.serialize(data, serializer);
        for(Item item: data.getItems()) {
            serializer.append(item);
            serializer.appendNewLine();
        }
    }

    @Override
    public Class getSerializedClass() {
        return ItemList.class;
    }
}
