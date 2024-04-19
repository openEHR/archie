package org.openehr.rm.datavalues.quantity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DataValue;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rminfo.RMPropertyIgnore;
import com.nedap.archie.openehr.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_ORDERED", propOrder = {
        "normalRange",
        "otherReferenceRanges",
        "normalStatus"
})
public abstract class DvOrdered<DataValueType extends DvOrdered<DataValueType>> extends DataValue implements Comparable<DataValueType> {

    @Nullable
    @XmlElement(name = "normal_status")
    private CodePhrase normalStatus;
    @Nullable
    @XmlElement(name = "normal_range")
    private DvInterval<DataValueType> normalRange;

    @Nullable
    @XmlElement(name = "other_reference_ranges")
    private List<ReferenceRange<DataValueType>> otherReferenceRanges = new ArrayList<>();

    public DvOrdered() {
    }

    public DvOrdered(@Nullable List<ReferenceRange<DataValueType>> otherReferenceRanges, @Nullable DvInterval<DataValueType> normalRange) {
        this.normalRange = normalRange;
        this.otherReferenceRanges = otherReferenceRanges;
    }

    protected DvOrdered(@Nullable List<ReferenceRange<DataValueType>> otherReferenceRanges, @Nullable DvInterval<DataValueType> normalRange, @Nullable CodePhrase normalStatus) {
        this.normalStatus = normalStatus;
        this.normalRange = normalRange;
        this.otherReferenceRanges = otherReferenceRanges;
    }


    public DvInterval<DataValueType> getNormalRange() {
        return normalRange;
    }

    public void setNormalRange(DvInterval<DataValueType> normalRange) {
        this.normalRange = normalRange;
    }

    public List<ReferenceRange<DataValueType>> getOtherReferenceRanges() {
        return otherReferenceRanges;
    }

    public void setOtherReferenceRanges(List<ReferenceRange<DataValueType>> otherReferenceRanges) {
        this.otherReferenceRanges = otherReferenceRanges;
    }

    public void addOtherReferenceRange(ReferenceRange<DataValueType> range) {
        otherReferenceRanges.add(range);
    }

    @Nullable
    public CodePhrase getNormalStatus() {
        return normalStatus;
    }

    public void setNormalStatus(@Nullable CodePhrase normalStatus) {
        this.normalStatus = normalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DvOrdered<?> dvOrdered = (DvOrdered<?>) o;
        return Objects.equals(normalStatus, dvOrdered.normalStatus) &&
                Objects.equals(normalRange, dvOrdered.normalRange) &&
                Objects.equals(otherReferenceRanges, dvOrdered.otherReferenceRanges);
    }

    @JsonIgnore
    @XmlTransient
    @RMPropertyIgnore
    public boolean isSimple() {
        return normalRange == null && (otherReferenceRanges == null || otherReferenceRanges.isEmpty());
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalStatus, normalRange, otherReferenceRanges);
    }

    @Invariant(value = "Other_reference_ranges_validity", ignored = true)
    public boolean otherReferenceRangesValid() {
        return InvariantUtil.nullOrNotEmpty(otherReferenceRanges);
    }

    @Invariant(value = "Is_simple_validity", ignored = true)
    public boolean simpleValiditiy() {
        return true;//we're not checking whether our implementation is correct here, just data
    }

    @Invariant("Normal_status_validity")
    public boolean normalStatusValidity() {
        return InvariantUtil.belongsToTerminologyByOpenEHRId(normalStatus, "normal statuses");
    }

    @Invariant("Normal_range_and_status_consistency")
    public boolean normalRangeAndStatusConsistency() {
        if(normalStatus != null && normalRange != null) {
            return Objects.equals(normalStatus.getCodeString(), "N") ^ !normalRange.has((DataValueType) this);
        }
        return true;
    }

}
