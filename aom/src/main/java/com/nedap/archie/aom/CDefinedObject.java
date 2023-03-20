package com.nedap.archie.aom;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CDefinedObject)) return false;
        if (!super.equals(o)) return false;
        CDefinedObject<?> that = (CDefinedObject<?>) o;
        return Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), defaultValue);
    }
}
