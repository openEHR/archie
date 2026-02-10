package org.openehr.bmm.v2.persistence.validation;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import static org.junit.Assert.assertEquals;

public class IncludesValidatorTest extends AbstractSchemaValidationsTest {

    @Test
    public void includeNotFound() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("include_not_found.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_INCLUDE_NOT_FOUND), validationResult.getLogger().getErrorCodes());
    }
}
