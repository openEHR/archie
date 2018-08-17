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

public class RMOccurrenceValidation {
    public static List<RMObjectValidationMessage> validate(List<RMObjectWithPath> rmObjects, String pathSoFar, CObject cobject) {
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
