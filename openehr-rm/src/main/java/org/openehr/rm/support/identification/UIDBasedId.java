package org.openehr.rm.support.identification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.nedap.archie.rminfo.Invariant;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UID_BASED_ID")
public abstract class UIDBasedId extends ObjectId {

    public static final String UUID_REGEXP = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";

    public UIDBasedId() {
    }

    public UIDBasedId(String value) {
        super(value);
    }

    @JsonIgnore
    @XmlTransient
    public UID getRoot() {
        String value = getValue();
        if (value == null) {
            return null;
        }
        int index = value.indexOf("::");
        String resultString = null;
        if (index < 0) {
            resultString = value;
        } else {
            resultString = value.substring(0, index);
        }
        if (resultString.matches(UUID_REGEXP)) {
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
    @JsonIgnore
    @XmlTransient
    public String getExtension() {
        String value = getValue();
        if (value == null) {
            return null;
        }
        int index = value.indexOf("::");
        if (index < 0) {
            return "";
        } else {
            return value.substring(index + 2);
        }
    }

    @JsonIgnore
    public boolean hasExtension() {
        return !Strings.isNullOrEmpty(getExtension());
    }

    //an absolutely useless invariant to check for data validity, so ignored
    @Invariant(value="Has_extension_valid", ignored=true)
    public boolean hasExtensionValid() {
        return Strings.isNullOrEmpty(getExtension()) ^ hasExtension();
    }

}
