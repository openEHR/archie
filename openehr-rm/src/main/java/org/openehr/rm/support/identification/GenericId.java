package org.openehr.rm.support.identification;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GENERIC_ID", propOrder = {"scheme"})
public class GenericId extends ObjectId {

    private String scheme;

    public GenericId() {
    }

    public GenericId(String value, String scheme) {
        super(value);
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GenericId genericId = (GenericId) o;
        return Objects.equals(scheme, genericId.scheme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), scheme);
    }
}
