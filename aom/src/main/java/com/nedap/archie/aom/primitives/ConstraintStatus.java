package com.nedap.archie.aom.primitives;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlEnumValue;

public enum ConstraintStatus {
    @JsonProperty("required")
    @XmlEnumValue("required")
    REQUIRED,
    @JsonProperty("extensible")
    @XmlEnumValue("extensible")
    EXTENSIBLE,
    @JsonProperty("preferred")
    @XmlEnumValue("preferred")
    PREFERRED,
    @JsonProperty("example")
    @XmlEnumValue("example")
    EXAMPLE;
}
