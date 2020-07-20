package org.openehr.bmm.v2.validation.validators;

import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidation;
import org.openehr.bmm.v2.validation.BmmValidationResult;
import org.openehr.utils.message.MessageLogger;

public class BmmVersionValidation implements BmmValidation {
    @Override
    public void validate(BmmValidationResult validationResult, BmmRepository repository, MessageLogger logger, PBmmSchema schema) {
        if (!BmmDefinitions.isBmmVersionCompatible(schema.getBmmVersion())) {
            logger.addError(BmmMessageIds.EC_INCOMPATIBLE_BMM_VERSION,
                    schema.getSchemaId(),
                    schema.getBmmVersion(),
                    BmmDefinitions.BMM_INTERNAL_VERSION);
        }
    }
}
