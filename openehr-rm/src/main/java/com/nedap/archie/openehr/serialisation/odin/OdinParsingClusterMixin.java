package com.nedap.archie.openehr.serialisation.odin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.openehr.rm.datastructures.Item;

import java.util.List;

public interface OdinParsingClusterMixin {

    @JsonDeserialize(converter = ItemMapToListConverter.class)
    void setItems(List<Item> child);


}
