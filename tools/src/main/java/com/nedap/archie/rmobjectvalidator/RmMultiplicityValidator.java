package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.rmobjectvalidator.RMObjectValidatingProcessor.RMObjectValidationMessageSink;
import java.util.Collection;

class RmMultiplicityValidator {
    void validate(RMObjectValidationMessageSink result, CAttribute attribute, ValidationPath pathSoFar, Object attributeValue) {
        if (attributeValue instanceof Collection<?> collectionValue) {
            //validate multiplicity
            Cardinality cardinality = attribute.getCardinality();
            if (cardinality != null) {
                if (!cardinality.getInterval().has(collectionValue.size())) {
                    String message = RMObjectValidationMessageIds.rm_CARDINALITY_MISMATCH.getMessage(cardinality.getInterval().toString());
                    result.addMessage(new RMObjectValidationMessage(attribute, pathSoFar.toString(), message, RMObjectValidationMessageType.CARDINALITY_MISMATCH));
                }
            }
        } else {
            MultiplicityInterval existence = attribute.getExistence();
            if (existence != null) {
                if (!existence.has(attributeValue == null ? 0 : 1)) {
                    String message = RMObjectValidationMessageIds.rm_EXISTENCE_MISMATCH.getMessage(attribute.getRmAttributeName(),
                            attribute.getParent() == null ? "Unknown type" : attribute.getParent().getRmTypeName(), existence.toString());

                    result.addMessage(new RMObjectValidationMessage(attribute, pathSoFar.toString(), message, RMObjectValidationMessageType.REQUIRED));
                }
            }
        }
    }
}
