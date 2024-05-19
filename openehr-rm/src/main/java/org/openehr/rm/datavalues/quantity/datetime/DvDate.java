package org.openehr.rm.datavalues.quantity.datetime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.json.DateDeserializer;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.SingleValuedDataValue;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.rminfo.PropertyType;
import com.nedap.archie.rminfo.RMProperty;
import com.nedap.archie.xml.adapters.DateXmlAdapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Objects;

/**
 * TODO: implement java.time.Temporal for this
 * TODO: implement java.time.Temporal for this
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_DATE", propOrder = {
        "value"
})
public class DvDate extends DvTemporal<DvDate, Long> implements SingleValuedDataValue<Temporal> {
    /**
     * The number of days in a 400 year cycle.
     */
    private static final int DAYS_PER_CYCLE = 146097;
    /**
     * The number of days from year zero to year 1970.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     * Year 0000 (we are using ISO, so year zero exists) is a leap year as well.
     */
    public static final long DAYS_BETWEEN_0001_AND_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L) - (1L * 365L + 1L);


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

    public DvDate(@Nullable List<ReferenceRange<DvDate>> otherReferenceRanges, @Nullable DvInterval<DvDate> normalRange, @Nullable CodePhrase normalStatus, @Nullable String magnitudeStatus, @Nullable DvDuration accuracy, Temporal value) {
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
    @JsonIgnore
    @XmlTransient
    @RMProperty(value = "magnitude", computed = PropertyType.COMPUTED)
    public Long getMagnitude() {
        return value == null ? null : (long) LocalDate.from(value).toEpochDay() + DAYS_BETWEEN_0001_AND_1970;
    }

    public void setMagnitude(Long magnitude) {
        if(magnitude == null) {
            value = null;
        } else {
            value = LocalDate.ofEpochDay(magnitude - DAYS_BETWEEN_0001_AND_1970);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DvDate dvDate = (DvDate) o;
        return Objects.equals(value, dvDate.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
