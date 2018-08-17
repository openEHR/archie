package com.nedap.archie.rmobjectvalidator.validations;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageIds;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageType;

import java.util.ArrayList;
import java.util.List;

public class RMOccurenceValidation {
    private final List<RMObjectWithPath> rmObjects;
    private final String pathSoFar;
    private final CObject cobject;

    public RMOccurenceValidation(List<RMObjectWithPath> rmObjects, String pathSoFar, CObject cobject) {
        this.rmObjects = rmObjects;
        this.pathSoFar = pathSoFar;
        this.cobject = cobject;
    }

    public List<RMObjectValidationMessage> validate() {
        if (cobject.getOccurrences() != null) {
            MultiplicityInterval occurrences = cobject.getOccurrences();
            if (!occurrences.has(rmObjects.size())) {
                String message = RMObjectValidationMessageIds.rm_OCCURRENCE_MISMATCH.getMessage(rmObjects.size(), occurrences.toString());
                RMObjectValidationMessageType messageType = occurrences.isMandatory() ? RMObjectValidationMessageType.REQUIRED : RMObjectValidationMessageType.DEFAULT;
                return Lists.newArrayList(new RMObjectValidationMessage(cobject, pathSoFar, message, messageType));
            }
        }
        return new ArrayList<>();
    }
}
