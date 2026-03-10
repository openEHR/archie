package com.nedap.archie.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 18/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CARDINALITY", propOrder = {
        "isOrdered",
        "isUnique",
        "interval"
})
public class Cardinality extends OpenEHRBase {

    private MultiplicityInterval interval;

    @XmlElement(name="is_ordered")
    private boolean isOrdered = true;
    @XmlElement(name="is_unique")
    private boolean isUnique = false;

    public Cardinality() {

    }

    public Cardinality(int lower, int higher) {
        isOrdered = true;//default: list semantics
        isUnique = false;
        interval = new MultiplicityInterval(lower, higher);
    }

    public MultiplicityInterval getInterval() {
        return interval;
    }

    public void setInterval(MultiplicityInterval interval) {
        this.interval = interval;
    }

    public boolean isOrdered() {
        return isOrdered;
    }

    public void setOrdered(boolean ordered) {
        this.isOrdered = ordered;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        this.isUnique = unique;
    }

    public static Cardinality unbounded() {
        Cardinality result = new Cardinality();
        result.setInterval(MultiplicityInterval.unbounded());
        return result;
    }

    public static Cardinality mandatoryAndUnbounded() {
        Cardinality result = new Cardinality();
        result.setInterval(new MultiplicityInterval(1, true, false, null, true, true));
        return result;
    }

    /**
     * True if the semantics of this cardinality represent a bag, i.e. unordered, non-unique membership.
     *
     * @return true if this is a bag
     */
    @JsonIgnore
    public Boolean isBag() {
        return !isOrdered && !isUnique;
    }

    /**
     * True if the semantics of this cardinality represent a list, i.e. ordered, non-unique membership.
     *
     * @return true if this is a list
     */
    @JsonIgnore
    public Boolean isList() {
        return isOrdered && !isUnique;
    }

    /**
     * True if the semantics of this cardinality represent a set, i.e. unordered, unique membership.
     *
     * @return true if this is a set
     */
    @JsonIgnore
    public Boolean isSet() {
        return !isOrdered && isUnique;
    }


    /**
     * Checks whether the cardinality interval of 'other' is subsumed by the interval for this cardinality
     *
     * @param other the other cardinality
     * @return true if this cardinality contains the other
     */
    public Boolean contains(Cardinality other) {
        return getInterval().contains(other.getInterval());
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Cardinality) {
            Cardinality otherCardinality = (Cardinality) other;
            return isOrdered == otherCardinality.isOrdered &&
                    isUnique == otherCardinality.isUnique &&
                    Objects.equals(interval, otherCardinality.interval);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, isOrdered, isUnique);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("cardinality matches {");
        builder.append(interval.toString());
        builder.append("}");
        List<String> tags = new ArrayList<>();
        if (!isOrdered()) {
            tags.add("unordered");
        }
        if (isUnique()) {
            tags.add("isUnique");
        }
        if (!tags.isEmpty()) {
            builder.append("; ").append(Joiner.on("; ").join(tags));
        }
        return builder.toString();
    }
}
