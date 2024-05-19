package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.DvQuantity;
import org.openehr.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Also tests DV_ORDERED, DV_AMOUNT, DV_QUANTIFIED...
 */
public class DvQuantityInvariantTest {

    @Test
    public void testValid() {
        InvariantTestUtil.assertValid(createValid());
        DvQuantity quantity = createValid();
        quantity.setMagnitude(1500d);
        quantity.setNormalStatus(new CodePhrase("openehr_normal_statuses::HH")); //quite a bit higher
        InvariantTestUtil.assertValid(quantity);
    }

    @Test
    public void normalStatusInvalid() {
        DvQuantity quantity = createValid();
        quantity.setMagnitude(1500d);
        quantity.setNormalStatus(new CodePhrase("openehr_normal_statuses::BLABLA")); //quite a bit higher
        InvariantTestUtil.assertInvariantInvalid(quantity, "Normal_status_validity", "/");
    }

    @Test
    public void valueInsideNormalRangeInvalid() {
        DvQuantity quantity = createValid();

        quantity.setNormalStatus(new CodePhrase("openehr_normal_statuses::HH")); //value indicated to be higher
        quantity.setMagnitude(500d); //yet inside the normal range!
        InvariantTestUtil.assertInvariantInvalid(quantity, "Normal_range_and_status_consistency", "/");
    }

    @Test
    public void invalidPercentage() {
        DvQuantity quantity = createValid();
        quantity.setAccuracyIsPercent(true);
        quantity.setAccuracy(101d);
        InvariantTestUtil.assertInvariantInvalid(quantity, "Accuracy_valid", "/");
        quantity.setAccuracy(-1d);
        InvariantTestUtil.assertInvariantInvalid(quantity, "Accuracy_valid", "/");
        quantity.setAccuracy(0.0d);
        InvariantTestUtil.assertInvariantInvalid(quantity, "Accuracy_is_percent_validity", "/");
    }

    @Test
    public void invalidMagnitudeStatus() {
        DvQuantity quantity = createValid();
        quantity.setMagnitudeStatus("bigger than");
        InvariantTestUtil.assertInvariantInvalid(quantity, "Magnitude_status_valid", "/");
    }

    @Test
    public void invalidReferenceRange() {
        DvQuantity rangeBottom = new DvQuantity();
        rangeBottom.setMagnitude(0d);
        rangeBottom.setUnits("kg");

        DvQuantity rangeTop = new DvQuantity();
        rangeTop.setMagnitude(1000d);
        rangeTop.setUnits("kg");

        DvQuantity illegalRangeBottom = new DvQuantity();
        illegalRangeBottom.setMagnitude(0d);
        illegalRangeBottom.setUnits("kg");

        DvQuantity illegalrangeTop = new DvQuantity();
        illegalrangeTop.setMagnitude(1000d);
        illegalrangeTop.setUnits("kg");


        rangeTop.setNormalRange(new DvInterval(illegalRangeBottom, illegalrangeTop));

        List<ReferenceRange<DvQuantity>> otherReferenceRanges = new ArrayList<>();
        ReferenceRange range = new ReferenceRange();
        range.setMeaning(new DvText("some reference range"));
        range.setRange(new DvInterval(rangeBottom, rangeTop));
        otherReferenceRanges.add(range);
        DvQuantity value = createValid();
        value.setOtherReferenceRanges(otherReferenceRanges);
        InvariantTestUtil.assertInvariantInvalid(value, "Range_is_simple", "REFERENCE_RANGE", "/other_reference_ranges[1]");
    }

    public DvQuantity createValid() {
        DvQuantity value = new DvQuantity();
        value.setMagnitude(43d);
        value.setPrecision(0l);
        value.setUnits("kg");
        value.setNormalStatus(new CodePhrase("openehr_normal_statuses::N"));

        DvQuantity normalRangeBottom = new DvQuantity();
        normalRangeBottom.setMagnitude(0d);
        normalRangeBottom.setUnits("kg");

        DvQuantity normalRangeTop = new DvQuantity();
        normalRangeTop.setMagnitude(1000d);
        normalRangeTop.setUnits("kg");
        value.setNormalRange(new DvInterval(normalRangeBottom, normalRangeTop));

        List<ReferenceRange<DvQuantity>> otherReferenceRanges = new ArrayList<>();
        ReferenceRange range = new ReferenceRange();
        range.setMeaning(new DvText("some reference range"));
        range.setRange(new DvInterval(normalRangeBottom, normalRangeTop));
        otherReferenceRanges.add(range);
        value.setOtherReferenceRanges(otherReferenceRanges);

        value.setAccuracy(100d);
        value.setAccuracyIsPercent(true);

        value.setMagnitudeStatus("=");
        return value;
    }
}
