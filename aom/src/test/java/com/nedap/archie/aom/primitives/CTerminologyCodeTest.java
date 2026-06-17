package com.nedap.archie.aom.primitives;

import com.nedap.archie.base.terminology.TerminologyCode;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CTerminologyCodeTest {

    @Test
    public void constraintGetterSetterRoundtrip() {
        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        assertNull(cTerminologyCode.getConstraint());
        cTerminologyCode.setConstraint("at0001");
        assertEquals("at0001", cTerminologyCode.getConstraint());
    }

    @Test
    public void getConstraintAsListReturnsEmptyWhenConstraintIsNull() {
        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        assertEquals(Collections.emptyList(), cTerminologyCode.getConstraintAsList());
    }

    @Test
    public void getConstraintAsListReturnsSingletonWhenConstraintIsSet() {
        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        cTerminologyCode.setConstraint("at0001");
        List<String> asList = cTerminologyCode.getConstraintAsList();
        assertEquals(1, asList.size());
        assertEquals("at0001", asList.get(0));
    }

    @Test
    public void assumedValueGetterSetterRoundtrip() {
        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        assertNull(cTerminologyCode.getAssumedValue());
        TerminologyCode assumed = TerminologyCode.createFromString("[local::at0001]");
        cTerminologyCode.setAssumedValue(assumed);
        assertSame(assumed, cTerminologyCode.getAssumedValue());
    }

    @Test
    public void constraintStatusDefaultsToRequired() {
        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        assertNull(cTerminologyCode.getConstraintStatus(), "raw status should be null until set");
        assertEquals(ConstraintStatus.REQUIRED, cTerminologyCode.getEffectiveConstraintStatus(),
                "effective status should fall back to REQUIRED");
        assertTrue(cTerminologyCode.isConstraintRequired());
    }

    @Test
    public void constraintStatusGetterSetterRoundtrip() {
        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        cTerminologyCode.setConstraintStatus(ConstraintStatus.PREFERRED);
        assertEquals(ConstraintStatus.PREFERRED, cTerminologyCode.getConstraintStatus());
        assertEquals(ConstraintStatus.PREFERRED, cTerminologyCode.getEffectiveConstraintStatus());
        assertFalse(cTerminologyCode.isConstraintRequired());
    }

    @Test
    public void isValidValueReturnsTrueWhenConstraintIsNull() {
        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        // Unconstrained: any value is valid, including null.
        assertTrue(cTerminologyCode.isValidValue(null));
        assertTrue(cTerminologyCode.isValidValue(TerminologyCode.createFromString("[local::at0001]")));
    }

    @Test
    public void isValidValueReturnsFalseForNullValueWhenRequired() {
        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        cTerminologyCode.setConstraint("at0001");
        assertFalse(cTerminologyCode.isValidValue(null));
    }

    @Test
    public void isValidValueAcceptsAnythingWhenConstraintNotRequired() {
        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        cTerminologyCode.setConstraint("at0001");
        cTerminologyCode.setConstraintStatus(ConstraintStatus.PREFERRED);
        // PREFERRED is non-required, so any non-null value is accepted without value-set lookup.
        assertTrue(cTerminologyCode.isValidValue(TerminologyCode.createFromString("[local::at9999]")));
    }
}
