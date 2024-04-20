package org.openehr.rm.datavalues.quantity;

import com.nedap.archie.base.RMObject;
import org.openehr.rm.datavalues.DvText;
import com.nedap.archie.rminfo.Invariant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "REFERENCE_RANGE", propOrder = {
        "meaning",
        "range"
})
public class ReferenceRange<T extends DvOrdered<T>> extends RMObject {

    private DvInterval<T> range;
    private DvText meaning;

    public ReferenceRange() {
    }

    public ReferenceRange(DvText meaning, DvInterval<T> range) {
        this.range = range;
        this.meaning = meaning;
    }

    public DvInterval<T> getRange() {
        return range;
    }

    public void setRange(DvInterval<T> range) {
        this.range = range;
    }

    public DvText getMeaning() {
        return meaning;
    }

    public void setMeaning(DvText meaning) {
        this.meaning = meaning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferenceRange<?> that = (ReferenceRange<?>) o;
        return Objects.equals(range, that.range) &&
                Objects.equals(meaning, that.meaning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, meaning);
    }

    @Invariant("Range_is_simple")
    public boolean rangeIsSimple() {
        return (range.isLowerUnbounded() || (range.getLower() != null && range.getLower().isSimple())) && (range.isUpperUnbounded() || (range.getUpper() != null && range.getUpper().isSimple()));
    }
}
