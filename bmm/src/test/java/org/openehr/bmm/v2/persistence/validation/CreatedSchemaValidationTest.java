package org.openehr.bmm.v2.persistence.validation;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreatedSchemaValidationTest extends AbstractSchemaValidationsTest {

    @Test
    public void packageIllegalQualifiedName() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("package_illegal_qualified_name.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_ILLEGAL_QUALIFIED_PACKAGE_NAME), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void illegalSiblingPackageName() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("illegal_sibling_packages.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_ILLEGAL_TOP_LEVEL_SIBLING_PACKAGES, BmmMessageIds.EC_ILLEGAL_TOP_LEVEL_SIBLING_PACKAGES), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void packageClassNameEmpty() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("package_class_name_empty.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.ec_BMM_class_name_empty), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void classNotInDefinition() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("class_not_in_definition.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.ec_BMM_class_not_in_definitions), validationResult.getLogger().getErrorCodes());
    }
}
