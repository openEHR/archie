package com.nedap.archie.adl14;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CPrimitiveTuple;
import com.nedap.archie.aom.SiblingOrder;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.primitives.CTerminologyCodeADL14;
import com.nedap.archie.aom.primitives.ConstraintStatus;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.base.terminology.TerminologyCode;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ADL14TermConstraintConverterTest {

    @Test
    public void toAdl2CopiesAllCObjectAndPrimitiveFields() {
        // Note: rmTypeName is not asserted here. CPrimitiveObject overrides getRmTypeName() to compute
        // it from the class name ("terminology_code"), ignoring the field. So toAdl2's copy of the
        // field is effectively a no-op for primitives — not worth pinning in a test.
        CTerminologyCodeADL14 source = new CTerminologyCodeADL14();
        source.setOccurrences(MultiplicityInterval.createMandatory());
        source.setDeprecated(Boolean.TRUE);
        SiblingOrder siblingOrder = SiblingOrder.createBefore("id1");
        source.setSiblingOrder(siblingOrder);
        source.setEnumeratedTypeConstraint(Boolean.TRUE);
        TerminologyCode assumed = TerminologyCode.createFromString("[local::at0001]");
        source.setAssumedValue(assumed);
        source.setConstraintStatus(ConstraintStatus.PREFERRED);
        source.addConstraint("at0042");

        CTerminologyCode result = ADL14TermConstraintConverter.toAdl2(source);

        assertEquals(MultiplicityInterval.createMandatory(), result.getOccurrences());
        assertEquals(Boolean.TRUE, result.getDeprecated());
        assertSame(siblingOrder, result.getSiblingOrder());
        assertEquals(Boolean.TRUE, result.getEnumeratedTypeConstraint());
        assertSame(assumed, result.getAssumedValue());
        assertEquals(ConstraintStatus.PREFERRED, result.getConstraintStatus());
        assertEquals("at0042", result.getConstraint());
    }

    @Test
    public void toAdl2CopiesSocParentForTupleMembers() {
        // Tuple members have socParent set to the CPrimitiveTuple (rather than parent set to a CAttribute).
        // The converter swaps members via list.set(...), which does not call setSocParent, so toAdl2 must copy it.
        CPrimitiveTuple primitiveTuple = new CPrimitiveTuple();
        CTerminologyCodeADL14 source = new CTerminologyCodeADL14();
        primitiveTuple.addMember(source);

        CTerminologyCode result = ADL14TermConstraintConverter.toAdl2(source);

        assertSame(primitiveTuple, result.getSocParent(),
                "socParent must be carried over so tuple-aware validators still recognise this as a tuple member");
    }

    @Test
    public void toAdl2CollapsesSingleElementConstraintListToString() {
        CTerminologyCodeADL14 source = new CTerminologyCodeADL14();
        source.setConstraint(Arrays.asList("ac0001"));

        CTerminologyCode result = ADL14TermConstraintConverter.toAdl2(source);

        assertEquals("ac0001", result.getConstraint());
    }

    @Test
    public void toAdl2LeavesConstraintNullWhenSourceConstraintListIsEmpty() {
        CTerminologyCodeADL14 source = new CTerminologyCodeADL14();
        // empty list — converter would have produced this for an empty source; CTerminologyCode keeps it null.
        CTerminologyCode result = ADL14TermConstraintConverter.toAdl2(source);
        assertNull(result.getConstraint());
    }

    @Test
    public void toAdl2DoesNotPropagateParentLink() {
        // toAdl2 itself only does a value copy. The structural setParent call lives in the converter's
        // replaceInParent helper. Verify the source's parent link is not leaked to the result.
        CAttribute attribute = new CAttribute("defining_code");
        CTerminologyCodeADL14 source = new CTerminologyCodeADL14();
        source.setParent(attribute);

        CTerminologyCode result = ADL14TermConstraintConverter.toAdl2(source);

        assertNull(result.getParent(), "toAdl2 should not copy the parent CAttribute link");
        assertSame(attribute, source.getParent(), "source's own parent link must remain intact");
    }
}
