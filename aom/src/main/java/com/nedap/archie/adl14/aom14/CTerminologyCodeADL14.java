package com.nedap.archie.adl14.aom14;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.utils.ConformanceCheckResult;
import com.nedap.archie.base.terminology.TerminologyCode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Terminology code constraint as parsed from ADL 1.4, which can contain multiple codes. Only used during the
 * ADL 1.4 to ADL 2 conversion, where it is converted into a {@link com.nedap.archie.aom.primitives.CTerminologyCode}.
 */
public class CTerminologyCodeADL14 extends CPrimitiveObject<List<String>, TerminologyCode> {

    @Nullable
    private TerminologyCode assumedValue;
    private List<String> constraint = new ArrayList<>();

    @Override
    public TerminologyCode getAssumedValue() {
        return assumedValue;
    }

    @Override
    public void setAssumedValue(TerminologyCode assumedValue) {
        this.assumedValue = assumedValue;
    }

    @Override
    public List<String> getConstraint() {
        return this.constraint;
    }

    @Override
    public List<?> getConstraintAsList() {
        return this.constraint;
    }

    public void setConstraint(List<String> constraint) {
        this.constraint = constraint;
    }

    public void addConstraint(String constraint) {
        this.constraint.add(constraint);
    }

    @Override
    public ConformanceCheckResult cConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypesConformant) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{[");
        boolean first = true;
        for(String constraint:getConstraint()) {
            if(!first) {
                result.append(", ");
            }
            first = false;
            result.append(constraint.toString());
        }
        result.append("]}");
        return result.toString();
    }

    @Override
    public String getRmTypeName() {
        if (getParent() == null || getParent().getRmAttributeName() == null) {
            return "terminology_code";
        }
        switch (getParent().getRmAttributeName()) {
            case "defining_code":
                return "CODE_PHRASE";
            case "symbol":
                return "DV_CODED_TEXT";
            default:
                return "terminology_code";
        }
    }
}
