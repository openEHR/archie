package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ArchetypeSlotMixin {

    @JsonProperty("is_closed")
    @JsonAlias("closed")
    public abstract boolean isClosed();
}
