package com.nedap.archie.rm.datavalues;

import com.nedap.archie.definitions.OpenEhrDefinitions;
import com.nedap.archie.rminfo.Invariant;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.net.URI;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DV_EHR_URI")
public class DvEHRURI extends DvURI {

    public DvEHRURI() {
    }

    public DvEHRURI(URI value) {
        super(value);
    }


    /**
     * Creates a DvEHRURI from a URI String representation
     *
     * @param uri
     */
    public DvEHRURI(String uri) {
        super(uri);
    }

    @Invariant("Scheme_valid")
    public boolean schemeValid() {
        return getValue() == null || (getValue().getScheme() != null && getValue().getScheme().equalsIgnoreCase(OpenEhrDefinitions.EHR_SCHEME));
    }
}
