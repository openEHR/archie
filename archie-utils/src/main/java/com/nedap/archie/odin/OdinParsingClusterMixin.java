package com.nedap.archie.odin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Item;

import java.util.List;

public interface OdinParsingClusterMixin {

    @JsonDeserialize(converter = ItemMapToListConverter.class)
    void setItems(List<Item> child);


}
