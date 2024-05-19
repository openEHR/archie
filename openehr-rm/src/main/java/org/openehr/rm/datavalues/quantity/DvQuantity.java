package org.openehr.rm.datavalues.quantity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.rm.datatypes.CodePhrase;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_QUANTITY", propOrder = {
        "magnitude",
        "units",
        "precision",
        "unitsSystem",
        "unitsDisplayName"
})
public class DvQuantity extends DvAmount<DvQuantity, Double> {

    @Nullable
    @XmlElement(defaultValue = "-1")
    private Long precision;
    private String units;
    @XmlElement
    private Double magnitude;
    @Nullable
    @XmlElement(name = "units_system")
    private String unitsSystem;
    @Nullable
    @XmlElement(name = "units_display_name")
    private String unitsDisplayName;


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

    public DvQuantity(@Nullable List<ReferenceRange<DvQuantity>> otherReferenceRanges, @Nullable DvInterval<DvQuantity> normalRange, @Nullable CodePhrase normalStatus, @Nullable Double accuracy, @Nullable Boolean accuracyIsPercent, @Nullable String magnitudeStatus, String units, Double magnitude, @Nullable Long precision) {
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
    @JsonIgnore
    public CodePhrase getProperty() {
        return property;
    }

    @Deprecated
    public void setProperty(CodePhrase property) {
        this.property = property;
    }

    @Nullable
    public String getUnitsSystem() {
        return unitsSystem;
    }

    public void setUnitsSystem(@Nullable String unitsSystem) {
        this.unitsSystem = unitsSystem;
    }

    @Nullable
    public String getUnitsDisplayName() {
        return unitsDisplayName;
    }

    public void setUnitsDisplayName(@Nullable String unitsDisplayName) {
        this.unitsDisplayName = unitsDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DvQuantity that = (DvQuantity) o;
        return Objects.equals(precision, that.precision) &&
                Objects.equals(units, that.units) &&
                Objects.equals(magnitude, that.magnitude) &&
                Objects.equals(property, that.property) &&
                Objects.equals(unitsSystem, that.unitsSystem) &&
                Objects.equals(unitsDisplayName, that.unitsDisplayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), precision, units, magnitude, property);
    }
}
