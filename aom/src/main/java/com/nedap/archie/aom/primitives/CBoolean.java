package com.nedap.archie.aom.primitives;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.utils.ConformanceCheckResult;
import com.nedap.archie.archetypevalidator.ErrorType;
import org.openehr.utils.message.I18n;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
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
    @Nullable
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
    public ConformanceCheckResult cConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypeNamesConformant) {
        ConformanceCheckResult superResult = super.cConformsTo(other, rmTypeNamesConformant);
        if(!superResult.doesConform()) {
            return superResult;
        }
        //now guaranteed to be the same class

        CBoolean otherBoolean = (CBoolean) other;
        if(otherBoolean.constraint.isEmpty()) {
            return ConformanceCheckResult.conforms();
        }

        if(!(constraint.size() <= otherBoolean.constraint.size())) {
            return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("A specialised boolean constraint cannot include the same number of more constraints than its parent"));
        }

        for(Boolean constraint:constraint) {
            if(!otherBoolean.constraint.contains(constraint)) {
                return ConformanceCheckResult.fails(ErrorType.VPOV, I18n.t("A specialised boolean constraint cannot add a constraint not in its parent"));
            }
        }
        return ConformanceCheckResult.conforms();
    }
}
