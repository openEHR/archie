package org.openehr.rm.support.identification;

import com.google.common.base.Strings;
import com.nedap.archie.base.RMObject;
import com.nedap.archie.rminfo.Invariant;

import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
public abstract class UID extends RMObject {

    String value;

    public UID() {
    }

    public UID(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UID uid = (UID) o;
        return Objects.equals(value, uid.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Invariant("Value_valid")
    public boolean valueValid() {
        return !Strings.isNullOrEmpty(value);
    }
}
