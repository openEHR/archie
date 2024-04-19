package org.openehr.rm.datavalues.quantity.datetime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.json.DateTimeDeserializer;
import com.nedap.archie.json.DateTimeSerializer;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.SingleValuedDataValue;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.ReferenceRange;
import com.nedap.archie.rminfo.PropertyType;
import com.nedap.archie.rminfo.RMProperty;
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
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_DATE_TIME", propOrder = {
		"value"
})
public class DvDateTime extends DvTemporal<DvDateTime, Long> implements SingleValuedDataValue<TemporalAccessor> {

	public static final long SECONDS_BETWEEN_0001_AND_1970 = DvDate.DAYS_BETWEEN_0001_AND_1970 * 24L * 60L * 60L; //this is wrong!

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


	public DvDateTime(@Nullable List<ReferenceRange<DvDateTime>> otherReferenceRanges, @Nullable DvInterval<DvDateTime> normalRange, @Nullable CodePhrase normalStatus, @Nullable String magnitudeStatus, @Nullable DvDuration accuracy, TemporalAccessor value) {
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
	@RMProperty(value = "magnitude", computed = PropertyType.COMPUTED)
	public Long getMagnitude() {
		if (value == null) {
			return null;
		}
		if (value.query(TemporalQueries.zone()) != null) {
			return ZonedDateTime.from(value).toEpochSecond() + SECONDS_BETWEEN_0001_AND_1970;
		} else {
			return LocalDateTime.from(value).toEpochSecond(ZoneOffset.UTC) + SECONDS_BETWEEN_0001_AND_1970;
		}
	}

	public void setMagnitude(Long magnitude) {
		if (magnitude == null) {
			value = null;
		} else {
			value = LocalDateTime.ofEpochSecond(magnitude - SECONDS_BETWEEN_0001_AND_1970, 0, ZoneOffset.UTC);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		DvDateTime that = (DvDateTime) o;

		return Objects.equals(value, that.value);

	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), value);
	}
}
