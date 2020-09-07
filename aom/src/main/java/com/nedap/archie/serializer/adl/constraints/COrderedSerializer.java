package com.nedap.archie.serializer.adl.constraints;

import com.nedap.archie.aom.primitives.COrdered;
import com.nedap.archie.base.Interval;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;

/**
 * @author markopi
 */
public abstract class COrderedSerializer<T extends COrdered<?>> extends ConstraintSerializer<T> {
    public COrderedSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

    @Override
    public final void serialize(T cobj) {
        int original = builder.mark();

        serializeBefore(cobj);
        serializeConstraintIntervals(cobj);
        serializeAssumedValue(cobj);

        if (original == builder.mark()) {
            builder.append("*");//TODO: this is deprecated ADL. Should be fixed, but hard to check, so left in for now
            //because leaving it out would mean invalid instead of deprecated ADL
        }
        builder.clearMark();
    }

    protected void serializeBefore(T cobj) {
    }

    private void serializeAssumedValue(T cobj) {
        if (cobj.getAssumedValue() != null) {
            if (shouldIncludeAssumedValue(cobj)) {
                builder.append("; ").append(serializeConstraintValue(cobj.getAssumedValue()));
            }
        }
    }

    protected boolean shouldIncludeAssumedValue(T cobj) {
        return true;
    }

    private void serializeConstraintIntervals(T cobj) {
        if (!cobj.getConstraint().isEmpty()) {
            boolean first = true;
            for (Interval<?> interval : cobj.getConstraint()) {
                if (!first) {
                    builder.append(", ");
                }
                if (isSingleValueInterval(interval)) {
                    builder.append(serializeConstraintValue(interval.getLower()));
                } else {
                    builder.append(serializeInterval(interval));
                }
                first = false;
            }
        }
    }

    private String serializeInterval(Interval interval) {

        if (interval.isLowerUnbounded()) {
            return "|" + (interval.isUpperIncluded() ? "<=" : "<") + serializeConstraintValue(interval.getUpper()) + "|";
        }
        if (interval.isUpperUnbounded()) {
            return "|" + (interval.isLowerIncluded() ? ">=" : ">") + serializeConstraintValue(interval.getLower()) + "|";
        }

        if (interval.getLower() != null && interval.getUpper() != null && interval.getLower() == interval.getUpper()) {
            return serializeConstraintValue(interval.getLower());
        }
        StringBuilder result = new StringBuilder();
        result.append("|");
        if (!interval.isLowerIncluded()) {
            result.append(">");
        }
        result.append(serializeConstraintValue(interval.getLower()));
        result.append("..");
        if (!interval.isUpperIncluded()) {
            result.append("<");
        }
        result.append(serializeConstraintValue(interval.getUpper()));
        result.append("|");
        return result.toString();
    }

    protected String serializeConstraintValue(Object value) {
        return value.toString();
    }

    private boolean isSingleValueInterval(Interval<?> interval) {
        //In case we know interval is a of a discrete type, such as integers, this can be made to work in case !upperIncluded and !lowerIncluded
        //but it's fine without. There is a separate solution for MultiplicityInterval and in case this returns false the
        //only consequence is a slightly less intuitive serialization
        return interval.getLower() != null &&
                interval.isUpperIncluded() &&
                interval.isLowerIncluded() &&
                !interval.isLowerUnbounded() &&
                !interval.isUpperUnbounded() &&
                interval.getLower().equals(interval.getUpper());
    }

    @Override
    public boolean isEmpty(T object) {
        return object.getConstraint() == null || object.getConstraint().isEmpty();
    }
}
