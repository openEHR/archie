package com.nedap.archie.rm.datavalues.quantity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.rm.datatypes.CodePhrase;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * TODO: This does not implement PROPORTION KIND, because multiple inheritance - won't work.
 * It does have a type=proportion kind enum
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_PROPORTION", propOrder = {
        "numerator",
        "denominator",
        "type",
        "precision"
})
public class DvProportion extends DvAmount<Double> {

    private Double numerator;
    private Double denominator;
    private Long type;
    @Nullable
    private Long precision;

    public DvProportion() {
    }

    public DvProportion(Double numerator, Double denominator, Long type) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.type = type;
    }

    public DvProportion(@Nullable List<ReferenceRange> otherReferenceRanges, @Nullable DvInterval normalRange, @Nullable CodePhrase normalStatus, @Nullable Double accuracy, @Nullable Boolean accuracyIsPercent, @Nullable String magnitudeStatus, Double numerator, Double denominator, Long type, @Nullable Long precision) {
        super(otherReferenceRanges, normalRange, normalStatus, accuracy, accuracyIsPercent, magnitudeStatus);
        this.numerator = numerator;
        this.denominator = denominator;
        this.type = type;
        this.precision = precision;
    }

    public Double getNumerator() {
        return numerator;
    }

    public void setNumerator(Double numerator) {
        this.numerator = numerator;
    }


    public Double getDenominator() {
        return denominator;
    }

    public void setDenominator(Double denominator) {
        this.denominator = denominator;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    @Nullable
    public Long getPrecision() {
        return precision;
    }

    public void setPrecision(@Nullable Long precision) {
        this.precision = precision;
    }

    @JsonIgnore
    public boolean isIntegral() {
        return precision != null && precision == 0;
    }

    @Override
    @JsonIgnore
    public Double getMagnitude() {
        if (numerator != null && denominator != null && denominator != 0.0d) {
            return numerator / denominator;
        } else if (numerator == null) {
            return 0.0;
        } else {
            return Double.MAX_VALUE;//TODO: actually: infinity. Max Double value?
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DvProportion that = (DvProportion) o;

        if (numerator != null ? !numerator.equals(that.numerator) : that.numerator != null) return false;
        if (denominator != null ? !denominator.equals(that.denominator) : that.denominator != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return precision != null ? precision.equals(that.precision) : that.precision == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (numerator != null ? numerator.hashCode() : 0);
        result = 31 * result + (denominator != null ? denominator.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        return result;
    }
}
