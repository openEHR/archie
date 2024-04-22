package org.openehr.rm.datavalues.timespecification;

import org.openehr.rm.datavalues.DataValue;
import org.openehr.rm.datavalues.encapsulated.DvParsable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_TIME_SPECIFICATION")
public abstract class DvTimeSpecification extends DataValue {

    private DvParsable value;

	public DvTimeSpecification() {
	}

	public DvTimeSpecification(DvParsable value) {
		this.value = value;
	}

    public DvParsable getValue() {
        return value;
    }

    public void setValue(DvParsable value) {
        this.value = value;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DvTimeSpecification that = (DvTimeSpecification) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
