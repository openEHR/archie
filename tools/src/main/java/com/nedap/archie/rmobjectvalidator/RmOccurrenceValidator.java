package com.nedap.archie.rmobjectvalidator;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rminfo.MetaModel;

import java.util.ArrayList;
import java.util.List;

class RmOccurrenceValidator {
    List<RMObjectValidationMessage> validate(MetaModel metaModel, List<RMObjectWithPath> rmObjects, String pathSoFar, CObject cobject) {
        if(cobject != null) {
            MultiplicityInterval occurrences = cobject.effectiveOccurrences(metaModel::referenceModelPropMultiplicity);
            if (occurrences != null && !occurrences.has(rmObjects.size())) {
                String message = RMObjectValidationMessageIds.rm_OCCURRENCE_MISMATCH.getMessage(rmObjects.size(), occurrences.toString());
                RMObjectValidationMessageType messageType = occurrences.isMandatory() ? RMObjectValidationMessageType.REQUIRED : RMObjectValidationMessageType.DEFAULT;
                return Lists.newArrayList(new RMObjectValidationMessage(cobject, pathSoFar, message, messageType));
            }

        }

        return new ArrayList<>();
    }
}
