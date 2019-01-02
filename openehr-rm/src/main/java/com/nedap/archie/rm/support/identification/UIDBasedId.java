package com.nedap.archie.rm.support.identification;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UID_BASED_ID")
public abstract class UIDBasedId extends ObjectId {

    public static final String UUID_REGEXP = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";

    public UID getRoot() {
        String value = getValue();
        if(value == null) {
            return null;
        }
        int index = value.indexOf("::");
        String resultString = null;
        if(index < 0) {
            resultString = value;
        } else {
            resultString = value.substring(index);
        }
        if(resultString.matches(UUID_REGEXP)) {
            UID result = new UUID();
            result.setValue(resultString);
            return result;
        } else if (resultString.matches("([0-9]\\.?)+")) {
            IsoOID result = new IsoOID();
            result.setValue(resultString);
            return result;
        } else {
            InternetId result = new InternetId();
            result.setValue(resultString);
            return result;
        }
    }

    @Nullable
    public String getExtension() {
        String value = getValue();
        if(value == null) {
            return null;
        }
        int index = value.indexOf("::");
        if(index < 0) {
            return "";
        } else {
            return value.substring(index + 2);
        }
    }

}
