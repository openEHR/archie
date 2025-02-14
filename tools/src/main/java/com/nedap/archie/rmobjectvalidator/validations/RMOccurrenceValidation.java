package com.nedap.archie.rmobjectvalidator.validations;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageIds;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated This class will be removed. Use the RMObjectValidator instead.
 */
@Deprecated
public class RMOccurrenceValidation {
    /**
     * @deprecated This method will be removed. Use the RMObjectValidator instead.
     */
    @Deprecated
    public static List<RMObjectValidationMessage> validate(MetaModel metaModel, List<RMObjectWithPath> rmObjects, String pathSoFar, CObject cobject) {
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
