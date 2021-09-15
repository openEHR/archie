package com.nedap.archie.aom.rmoverlay;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

public enum VisibilityType {
    @JsonProperty("hide")
    @XmlEnumValue("hide")
    HIDE,
    @JsonProperty("show")
    @XmlEnumValue("show")
    SHOW;
}
