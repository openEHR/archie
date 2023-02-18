package com.nedap.archie.rm.support.identification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TERMINOLOGY_ID")
public class TerminologyId extends ObjectId {

    public TerminologyId() {

    }

    public TerminologyId(String terminologyId, String terminologyVersion) {
        if (terminologyVersion == null || terminologyVersion.isEmpty()) {
            super.setValue(terminologyId);
        } else {
            super.setValue(terminologyId + "(" + terminologyVersion + ")");
        }
    }

    public TerminologyId(String terminologyId) {
        this(terminologyId, null);
    }

    public String name() {
        String[] valueParts = getValueAsParts();
        if (valueParts != null && valueParts.length >= 1) {
            return valueParts[0];
        } else {
            return null;
        }
    }

    public String versionId() {
        String[] valueParts = getValueAsParts();
        if (valueParts != null && valueParts.length >= 2) {
            return valueParts[1];
        } else {
            return null;
        }
    }

    private String[] getValueAsParts() {
        String value = getValue();
        if (value != null) {
            return value.split("[()]");
        } else {
            return null;
        }
    }

}
