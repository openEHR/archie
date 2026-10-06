package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rminfo.MetaModel;

import com.nedap.archie.rmobjectvalidator.RMObjectValidatingProcessor.ValidationMessages;
import java.util.List;

class RmOccurrenceValidator {
    void validate(ValidationMessages result, MetaModel metaModel, List<RMObjectWithPath> rmObjects, ValidationPath pathSoFar, CObject cobject) {
        if(cobject != null) {
            MultiplicityInterval occurrences = cobject.effectiveOccurrences(metaModel::referenceModelPropMultiplicity);
            if (occurrences != null && !occurrences.has(rmObjects.size())) {
                String message = RMObjectValidationMessageIds.rm_OCCURRENCE_MISMATCH.getMessage(rmObjects.size(), occurrences.toString());
                RMObjectValidationMessageType messageType = occurrences.isMandatory() ? RMObjectValidationMessageType.REQUIRED : RMObjectValidationMessageType.DEFAULT;
                result.add(new RMObjectValidationMessage(cobject, pathSoFar.toString(), message, messageType));
            }

        }
    }
}
