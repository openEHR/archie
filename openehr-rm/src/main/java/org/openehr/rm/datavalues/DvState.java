package org.openehr.rm.datavalues;

import com.nedap.archie.rminfo.RMProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_STATE", propOrder = {
        "value",
        "isTerminal"
})
public class DvState extends DataValue implements SingleValuedDataValue<DvCodedText> {

    @XmlElement(name = "is_terminal")
    @RMProperty("is_terminal")
    private boolean isTerminal;
    private DvCodedText value;

    public boolean isTerminal() {
        return isTerminal;
    }

    public void setTerminal(boolean terminal) {
        this.isTerminal = terminal;
    }

    public DvCodedText getValue() {
        return value;
    }

    public void setValue(DvCodedText value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DvState dvState = (DvState) o;
        return isTerminal == dvState.isTerminal &&
                Objects.equals(value, dvState.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isTerminal, value);
    }
}
