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
    public void isAnyAllowed() {
        assertTrue(code(null, null).isAnyAllowed());
        assertTrue(code("", null).isAnyAllowed());
        assertFalse(code("at1", null).isAnyAllowed());
    }

    @Test
    public void conformsToUnconstrainedParent() {
        assertConforms(code("at1", null), code(null, null));
        assertConforms(code(null, null), code(null, null));
        assertConforms(code("ac1", ConstraintStatus.EXAMPLE), code(null, ConstraintStatus.REQUIRED));
    }

    @Test
    public void unconstrainedChildDoesNotConformToRequiredParent() {
        assertDoesNotConform(code(null, null), code("at1", null));
        assertDoesNotConform(code("", null), code("at1", null));
        assertDoesNotConform(code(null, null), code("ac1", ConstraintStatus.REQUIRED));
    }

    @Test
    public void unconstrainedChildConformsToNonRequiredParent() {
        assertConforms(code(null, ConstraintStatus.PREFERRED), code("at1", ConstraintStatus.PREFERRED));
        assertConforms(code(null, null), code("ac1", ConstraintStatus.EXTENSIBLE));
    }

    @Test
    public void constraintStatusCanOnlyBeNarrowed() {
        assertConforms(code("at1", ConstraintStatus.REQUIRED), code("at1", ConstraintStatus.EXAMPLE));
        assertConforms(code("at1", ConstraintStatus.PREFERRED), code("at1", ConstraintStatus.PREFERRED));
        assertConforms(code("at1", null), code("at1", ConstraintStatus.EXTENSIBLE));
        assertDoesNotConform(code("at1", ConstraintStatus.EXAMPLE), code("at1", ConstraintStatus.PREFERRED));
        assertDoesNotConform(code("at1", ConstraintStatus.EXTENSIBLE), code("at1", null));
        assertDoesNotConform(code(null, ConstraintStatus.EXAMPLE), code("at1", ConstraintStatus.PREFERRED));
    }

    @Test
    public void anyCodeConformsToNonRequiredParent() {
        assertConforms(code("at2", ConstraintStatus.EXTENSIBLE), code("at1", ConstraintStatus.EXTENSIBLE));
        assertConforms(code("at2", ConstraintStatus.REQUIRED), code("at1", ConstraintStatus.PREFERRED));
        assertConforms(code("ac2", ConstraintStatus.EXAMPLE), code("ac1", ConstraintStatus.EXAMPLE));
        assertConforms(code("at1", ConstraintStatus.PREFERRED), code("ac1", ConstraintStatus.PREFERRED));
    }

    @Test
    public void requiredCodesMustBeConformant() {
        assertConforms(code("at1", null), code("at1", null));
        assertConforms(code("at1.1", null), code("at1", null));
        assertDoesNotConform(code("at2", null), code("at1", null));
        assertDoesNotConform(code("at1", null), code("ac1", null));
        assertDoesNotConform(code("ac1", null), code("at1", null));
    }

    private static CTerminologyCode code(String constraint, ConstraintStatus constraintStatus) {
        CTerminologyCode result = new CTerminologyCode();
        result.setConstraint(constraint);
        result.setConstraintStatus(constraintStatus);
        return result;
    }

    private static void assertConforms(CTerminologyCode child, CTerminologyCode parent) {
        assertTrue(child.cConformsTo(parent, (a, b) -> true).doesConform(), child + " should conform to " + parent);
    }

    private static void assertDoesNotConform(CTerminologyCode child, CTerminologyCode parent) {
        assertFalse(child.cConformsTo(parent, (a, b) -> true).doesConform(), child + " should not conform to " + parent);
    }
}
