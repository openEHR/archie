package com.nedap.archie.rm.datavalues;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
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
}
