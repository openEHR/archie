package org.openehr.bmm.v2.persistence.validation;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import static org.junit.Assert.assertEquals;

public class PropertyValidatorTest extends AbstractSchemaValidationsTest {

    @Test
    public void ancestorEmpty() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("single_property_type_not_found.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_SINGLE_PROPERTY_TYPE_NOT_FOUND, BmmMessageIds.EC_SINGLE_PROPERTY_TYPE_NOT_FOUND), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void overridenPropertyNonConformance() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("overridden_property_non_conformance.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_OVERRIDDEN_PROPERTY_DOES_NOT_CONFORM), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void containerTypeEmpty() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("container_target_type_empty.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_CONTAINER_PROPERTY_TARGET_TYPE_NOT_DEFINED), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void containerTypeNotFound() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("container_type_not_found.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_CONTAINER_TYPE_NOT_FOUND), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void containerTargetTypeNotFound() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("container_target_type_not_found.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_CONTAINER_PROPERTY_TARGET_TYPE_NOT_FOUND), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void genericContainerPropertyNotFound() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("generic_container_property_not_found.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_GENERIC_PROPERTY_TYPE_PARAMETER_NOT_FOUND), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void singleOpenPropertyTypeNotFound() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("single_open_property_type_not_found.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_SINGLE_OPEN_PARAMETER_NOT_FOUND), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void genericRootTypeNotFound() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("generic_root_type_not_found.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_GENERIC_PROPERTY_ROOT_TYPE_NOT_FOUND), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void genericPropertyTypeDefUndefined() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("generic_property_type_def_undefined.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_GENERIC_PROPERTY_TYPE_DEF_UNDEFINED), validationResult.getLogger().getErrorCodes());
    }

    @Test
    public void genericParameterNotFound() throws Exception {
        BmmValidationResult validationResult = parseAndConvert("generic_parameter_not_found.bmm");
        assertEquals(Lists.newArrayList(BmmMessageIds.EC_GENERIC_PARAMETER_NOT_FOUND), validationResult.getLogger().getErrorCodes());
    }
}
