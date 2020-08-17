package org.openehr.bmm.v2.validation.validators;

import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.persistence.BmmIncludeSpec;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidation;
import org.openehr.bmm.v2.validation.BmmValidationResult;
import org.openehr.utils.message.MessageLogger;

public class IncludesValidation implements BmmValidation {
    @Override
    public void validate(BmmValidationResult validationResult, BmmRepository repository, MessageLogger logger, PBmmSchema schema) {
        if(schema.getIncludes() != null) {
            for(BmmIncludeSpec includeSpec:schema.getIncludes().values()) {
                if (!repository.containsPersistentSchema(includeSpec.getId())) {
                    logger.addError(BmmMessageIds.EC_INCLUDE_NOT_FOUND, schema.getSchemaId(), includeSpec.getId());
                }
            }
        }
    }
}
