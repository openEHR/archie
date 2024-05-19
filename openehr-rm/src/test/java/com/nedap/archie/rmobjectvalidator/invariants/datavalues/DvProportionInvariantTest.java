package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datavalues.quantity.DvProportion;
import org.openehr.rm.datavalues.quantity.ProportionKind;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class DvProportionInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new DvProportion(5d, 1d, ProportionKind.UNITARY.getPk()));
        InvariantTestUtil.assertValid(new DvProportion(5.5d, 1d, ProportionKind.UNITARY.getPk()));
        InvariantTestUtil.assertValid(new DvProportion(1d, 100d, ProportionKind.PERCENT.getPk()));
        InvariantTestUtil.assertValid(new DvProportion(1d, 100d, ProportionKind.INTEGER_FRACTION.getPk()));
        InvariantTestUtil.assertValid(new DvProportion(5d, 100d, ProportionKind.FRACTION.getPk()));
        InvariantTestUtil.assertValid(new DvProportion(0.5d, 100.6d, ProportionKind.RATIO.getPk()));
    }

    @Test
    public void unitaryInvalid() {
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5.5d, 2d, ProportionKind.UNITARY.getPk()), "Unitary_validity", "/");
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5.5d, -1d, ProportionKind.UNITARY.getPk()), "Unitary_validity", "/");
    }

    @Test
    public void percentInvalid() {
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5.5d, 2d, ProportionKind.PERCENT.getPk()), "Percent_validity", "/");
    }

    @Test
    public void integerFractionInvalid() {
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5.5d, 2d, ProportionKind.INTEGER_FRACTION.getPk()), "Fraction_validity", "/");
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5d, 2.01d, ProportionKind.INTEGER_FRACTION.getPk()), "Fraction_validity", "/");
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5d, 2.01d, ProportionKind.INTEGER_FRACTION.getPk(), 1l), "Fraction_validity", "/");
    }

    @Test
    public void fractionInvalid() {
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5.5d, 2d, ProportionKind.INTEGER_FRACTION.getPk()), "Fraction_validity", "/");
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5d, 2.01d, ProportionKind.INTEGER_FRACTION.getPk()), "Fraction_validity", "/");
    }

    @Test
    public void denominatorZero() {
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5.5d, 0d, ProportionKind.RATIO.getPk()), "Valid_denominator", "/");
    }

    @Test
    public void typeValid() {
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5.5d, 1d, -1l), "Type_validity", "/");
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5.5d, 1d, 5l), "Type_validity", "/");
    }

    @Test
    public void precisionInvalid() {
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5.5d, 1d, ProportionKind.RATIO.getPk(), 0l), "Precision_validity", "/");
        InvariantTestUtil.assertInvariantInvalid(new DvProportion(5d, 1.2d, ProportionKind.RATIO.getPk(), 0l), "Precision_validity", "/");
    }

}
