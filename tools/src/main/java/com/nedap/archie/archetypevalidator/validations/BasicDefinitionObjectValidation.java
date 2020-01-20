package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.primitives.COrdered;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import com.nedap.archie.base.Interval;
import org.openehr.utils.message.I18n;

/**
 * Contains validation for basic object validity. Many of these checks are also included in the ADL grammar, but\
 * of course not every AOM object comes from ADL :)
 */
public class BasicDefinitionObjectValidation extends ValidatingVisitor {

    protected void validate(CObject cObject) {
        if(cObject.getOccurrences() != null) {
            validateOccurrences(cObject);
        }
        if(cObject instanceof COrdered) {
            validateCOrdered((COrdered) cObject);
        }
    }

    private void validateCOrdered(COrdered<?> cOrdered) {
        for(Interval interval:cOrdered.getConstraint()) {
            if(!isValidInterval(interval)) {
                this.addMessageWithPath(ErrorType.OTHER, cOrdered.path(), I18n.t("The constraint interval for this {0} has lower > upper, this is not allowed", cOrdered.getClass().getSimpleName()));
            }
        }
    }

    protected void validate(CAttribute cAttribute) {

        if(cAttribute.getCardinality() != null) {
            if (cAttribute.getCardinality().getInterval() == null) {
                this.addMessageWithPath(ErrorType.OTHER, cAttribute.path(), I18n.t("Cardinality must have an interval present, but it was null"));
            } else if(!isValidInterval(cAttribute.getCardinality().getInterval())) {
                this.addMessageWithPath(ErrorType.OTHER, cAttribute.path(), I18n.t("The attribute cardinality interval has lower > upper, this is not allowed"));
            }
        }

    }



    private void validateOccurrences(CObject cObject) {
        if(!isValidInterval(cObject.getOccurrences())) {
            this.addMessageWithPath(ErrorType.OTHER, cObject.path(), I18n.t("The occurrences interval has lower > upper, this is not allowed"));
        }
    }

    private boolean isValidInterval(Interval<? extends Comparable> interval) {
        if(interval.getUpper() != null && interval.getLower() != null &&
            !interval.isLowerUnbounded() &&
            !interval.isUpperUnbounded()){
            if(interval.getComparableUpper().compareTo(interval.getComparableLower()) < 0){
                return false;
            }
        }
        return true;
    }
}
