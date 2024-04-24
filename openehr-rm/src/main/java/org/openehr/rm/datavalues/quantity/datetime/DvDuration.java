package org.openehr.rm.datavalues.quantity.datetime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.datetime.DateTimeParsers;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.SingleValuedDataValue;
import org.openehr.rm.datavalues.quantity.DvAmount;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.xml.adapters.DurationXmlAdapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Objects;

/**
 * TODO: magnitude of duration is not defined properly
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_DURATION", propOrder = {
		"value"
})
public class DvDuration extends DvAmount<DvDuration, Double> implements SingleValuedDataValue<TemporalAmount> {

	@XmlJavaTypeAdapter(DurationXmlAdapter.class)
	private TemporalAmount value;

	public DvDuration() {
	}

	public DvDuration(TemporalAmount value) {
		this.value = value;
	}

	/**
	 * Constructs a DvDuration form an Iso8601 Duration
	 *
	 * @param iso8601Duration
	 */
	public DvDuration(String iso8601Duration) {
		this.value = DateTimeParsers.parseDurationValue(iso8601Duration);
	}

	public DvDuration(@Nullable List<ReferenceRange<DvDuration>> otherReferenceRanges, @Nullable DvInterval<DvDuration> normalRange, @Nullable CodePhrase normalStatus, @Nullable Double accuracy, @Nullable Boolean accuracyIsPercent, @Nullable String magnitudeStatus, TemporalAmount value) {
		super(otherReferenceRanges, normalRange, normalStatus, accuracy, accuracyIsPercent, magnitudeStatus);
		this.value = value;
	}

	@Override
    public TemporalAmount getValue() {
        return value;
    }

    @Override
    public void setValue(TemporalAmount value) {
        this.value = value;
    }

    @XmlTransient
    @Override
    @JsonIgnore
    public Double getMagnitude() {
		if(value == null) {
			return null;
		}
		return TimeDefinitions.convertTemporalAmountToSeconds(value);
    }


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		DvDuration that = (DvDuration) o;

		return Objects.equals(value, that.value);

	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), value);
	}
}
