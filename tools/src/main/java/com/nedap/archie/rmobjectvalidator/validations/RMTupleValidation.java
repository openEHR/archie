package com.nedap.archie.rmobjectvalidator.validations;

import com.nedap.archie.aom.*;
import com.nedap.archie.query.RMObjectAttributes;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageIds;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated This class will be removed. Use the RMObjectValidator instead.
 */
@Deprecated
public class RMTupleValidation {
    /**
     * @deprecated This method will be removed. Use the RMObjectValidator instead.
     */
    @Deprecated
    public static List<RMObjectValidationMessage> validate(ModelInfoLookup lookup, CObject cobject, String pathSoFar, List<RMObjectWithPath> rmObjects, CAttributeTuple tuple) {
        List<RMObjectValidationMessage> result = new ArrayList<>();
        if (rmObjects.size() != 1) {
            String message = RMObjectValidationMessageIds.rm_TUPLE_CONSTRAINT.getMessage(cobject.toString(), rmObjects.toString());
            result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
            return result;
        }
        Object rmObject = rmObjects.get(0).getObject();
        if (!tuple.isValid(lookup, rmObject)) {
            if(tuple.getTuples().size() == 1) {
                // Try to make useful validation messages
                result.addAll(validateSingleTuple(lookup, pathSoFar, rmObject, tuple));
            }

            if(result.isEmpty()) {
                // Fall back to generic validation message
                String message = RMObjectValidationMessageIds.rm_TUPLE_MISMATCH.getMessage(tuple.toString());
                result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
            }
        }
        return result;
    }

    /**
     * Validate a CAttributeTuple with a single tuple.
     *
     * This will check each attribute in the tuple individually to get more specific validation messages.
     */
    private static List<RMObjectValidationMessage> validateSingleTuple(ModelInfoLookup lookup, String pathSoFar, Object rmObject, CAttributeTuple attributeTuple) {
        List<RMObjectValidationMessage> result = new ArrayList<>();

        CPrimitiveTuple tuple = attributeTuple.getTuples().get(0);

        int index = 0;
        for(CAttribute attribute:attributeTuple.getMembers()) {
            String attributeName = attribute.getRmAttributeName();
            CPrimitiveObject<?, ?> cPrimitiveObject = tuple.getMembers().get(index);
            Object value = RMObjectAttributes.getAttributeValueFromRMObject(rmObject, attributeName, lookup);
            String path = pathSoFar + "/" + attributeName + "[" + cPrimitiveObject.getNodeId() + "]";

            result.addAll(RMPrimitiveObjectValidation.validate_inner(lookup, value, path, cPrimitiveObject));

            index++;
        }

        return result;
    }
}
