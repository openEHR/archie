package com.nedap.archie.odin;

import com.nedap.archie.rm.datastructures.Item;

import java.util.List;

public interface OdinParsingItemTreeMixin {

    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(converter = ItemMapToListConverter.class)
    @tools.jackson.databind.annotation.JsonDeserialize(converter = com.nedap.archie.odin3.ItemMapToListConverter.class)
    void setItems(List<Item> items);
}
