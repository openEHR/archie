package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AuthoredResourceMixin {

    @JsonProperty("is_controlled")
    @JsonAlias("controlled")
    public abstract Boolean getControlled();
}
