package com.nedap.archie.rm.datavalues.quantity.datetime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.json.DateTimeDeserializer;
import com.nedap.archie.json.DateTimeSerializer;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.SingleValuedDataValue;
import com.nedap.archie.rm.datavalues.quantity.DvInterval;
import com.nedap.archie.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.xml.adapters.DateTimeXmlAdapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.List;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_DATE_TIME", propOrder = {
		"value"
})
public class DvDateTime extends DvTemporal<Long> implements SingleValuedDataValue<TemporalAccessor> {

	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	private TemporalAccessor value;


	public DvDateTime() {
	}

	public DvDateTime(TemporalAccessor value) {
		this.value = value;
	}

	/**
	 * Constructs a DvDateTime from an ISO 8601 Date-Time String
	 *
	 * @param iso8601DateTime
	 */
	public DvDateTime(String iso8601DateTime) {
		this.value = DateTimeParsers.parseDateTimeValue(iso8601DateTime);
	}


	public DvDateTime(@Nullable List<ReferenceRange> otherReferenceRanges, @Nullable DvInterval normalRange, @Nullable CodePhrase normalStatus, @Nullable String magnitudeStatus, @Nullable DvDuration accuracy, TemporalAccessor value) {
		super(otherReferenceRanges, normalRange, normalStatus, magnitudeStatus, accuracy);
		this.value = value;
	}

	@Override
//    @XmlElements({
//            @XmlElement(type = OffsetDateTime.class),
//            @XmlElement(type = LocalDateTime.class)
//    })
	@JsonSerialize(using = DateTimeSerializer.class)
	@JsonDeserialize(using = DateTimeDeserializer.class)
	public TemporalAccessor getValue() {
		return value;
	}

	@Override
	public void setValue(TemporalAccessor value) {
		this.value = value;
	}

	@Override
	@XmlTransient
	@JsonIgnore
	public Long getMagnitude() {
		if (value == null) {
			return null;
		}
		if (value.query(TemporalQueries.zone()) != null) {
			return ZonedDateTime.from(value).toEpochSecond();
		} else {
			return LocalDateTime.from(value).toEpochSecond(ZoneOffset.UTC);
		}
	}

	public void setMagnitude(Long magnitude) {
		if (magnitude == null) {
			value = null;
		} else {
			value = LocalDateTime.ofEpochSecond(magnitude, 0, ZoneOffset.UTC);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		DvDateTime that = (DvDateTime) o;

		return value != null ? value.equals(that.value) : that.value == null;

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
