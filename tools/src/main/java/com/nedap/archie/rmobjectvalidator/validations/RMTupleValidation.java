package com.nedap.archie.rmobjectvalidator.validations;

import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageIds;

import java.util.ArrayList;
import java.util.List;

public class RMTupleValidation {
    public static List<RMObjectValidationMessage> validate(ModelInfoLookup lookup, CObject cobject, String pathSoFar, List<RMObjectWithPath> rmObjects, CAttributeTuple tuple) {
        List<RMObjectValidationMessage> result = new ArrayList<>();
        if (rmObjects.size() != 1) {
            String message = RMObjectValidationMessageIds.rm_TUPLE_CONSTRAINT.getMessage(cobject.toString(), rmObjects.toString());
            result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
            return result;
        }
        Object rmObject = rmObjects.get(0).getObject();
        if (!tuple.isValid(lookup, rmObject)) {
            String message = RMObjectValidationMessageIds.rm_TUPLE_MISMATCH.getMessage(tuple.toString());
            result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
        }
        return result;
    }
}
