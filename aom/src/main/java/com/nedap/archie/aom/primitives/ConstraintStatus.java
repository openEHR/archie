package com.nedap.archie.aom.primitives;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlEnumValue;

public enum ConstraintStatus {
    @JsonProperty("required")
    @XmlEnumValue("required")
    REQUIRED(0),
    @JsonProperty("extensible")
    @XmlEnumValue("extensible")
    EXTENSIBLE(1),
    @JsonProperty("preferred")
    @XmlEnumValue("preferred")
    PREFERRED(2),
    @JsonProperty("example")
    @XmlEnumValue("example")
    EXAMPLE(3);

    private final int value;

    ConstraintStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean cConformsTo(ConstraintStatus parent) {
        return value <= parent.value;
    }
}
