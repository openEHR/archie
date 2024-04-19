package org.openehr.rm.support.identification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.nedap.archie.base.RMObject;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rminfo.RMProperty;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by pieter.bos on 08/07/16.
 */
public class VersionTreeId extends RMObject {

    public static final Pattern BRANCH_VERSION = Pattern.compile("[1-9][0-9]*[.][0-9]+[.][0-9]+");
    public static final Pattern SIMPLE_VERSION = Pattern.compile("[1-9][0-9]*");

    private String value;

    public VersionTreeId() {
    }

    public VersionTreeId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    @XmlTransient
    @RMProperty("is_branch")
    public boolean isBranch() {
        if (value == null) {
            return false;
        }

        if (BRANCH_VERSION.matcher(value).matches()) {
            return true;
        } else {
            // checking if it is just a 1-part identifier or invalid
            if (SIMPLE_VERSION.matcher(value).matches()) {
                return false;
            } else {
                throw new IllegalArgumentException("Invalid object. Version needs to be 1- or 3-part numeric identifier.");
            }
        }
    }

    @JsonIgnore
    @XmlTransient
    public String getTrunkVersion() {
        if (value == null) {
            return null;
        }

        if (isBranch()) {
            int dot = value.indexOf(".");
            return value.substring(0, dot);
        } else {
            return value;
        }
    }

    @JsonIgnore
    @XmlTransient
    public String getBranchNumber() {
        if (!isBranch()) {
            return null;
        }

        String branch = value.substring(value.indexOf(".")+1);
        return branch.substring(0, branch.indexOf("."));
    }

    @JsonIgnore
    @XmlTransient
    public String getBranchVersion() {
        if (!isBranch()) {
            return null;
        }

        String branch = value.substring(value.indexOf(".")+1);
        return branch.substring(branch.indexOf(".")+1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersionTreeId that = (VersionTreeId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Invariant("Value_valid")
    public boolean valueValid() {
        return !Strings.isNullOrEmpty(value);
    }

    @Invariant("Value_format_valid") //not a standard invariant, but don't care - this is a much better validation
    public boolean trunkVersionValid() {
        return Strings.isNullOrEmpty(value) || SIMPLE_VERSION.matcher(value).matches() || BRANCH_VERSION.matcher(value).matches();
    }

}
