package org.openehr.rm.datavalues;

import com.nedap.archie.rminfo.Invariant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_URI", propOrder = {
        "value"
})
public class DvURI extends DataValue implements SingleValuedDataValue<URI> {

    private URI value; //supposed to be a string, but this is better. Legal to change this with type replacements.


    public DvURI() {
    }

    public DvURI(URI value) {
        this.value = value;
    }

    /**
     * Creates a DVURI from a URI String representation
     *
     * @param uri
     */
    public DvURI(String uri) {
        this.value = URI.create(uri);
    }

    @Override
    public URI getValue() {
        return value;
    }

    @Override
    public void setValue(URI value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DvURI dvURI = (DvURI) o;
        return Objects.equals(value, dvURI.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Invariant(value="Value_valid")
    public boolean valueValid() {
        return value == null || !value.toString().isEmpty();
    }
}
