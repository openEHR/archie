package org.s2.util.enumerations;

import com.nedap.archie.base.RMObject;

import javax.xml.bind.annotation.XmlElement;
import java.util.Objects;

public abstract class EnumerationVar<V, T extends EnumerationType<V>> extends RMObject {

    /**
     * Enumeration type.
     */
    final T enumeration;

    /**
     * Enumeration value.
     */
    @XmlElement(name = "value")
    protected V value;

    public EnumerationVar() {
        enumeration = (T) new DefaultEnum();
        this.value = (V) enumeration.getItemValue(0);
    }

    public EnumerationVar(V value) {
        this();
        this.value = value;
    }

    public T getEnumeration() {
        return enumeration;
    };

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        EnumerationVar<?, ?> otherEnum = (EnumerationVar<?, ?>) other;
        return value.equals(otherEnum.getValue());
    }
}
