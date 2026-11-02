package org.openehr.bmm.v2.persistence.validation;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicSchemaValidationsTest extends AbstractSchemaValidationsTest {

    @Test
    public void duplicateClass() throws Exception {
        BmmValidationResult schema = parseAndConvert("duplicate_class.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_DUPLICATE_CLASS_IN_PACKAGES), schema.getLogger().getErrorCodes());
    }

    @Test
    public void classNotInPackages() throws Exception {
        BmmValidationResult schema = parseAndConvert("class_not_in_packages.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_CLASS_NOT_DECLARED_IN_PACKAGES), schema.getLogger().getErrorCodes());
    }

    @Test
    public void archetypeParentClassNotDefined() throws Exception {
        BmmValidationResult schema = parseModifyThenConvert("valid.bmm", (s) -> s.setArchetypeParentClass("unknownClass"));
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_ARCHETYPE_PARENT_CLASS_UNDEFINED), schema.getLogger().getErrorCodes());
    }

    @Test
    public void rmReleaseInvalid() throws Exception {
        BmmValidationResult schema = parseModifyThenConvert("valid.bmm", (s) -> s.setRmRelease("not_a_version"));
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_RM_RELEASE_INVALID), schema.getLogger().getErrorCodes());

    }

    @Test
    public void incompatibleBmmVersion() throws Exception {
        BmmValidationResult schema = parseModifyThenConvert("valid.bmm", (s) -> s.setBmmVersion("3.0"));
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_INCOMPATIBLE_BMM_VERSION), schema.getLogger().getErrorCodes());
    }

}
