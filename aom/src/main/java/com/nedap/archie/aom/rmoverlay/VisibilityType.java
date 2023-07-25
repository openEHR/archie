package com.nedap.archie.aom.rmoverlay;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

public enum VisibilityType {
    @JsonProperty("hide")
    @XmlEnumValue("hide")
    HIDE,
    @JsonProperty("show")
    @XmlEnumValue("show")
    SHOW;
}
