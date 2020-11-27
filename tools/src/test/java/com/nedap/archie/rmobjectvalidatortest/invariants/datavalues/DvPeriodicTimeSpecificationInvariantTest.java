package com.nedap.archie.rmobjectvalidatortest.invariants.datavalues;

import com.nedap.archie.rm.datavalues.encapsulated.DvParsable;
import com.nedap.archie.rm.datavalues.timespecification.DvPeriodicTimeSpecification;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmobjectvalidatortest.InvariantTestUtil;
import com.nedap.archie.rmutil.InvariantUtil;
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
