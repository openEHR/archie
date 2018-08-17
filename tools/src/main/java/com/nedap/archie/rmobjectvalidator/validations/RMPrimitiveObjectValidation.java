package com.nedap.archie.rmobjectvalidator.validations;

import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageIds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RMPrimitiveObjectValidation {
    private final ModelInfoLookup lookup;
    private final List<RMObjectWithPath> rmObjects;
    private final String pathSoFar;
    private final CPrimitiveObject cobject;

    public RMPrimitiveObjectValidation(ModelInfoLookup lookup, List<RMObjectWithPath> rmObjects, String pathSoFar, CPrimitiveObject cobject) {
        this.lookup = lookup;
        this.rmObjects = rmObjects;
        this.pathSoFar = pathSoFar;
        this.cobject = cobject;
    }

    public List<RMObjectValidationMessage> validate() {
        if (cobject.getSocParent() != null) {
            //validate the tuple, not the primitive object directly
            return Collections.emptyList();
        }
        List<RMObjectValidationMessage> result = new ArrayList<>();
        if (rmObjects.size() != 1) {
            String message = RMObjectValidationMessageIds.rm_PRIMITIVE_CONSTRAINT.getMessage(cobject.toString(), rmObjects.toString());
            result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
            return result;
        }
        Object rmObject = rmObjects.get(0).getObject();
        if (!cobject.isValidValue(lookup, rmObject)) {
            String message = RMObjectValidationMessageIds.rm_INVALID_FOR_CONSTRAINT.getMessage(cobject.toString(), rmObject.toString());
            result.add(new RMObjectValidationMessage(cobject, pathSoFar, message));
        }
        return result;
    }
}
