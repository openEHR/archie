package org.openehr.bmm.v2.validation;

import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.utils.message.MessageLogger;

/**
 * A single BMM Validation
 */
public interface BmmValidation {

    void validate(BmmValidationResult validationResult, BmmRepository repository, MessageLogger logger, PBmmSchema schema);
}
