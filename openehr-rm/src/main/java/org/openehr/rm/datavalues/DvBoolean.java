package org.openehr.rm.datavalues;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_BOOLEAN", propOrder = {
        "value"
})
public class DvBoolean extends DataValue implements SingleValuedDataValue<Boolean> {

    private Boolean value;

    public DvBoolean() {
    }

    public DvBoolean(Boolean value) {
        this.value = value;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DvBoolean dvBoolean = (DvBoolean) o;
        return Objects.equals(value, dvBoolean.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
