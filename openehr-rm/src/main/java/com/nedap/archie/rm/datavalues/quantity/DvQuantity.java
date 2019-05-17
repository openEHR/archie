package com.nedap.archie.rm.datavalues.quantity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.rm.datatypes.CodePhrase;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_QUANTITY", propOrder = {
        "magnitude",
        "units",
        "precision"
})
public class DvQuantity extends DvAmount<Double> {

    @Nullable
    @XmlElement(defaultValue = "-1")
    private Long precision;
    private String units;
    @XmlElement
    private Double magnitude;

    /**
     * This has been removed, but causes many archetypes to fail because they still define it. So introduce, but don't use
     * don't even serialize
     */
    @Deprecated
    @JsonIgnore
    @Nullable
    private transient CodePhrase property;


    public DvQuantity() {
    }

    public DvQuantity(String units, Double magnitude, @Nullable Long precision) {
        this.precision = precision;
        this.units = units;
        this.magnitude = magnitude;
    }

    public DvQuantity(@Nullable List<ReferenceRange> otherReferenceRanges, @Nullable DvInterval normalRange, @Nullable CodePhrase normalStatus, @Nullable Double accuracy, @Nullable Boolean accuracyIsPercent, @Nullable String magnitudeStatus, String units, Double magnitude, @Nullable Long precision) {
        super(otherReferenceRanges, normalRange, normalStatus, accuracy, accuracyIsPercent, magnitudeStatus);
        this.precision = precision;
        this.units = units;
        this.magnitude = magnitude;
    }

    @Nullable
    public Long getPrecision() {
        return precision;
    }

    public void setPrecision(@Nullable Long precision) {
        this.precision = precision;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    @Deprecated
    public CodePhrase getProperty() {
        return property;
    }

    @Deprecated
    public void setProperty(CodePhrase property) {
        this.property = property;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DvQuantity that = (DvQuantity) o;

        if (precision != null ? !precision.equals(that.precision) : that.precision != null) return false;
        if (units != null ? !units.equals(that.units) : that.units != null) return false;
        return magnitude != null ? magnitude.equals(that.magnitude) : that.magnitude == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        result = 31 * result + (units != null ? units.hashCode() : 0);
        result = 31 * result + (magnitude != null ? magnitude.hashCode() : 0);
        return result;
    }
}
