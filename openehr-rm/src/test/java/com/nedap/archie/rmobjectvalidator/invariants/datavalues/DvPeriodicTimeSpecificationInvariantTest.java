package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datavalues.encapsulated.DvParsable;
import org.openehr.rm.datavalues.timespecification.DvPeriodicTimeSpecification;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class DvPeriodicTimeSpecificationInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(createValidEIVL());
        InvariantTestUtil.assertValid(createValidPIVL());
    }

    @Test
    public void formalismInvalid() {
        DvPeriodicTimeSpecification value = new DvPeriodicTimeSpecification();
        value.setValue(new DvParsable("insert valid time spec here", "HL7:blabla"));
        InvariantTestUtil.assertInvariantInvalid(value, "Value_valid", "/");
    }

    public DvPeriodicTimeSpecification createValidPIVL() {
        DvPeriodicTimeSpecification value = new DvPeriodicTimeSpecification();
        value.setValue(new DvParsable("insert valid time spec here", "HL7:PIVL"));
        return value;
    }

    public DvPeriodicTimeSpecification createValidEIVL() {
        DvPeriodicTimeSpecification value = new DvPeriodicTimeSpecification();
        value.setValue(new DvParsable("insert valid time spec here", "HL7:EIVL"));
        return value;
    }
}
