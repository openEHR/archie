package com.nedap.archie.rm.support.identification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rminfo.RMProperty;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
public class VersionTreeId extends RMObject {

    public static final String BRANCH_VERSION = ".*[1-9][0-9]*[.].*[0-9].*[.].*[0-9].*";
    public static final String SIMPLE_VERSION = "[1-9][0-9]*";

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
    public Boolean isBranch() {
        if (value == null) {
            return null;
        }

        if (value.matches(BRANCH_VERSION))
            return true;
        else {
            // checking if it is just a 1-part identifier or invalid
            if (value.matches(SIMPLE_VERSION))
                return false;
            else    // TODO: what kind of exception should be thrown here?
                throw new IllegalArgumentException("Invalid object. Version needs to be 1- or 3-part numeric identifier.");
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
        } else
            return value;
    }

    @JsonIgnore
    @XmlTransient
    public String getBranchNumber() {
        if (value == null || isBranch().equals(false)) {
            return null;
        }

        String branch = value.substring(value.indexOf(".")+1);
        return branch.substring(0, branch.indexOf("."));
    }

    @JsonIgnore
    @XmlTransient
    public String getBranchVersion() {
        if (value == null || isBranch().equals(false)) {
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
}
