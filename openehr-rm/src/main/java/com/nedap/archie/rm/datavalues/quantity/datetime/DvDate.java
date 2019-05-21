package com.nedap.archie.rm.datavalues.quantity.datetime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.json.DateDeserializer;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.SingleValuedDataValue;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.xml.adapters.DateXmlAdapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.List;

/**
 * TODO: implement java.time.Temporal for this
 * TODO: implement java.time.Temporal for this
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_DATE", propOrder = {
        "value"
})
public class DvDate extends DvTemporal<Long> implements SingleValuedDataValue<Temporal> {
    //TODO: in XML this should be a string probably
    @XmlJavaTypeAdapter(DateXmlAdapter.class)
    private Temporal value;


    public DvDate() {
    }

    public DvDate(Temporal value) {
        setValue(value);
    }


    /**
     * Constructs a DvDate from an ISO 8601 Date String
     *
     * @param iso8601Date
     */
    public DvDate(String iso8601Date) {

        setValue(DateTimeParsers.parseDateValue(iso8601Date));
    }

    public DvDate(@Nullable List<ReferenceRange> otherReferenceRanges, @Nullable DvInterval normalRange, @Nullable CodePhrase normalStatus, @Nullable String magnitudeStatus, @Nullable DvDuration accuracy, Temporal value) {
        super(otherReferenceRanges, normalRange, normalStatus, magnitudeStatus, accuracy);
        this.value = value;
    }

    @Override
//    @XmlElements({
//            @XmlElement(type=LocalDate.class),
//            @XmlElement(type=YearMonth.class),
//            @XmlElement(type=Year.class)
//
//    })
    @JsonDeserialize(using= DateDeserializer.class)
    public Temporal getValue() {
        return value;
    }

    @Override
    public void setValue(Temporal value) {
        if(value.isSupported(ChronoField.HOUR_OF_DAY)) {
            //TODO: do we really need this validation?
            throw new IllegalArgumentException("value must only have a year, month or date, but this supports hours: " + value);
        }
        this.value = value;
    }

    @Override
    public Long getMagnitude() {
        return value == null ? null : (long) LocalDate.from(value).toEpochDay();
    }

    public void setMagnitude(Long magnitude) {
        if(magnitude == null) {
            value = null;
        } else {
            value = LocalDate.ofEpochDay(magnitude);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DvDate dvDate = (DvDate) o;

        return value != null ? value.equals(dvDate.value) : dvDate.value == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
