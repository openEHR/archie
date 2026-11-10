package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.*;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rminfo.AttributeAccessor;
import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RmTupleValidator {
    private final AttributeAccessor attributeAccessor;
    private final ValidationHelper validationHelper;
    private final RmPrimitiveObjectValidator rmPrimitiveObjectValidator;

    RmTupleValidator(ModelInfoLookup lookup, ValidationHelper validationHelper, RmPrimitiveObjectValidator rmPrimitiveObjectValidator) {
        this.attributeAccessor = new AttributeAccessor(lookup);
        this.validationHelper = validationHelper;
        this.rmPrimitiveObjectValidator = rmPrimitiveObjectValidator;
    }

    List<RMObjectValidationMessage> validate(CObject cobject, ValidationPath pathSoFar, List<RMObjectWithPath> rmObjects, CAttributeTuple tuple) {
        if (rmObjects.size() != 1) {
            String message = RMObjectValidationMessageIds.rm_TUPLE_CONSTRAINT.getMessage(cobject.toString(), rmObjects.toString());
            return Collections.singletonList(new RMObjectValidationMessage(cobject, pathSoFar.toString(), message));
        }
        Object rmObject = rmObjects.get(0).getObject();
        if (!validationHelper.isValid(tuple, rmObject)) {
            if(tuple.getTuples().size() == 1) {
                // Try to make useful validation messages
                List<RMObjectValidationMessage> result = validateSingleTuple(pathSoFar, rmObject, tuple);
                if (result != null) {
                    return result;
                }
            }
            // Fall back to generic validation message
            String message = RMObjectValidationMessageIds.rm_TUPLE_MISMATCH.getMessage(tuple.toString());
            return Collections.singletonList(new RMObjectValidationMessage(cobject, pathSoFar.toString(), message));
        } else {
            return null;
        }
    }

    /**
     * Validate a CAttributeTuple with a single tuple.
     *
     * This will check each attribute in the tuple individually to get more specific validation messages.
     */
    private List<RMObjectValidationMessage> validateSingleTuple(ValidationPath pathSoFar, Object rmObject, CAttributeTuple attributeTuple) {
        List<RMObjectValidationMessage> result = null;

        CPrimitiveTuple tuple = attributeTuple.getTuples().get(0);

        int index = 0;
        for(CAttribute attribute:attributeTuple.getMembers()) {
            String attributeName = attribute.getRmAttributeName();
            CPrimitiveObject<?, ?> cPrimitiveObject = tuple.getMembers().get(index);
            Object value = attributeAccessor.getValue(rmObject, attributeName);
            ValidationPath path = pathSoFar.add(attributeName, cPrimitiveObject);

            List<RMObjectValidationMessage> messages = rmPrimitiveObjectValidator.validate_inner(value, path, cPrimitiveObject);
            if (messages != null) {
                if (result == null) {
                    result = new ArrayList<>(messages);
                } else {
                    result.addAll(messages);
                }
            }

            index++;
        }

        return result;
    }
}
