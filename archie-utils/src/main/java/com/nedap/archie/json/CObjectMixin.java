package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class CObjectMixin {

    @JsonProperty("is_deprecated")
    @JsonAlias("deprecated")
    public abstract Boolean getDeprecated();

}
