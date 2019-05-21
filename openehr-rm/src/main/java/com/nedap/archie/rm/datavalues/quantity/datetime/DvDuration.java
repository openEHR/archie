package com.nedap.archie.rm.datavalues.quantity.datetime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.json.DurationDeserializer;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.SingleValuedDataValue;
import com.nedap.archie.rm.datavalues.quantity.DvAmount;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.xml.adapters.DurationXmlAdapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.temporal.TemporalAmount;
import java.util.List;

/**
 * TODO: magnitude of duration is not defined properly
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_DURATION", propOrder = {
		"value"
})
public class DvDuration extends DvAmount<Long> implements SingleValuedDataValue<TemporalAmount> {

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

	public DvDuration(@Nullable List<ReferenceRange> otherReferenceRanges, @Nullable DvInterval normalRange, @Nullable CodePhrase normalStatus, @Nullable Double accuracy, @Nullable Boolean accuracyIsPercent, @Nullable String magnitudeStatus, TemporalAmount value) {
		super(otherReferenceRanges, normalRange, normalStatus, accuracy, accuracyIsPercent, magnitudeStatus);
		this.value = value;
	}

	@Override
//    @XmlElements({
//            @XmlElement(type=Period.class),
//            @XmlElement(type=Duration.class)
//    })    
	@JsonDeserialize(using = DurationDeserializer.class)
	public TemporalAmount getValue() {
		return value;
	}

	@Override
	public void setValue(TemporalAmount value) {
		this.value = value;
	}

	@XmlTransient
	@Override
	public Long getMagnitude() {
		return null; //no magnitude defined in spec
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		DvDuration that = (DvDuration) o;

		return value != null ? value.equals(that.value) : that.value == null;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
