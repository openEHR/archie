package com.nedap.archie.aom;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Defined Object. Parameterized so the default value methods can be overridden with a different type
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_DEFINED_OBJECT", propOrder= {
        "defaultValue"
})
public abstract class CDefinedObject<T> extends CObject {

    @XmlElement(name="default_value") //TODO: this will not deserialize, it needs possible classes
    @Nullable
    private T defaultValue;

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * True if there is an assumed value.
     *
     * @return
     */
    public Boolean hasDefaultValue() {
        return defaultValue != null;
    }
}
