package com.nedap.archie.aom.primitives;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.utils.ConformanceCheckResult;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.base.Interval;
import com.nedap.archie.rminfo.ModelInfoLookup;
import org.openehr.utils.message.I18n;

import java.util.function.BiFunction;

/**
 * Created by pieter.bos on 15/10/15.
 */
public abstract class COrdered<T> extends CPrimitiveObject<Interval<T>, T> {

    @Override
    @Deprecated
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
    public ConformanceCheckResult cConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypesConformant) {
        ConformanceCheckResult superResult = super.cConformsTo(other, rmTypesConformant);
        if (!superResult.doesConform()) {
            return superResult;
        }
        //now guaranteed to be the same class

        @SuppressWarnings("unchecked")
        COrdered<T> otherOrdered = (COrdered<T>) other;
        if(otherOrdered.getConstraint().isEmpty()) {
            return ConformanceCheckResult.conforms();
        }


        for(Interval<T> constraint:getConstraint()) {
            boolean found = false;
            for(Interval<T> otherConstraint:otherOrdered.getConstraint()) {
                if(otherConstraint.contains(constraint)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("Parent constraint contains no interval that fully contains {0}", constraint));
            }
        }
        return ConformanceCheckResult.conforms();
    }
}
