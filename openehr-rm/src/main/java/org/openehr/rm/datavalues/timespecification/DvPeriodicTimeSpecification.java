package org.openehr.rm.datavalues.timespecification;

import com.nedap.archie.rminfo.Invariant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="DV_PERIODIC_TIME_SPECIFICATION")
public class DvPeriodicTimeSpecification extends DvTimeSpecification {

    @Invariant("Value_valid")
    public boolean valueValid() {
        if(getValue() != null && getValue().getFormalism() != null) {
            String formalism = getValue().getFormalism();
            return formalism.equals("HL7:PIVL") || formalism.equals("HL7:EIVL");
        }
        return true;
    }
}
