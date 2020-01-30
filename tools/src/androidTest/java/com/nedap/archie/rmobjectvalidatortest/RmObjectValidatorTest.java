package com.nedap.archie.rmobjectvalidatortest;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.archetypevalidator.ArchetypeValidatorTest;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.DvProportion;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageType;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RmObjectValidatorTest {

    private ADLParser parser = new ADLParser();
    TestUtil testUtil = new TestUtil();
    RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());

    @Test
    public void requiredAttributesShouldBePresent() throws Exception {
        Archetype archetype = parse("/adl2-tests/rmobjectvalidity/openEHR-TEST_RMOBJECT-ELEMENT.element_with_required_attributes.v1.0.0.adls");

        Element element = (Element) testUtil.constructEmptyRMObject(archetype.getDefinition());
        DvProportion dvProportion = (DvProportion) element.getValue();
        dvProportion.setDenominator(4D);
        dvProportion.setType(3L);
        List<RMObjectValidationMessage> validationMessages = validator.validate(archetype, element);
        assertEquals("There should be 1 errors", 1, validationMessages.size());
        assertEquals("There should be a validation message about the numerator", "Attribute numerator of class DV_PROPORTION does not match existence 1..1", validationMessages.get(0).getMessage());

        dvProportion.setNumerator(2D);
        validationMessages = validator.validate(archetype, element);
        assertEquals("There should be 0 errors", 0, validationMessages.size());
    }

    @Test
    public void testEmptyElementWithoutArchetype() {
        Element element = new Element();
        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = rmObjectValidator.validate(element);
        assertEquals(2, messages.size());
        for(RMObjectValidationMessage message:messages) {
            assertTrue(Sets.newHashSet("/name", "/archetype_node_id").contains(message.getPath()));
            assertEquals(RMObjectValidationMessageType.REQUIRED, message.getType());
        }
    }

    @Test
    public void testClusterWithoutArchetype() {
        Cluster cluster = new Cluster();
        cluster.setName(new DvText("test cluster"));
        cluster.setArchetypeNodeId("id12");
        Element element = new Element();
        cluster.setItems(Lists.newArrayList(element));
        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = rmObjectValidator.validate(cluster);
        assertEquals(2, messages.size());
        for(RMObjectValidationMessage message:messages) {
            assertTrue(message.getPath(), Sets.newHashSet("/items[1]/name", "/items[1]/archetype_node_id").contains(message.getPath()));
            assertEquals(RMObjectValidationMessageType.REQUIRED, message.getType());
        }
    }

    @Test
    public void testValidClusterWithoutArchetype() {
        Cluster cluster = new Cluster();
        cluster.setName(new DvText("test cluster"));
        cluster.setArchetypeNodeId("id12");
        Element element = new Element();
        element.setName(new DvText("test element"));
        element.setArchetypeNodeId("id15");
        cluster.setItems(Lists.newArrayList(element));
        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> messages = rmObjectValidator.validate(cluster);
        assertEquals(0, messages.size());

    }


    @Test
    public void testNestedEmptyElementWithoutArchetype() {
        Element element = new Element();
        RMObjectValidator rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance());
        List<RMObjectValidationMessage> validate = rmObjectValidator.validate(element);
        assertFalse(validate.isEmpty());
    }

    private Archetype parse(String filename) throws IOException {
        Archetype archetype = parser.parse(ArchetypeValidatorTest.class.getResourceAsStream(filename));
        assertTrue(parser.getErrors().toString(), parser.getErrors().hasNoErrors());
        return archetype;
    }

}