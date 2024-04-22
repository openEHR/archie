package org.openehr.rm.datavalues.quantity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.rm.datatypes.CodePhrase;
import com.nedap.archie.rminfo.Invariant;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

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
public class DvProportion extends DvAmount<DvProportion, Double> {

    private Double numerator;
    private Double denominator;
    private Long type;
    @Nullable
    private Long precision;

    public DvProportion() {
    }

    public DvProportion(Double numerator, Double denominator, Long type) {
        this(numerator, denominator, type, null);
    }

    public DvProportion(Double numerator, Double denominator, Long type, Long precision) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.type = type;
        this.precision = precision;
    }

    public DvProportion(@Nullable List<ReferenceRange<DvProportion>> otherReferenceRanges, @Nullable DvInterval<DvProportion> normalRange, @Nullable CodePhrase normalStatus, @Nullable Double accuracy, @Nullable Boolean accuracyIsPercent, @Nullable String magnitudeStatus, Double numerator, Double denominator, Long type, @Nullable Long precision) {
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

    private static boolean isInteger(Double value) {
        return value == Math.floor(value) && !Double.isInfinite(value);
    }

    @JsonIgnore
    public boolean isIntegral() {
        return numerator != null && denominator != null && isInteger(numerator) && isInteger(denominator);
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
        return Objects.equals(numerator, that.numerator) &&
                Objects.equals(denominator, that.denominator) &&
                Objects.equals(type, that.type) &&
                Objects.equals(precision, that.precision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numerator, denominator, type, precision);
    }

    @Invariant("Type_validity")
    public boolean typeValid() {
        if(type != null) {
            return type >= 0 && type <= 4;
        }
        return true;
    }

    @Invariant("Precision_validity")
    public boolean precisionValid() {
        if(precision != null && precision == 0 && denominator != null && numerator != null) {
            //what can possibly go wrong with double values here....
           return isIntegral();
        }
        return true;
    }

    @Invariant(value = "Is_integral_validity", ignored = true) //not data validation, correctness validaiton
    public boolean integralValidity() {
        if(isIntegral() && denominator != null && numerator != null) {
            return (numerator.equals(Math.floor(numerator)) || Double.isInfinite(numerator)) &&
                    (denominator.equals(Math.floor(denominator)) || Double.isFinite(denominator));
        }
        return true;
    }

    @Invariant("Fraction_validity")
    public boolean fractionValidity() {
        if(type != null && (type.equals(ProportionKind.FRACTION.getPk()) || type.equals(ProportionKind.INTEGER_FRACTION.getPk()))) {
            return isIntegral();
        }
        return true;
    }

    @Invariant("Unitary_validity")
    public boolean unitaryValidity() {
        if(type != null && denominator != null && type.equals(ProportionKind.UNITARY.getPk())) {
            return denominator.equals(1d);
        }
        return true;
    }

    @Invariant("Percent_validity")
    public boolean percentValidity() {
        if(type != null && denominator != null && type.equals(ProportionKind.PERCENT.getPk())) {
            return denominator.equals(100d);
        }
        return true;
    }

    @Invariant("Valid_denominator")
    public boolean denominatorValid() {
        if(denominator != null) {
            return !denominator.equals(0d);
        }
        return true;
    }


}
