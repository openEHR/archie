package org.openehr.bmm.v2.persistence.validation;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import static org.junit.Assert.assertEquals;

public class ClassesValidatorTest extends AbstractSchemaValidationsTest {

    @Test
    public void ancestorEmpty() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("ancestor_name_empty.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_ANCESTOR_NAME_EMPTY), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void ancestorDoesNotExist() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("ancestor_doesnt_exist.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_ANCESTOR_DOES_NOT_EXIST), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void ancestorDefDoesNotExist() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("ancestor_def_doesnt_exist.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_ANCESTOR_DOES_NOT_EXIST), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void genericParameterTypeMissing() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("generic_parameter_type_missing.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_GENERIC_PARAMETER_CONSTRAINT_DOES_NOT_EXIST), validationResult.getLogger().getErrorCodes());
    }
}
