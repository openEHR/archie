package com.nedap.archie.rmobjectvalidator.validations;

import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageIds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RMTupleValidation {
    private final ModelInfoLookup lookup;
    private final CObject cobject;
    private final String pathSoFar;
    private final List<RMObjectWithPath> rmObjects;
    private final CAttributeTuple tuple;

    public RMTupleValidation(ModelInfoLookup lookup, CObject cobject, String pathSoFar, List<RMObjectWithPath> rmObjects, CAttributeTuple tuple) {
        this.lookup = lookup;
        this.cobject = cobject;
        this.pathSoFar = pathSoFar;
        this.rmObjects = rmObjects;
        this.tuple = tuple;
    }

    public Collection<? extends RMObjectValidationMessage> validate() {
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
