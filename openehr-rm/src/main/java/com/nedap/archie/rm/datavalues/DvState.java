package com.nedap.archie.rm.datavalues;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

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
}
