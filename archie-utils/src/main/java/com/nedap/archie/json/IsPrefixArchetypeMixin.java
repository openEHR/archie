package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class IsPrefixArchetypeMixin {

    @JsonProperty("is_differential")
    @JsonAlias("differential")
    public abstract boolean isDifferential();


    @JsonProperty("is_generated")
    @JsonAlias("generated")
    public abstract Boolean getGenerated();
}
