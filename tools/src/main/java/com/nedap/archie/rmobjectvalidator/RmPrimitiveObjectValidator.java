package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.query.RMObjectWithPath;
import org.openehr.utils.message.I18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RmPrimitiveObjectValidator {
    private final ValidationHelper validationHelper;

    public RmPrimitiveObjectValidator(ValidationHelper validationHelper) {
        this.validationHelper = validationHelper;
    }

    public List<RMObjectValidationMessage> validate(List<RMObjectWithPath> rmObjects, String pathSoFar, CPrimitiveObject<?, ?> cobject) {
        if(cobject == null) {
            return new ArrayList<>();
        }
        if (cobject.getSocParent() != null) {
            //validate the tuple, not the primitive object directly
            return Collections.emptyList();
        }
        if (rmObjects.size() != 1) {
            List<RMObjectValidationMessage> result = new ArrayList<>();
            result.add(createValidationMessage(rmObjects, pathSoFar, cobject));
            return result;
        }
        Object rmObject = rmObjects.get(0).getObject();
        return validate_inner(rmObject, pathSoFar, cobject);
    }

    List<RMObjectValidationMessage> validate_inner(Object rmObject, String pathSoFar, CPrimitiveObject<?, ?> cobject) {
        List<RMObjectValidationMessage> result = new ArrayList<>();
        if (!validationHelper.isValidValue(cobject, rmObject)) {
            result.add(createValidationMessage(rmObject, pathSoFar, cobject));
        }
        return result;
    }

    private RMObjectValidationMessage createValidationMessage(Object value, String pathSoFar, CPrimitiveObject<?, ?> cobject) {
        List<?> constraint = cobject.getConstraint();
        String message;

        if(constraint.size() == 1) {
            String constraintStr = ConstraintToStringUtil.constraintElementToString(constraint.get(0));
            message = RMObjectValidationMessageIds.rm_INVALID_FOR_CONSTRAINT.getMessage(getValueString(value), constraintStr);
        } else {
            String constraintStr = ConstraintToStringUtil.constraintListToString(constraint);
            message = RMObjectValidationMessageIds.rm_INVALID_FOR_CONSTRAINT_MULTIPLE.getMessage(getValueString(value)) + "\n" +
                    constraintStr;
        }
        return new RMObjectValidationMessage(cobject, pathSoFar, message);
    }

    private String getValueString(Object value) {
        if(value == null) {
            return I18n.t("empty");
        }

        return (value instanceof String) ? "\"" + value.toString() + "\"" : value.toString();
    }
}
