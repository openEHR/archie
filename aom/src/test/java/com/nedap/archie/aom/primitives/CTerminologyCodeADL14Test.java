package com.nedap.archie.aom.primitives;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.base.terminology.TerminologyCode;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CTerminologyCodeADL14Test {

    @Test
    public void newInstanceHasEmptyConstraintList() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        assertTrue(cTerminologyCode.getConstraint().isEmpty());
        assertTrue(cTerminologyCode.getConstraintAsList().isEmpty());
    }

    @Test
    public void addConstraintAccumulates() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        cTerminologyCode.addConstraint("at0001");
        cTerminologyCode.addConstraint("at0002");
        assertEquals(Arrays.asList("at0001", "at0002"), cTerminologyCode.getConstraint());
    }

    @Test
    public void setConstraintReplacesList() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        cTerminologyCode.addConstraint("at0001");
        cTerminologyCode.setConstraint(Arrays.asList("at0042", "at0043"));
        assertEquals(Arrays.asList("at0042", "at0043"), cTerminologyCode.getConstraint());
    }

    @Test
    public void assumedValueGetterSetterRoundtrip() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        assertNull(cTerminologyCode.getAssumedValue());
        TerminologyCode assumed = TerminologyCode.createFromString("[local::at0001]");
        cTerminologyCode.setAssumedValue(assumed);
        assertSame(assumed, cTerminologyCode.getAssumedValue());
    }

    @Test
    public void constraintStatusDefaultsToRequired() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        assertNull(cTerminologyCode.getConstraintStatus(), "raw status should be null until set");
        assertEquals(ConstraintStatus.REQUIRED.getValue(), cTerminologyCode.getEffectiveConstraintStatus());
        assertTrue(cTerminologyCode.isConstraintRequired());
    }

    @Test
    public void constraintStatusGetterSetterRoundtrip() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        cTerminologyCode.setConstraintStatus(ConstraintStatus.EXTENSIBLE);
        assertEquals(ConstraintStatus.EXTENSIBLE.getValue(), cTerminologyCode.getEffectiveConstraintStatus());
        assertFalse(cTerminologyCode.isConstraintRequired());
    }

    @Test
    public void toStringRendersAllConstraints() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        cTerminologyCode.addConstraint("at0001");
        cTerminologyCode.addConstraint("at0002");
        assertEquals("{[at0001, at0002]}", cTerminologyCode.toString());
    }

    @Test
    public void toStringForEmptyConstraint() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        assertEquals("{[]}", cTerminologyCode.toString());
    }

    @Test
    public void getRmTypeNameDefaultWhenNoParent() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        assertEquals("terminology_code", cTerminologyCode.getRmTypeName());
    }

    @Test
    public void getRmTypeNameForDefiningCodeAttribute() {
        CTerminologyCodeADL14 cTerminologyCode = withParentAttribute("defining_code");
        assertEquals("CODE_PHRASE", cTerminologyCode.getRmTypeName());
    }

    @Test
    public void getRmTypeNameForSymbolAttribute() {
        CTerminologyCodeADL14 cTerminologyCode = withParentAttribute("symbol");
        assertEquals("DV_CODED_TEXT", cTerminologyCode.getRmTypeName());
    }

    @Test
    public void getRmTypeNameForOtherAttributeFallsBackToDefault() {
        CTerminologyCodeADL14 cTerminologyCode = withParentAttribute("something_else");
        assertEquals("terminology_code", cTerminologyCode.getRmTypeName());
    }

    @Test
    public void getValueSetExpandedCollectsAtCodes() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        cTerminologyCode.addConstraint("at0001");
        cTerminologyCode.addConstraint("at0002");
        // With no archetype attached, ac-codes are not expandable, but at-codes pass through.
        List<String> expanded = cTerminologyCode.getValueSetExpanded();
        assertEquals(Arrays.asList("at0001", "at0002"), expanded);
    }

    @Test
    public void getValueSetExpandedSkipsAcCodesWithoutArchetype() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        cTerminologyCode.addConstraint("ac0001");
        // ac-codes need a terminology to resolve, and without one they're silently skipped.
        assertTrue(cTerminologyCode.getValueSetExpanded().isEmpty());
    }

    @Test
    public void getTermsReturnsEmptyWhenNoArchetype() {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        cTerminologyCode.addConstraint("at0001");
        // No archetype attached -> early return with empty list (guards against NPE in rule contexts).
        assertTrue(cTerminologyCode.getTerms().isEmpty());
    }

    private CTerminologyCodeADL14 withParentAttribute(String rmAttributeName) {
        CTerminologyCodeADL14 cTerminologyCode = new CTerminologyCodeADL14();
        CAttribute parent = new CAttribute(rmAttributeName);
        cTerminologyCode.setParent(parent);
        return cTerminologyCode;
    }
}
