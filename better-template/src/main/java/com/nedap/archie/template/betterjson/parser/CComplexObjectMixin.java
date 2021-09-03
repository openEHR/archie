package com.nedap.archie.template.betterjson.parser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.base.OpenEHRBase;

@JsonIgnoreProperties(value = {"attributeCustomizations"})
public class CComplexObjectMixin extends CComplexObject {

//    @Override
//    @JsonDeserialize(using=DefaultValueDeserializer.class)
//    public void setDefaultValue(OpenEHRBase something) {
//
//    }
}
