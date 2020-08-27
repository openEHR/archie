package com.nedap.archie.aom.primitives;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="C_BOOLEAN")
public class CBoolean extends CPrimitiveObject<Boolean, Boolean> {
    @XmlElement(name="assumed_value")
    private Boolean assumedValue;
    @Nullable
    private List<Boolean> constraint = new ArrayList<>();

    @Override
    public Boolean getAssumedValue() {
        return assumedValue;
    }

    @Override
    public void setAssumedValue(Boolean assumedValue) {
        this.assumedValue = assumedValue;
    }

    @Override
    public List<Boolean> getConstraint() {
        return constraint;
    }

    @Override
    public void setConstraint(List<Boolean> constraint) {
        this.constraint = constraint;

    }

    @Override
    public void addConstraint(Boolean constraint) {
        this.constraint.add(constraint);
    }

    @Override
    public boolean cConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypeNamesConformant) {
        if(!super.cConformsTo(other, rmTypeNamesConformant)) {
            return false;
        }
        //now guaranteed to be the same class

        CBoolean otherBoolean = (CBoolean) other;
        if(otherBoolean.constraint.isEmpty()) {
            return true;
        }

        if(!(constraint.size() <= otherBoolean.constraint.size())) {
            return false;
        }

        for(Boolean constraint:constraint) {
            if(!otherBoolean.constraint.contains(constraint)) {
                return false;
            }
        }
        return true;
    }
}
