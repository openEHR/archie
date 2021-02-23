package com.nedap.archie.aom.primitives;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.base.Interval;
import com.nedap.archie.rminfo.ModelInfoLookup;

import javax.xml.bind.annotation.XmlType;
import java.util.function.BiFunction;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_ORDERED")
public abstract class COrdered<T> extends CPrimitiveObject<Interval<T>, T> {

    @Override
    public boolean isValidValue(T value) {
        if(getConstraint().isEmpty()) {
            return true;
        }
        for(Interval<T> constraint:getConstraint()) {
            if(constraint.has(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean cConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypesConformant) {
        if(!super.cConformsTo(other, rmTypesConformant)) {
            return false;
        }
        //now guaranteed to be the same class

        COrdered<?> otherOrdered = (COrdered) other;
        if(otherOrdered.getConstraint().isEmpty()) {
            return true;
        }


        for(Interval<T> constraint:getConstraint()) {
            boolean found = false;
            for(Interval otherConstraint:otherOrdered.getConstraint()) {
                if(otherConstraint.contains(constraint)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                return false;
            }
        }
        return true;
    }
}
