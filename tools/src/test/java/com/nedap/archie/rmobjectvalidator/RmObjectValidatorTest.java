package com.nedap.archie.rmobjectvalidator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datastructures.Item;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.DvProportion;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.*;

public class RmObjectValidatorTest {

    private TestUtil testUtil;
    InMemoryFullArchetypeRepository emptyRepo;
    private RMObjectValidator validator;
    private RMObjectValidator validatorWithoutInvariants;


    @Before
    public void setup() {

        testUtil = new TestUtil();

        emptyRepo = new InMemoryFullArchetypeRepository();
        validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance(), emptyRepo,
                new ValidationConfiguration.Builder().build());
        validatorWithoutInvariants = new RMObjectValidator(ArchieRMInfoLookup.getInstance(), emptyRepo,
                new ValidationConfiguration.Builder().validateInvariants(false).build());
    }

    @Test
    public void requiredAttributesShouldBePresent() throws Exception {
        Archetype archetype = parse("/adl2-tests/rmobjectvalidity/openEHR-EHR-ELEMENT.element_with_required_attributes.v1.0.0.adls");
        OperationalTemplate opt = createOpt(archetype);

        Element element = (Element) testUtil.constructEmptyRMObject(archetype.getDefinition());
        DvProportion dvProportion = (DvProportion) element.getValue();
        dvProportion.setDenominator(4D);
        dvProportion.setType(3L);

        List<RMObjectValidationMessage> validationMessages = validatorWithoutInvariants.validate(opt, element);
        assertEquals("There should be 1 errors", 1, validationMessages.size());
        assertEquals("There should be a validation message about the numerator", "Attribute numerator of class DV_PROPORTION does not match existence 1..1", validationMessages.get(0).getMessage());
        assertEquals("The path should be correct", "/value/numerator", validationMessages.get(0).getPath());
        assertEquals("The archetype path should be correct", "/value[id2]/numerator", validationMessages.get(0).getArchetypePath());

        dvProportion.setNumerator(2D);
        validationMessages = validator.validate(opt, element);
        assertEquals("There should be 0 errors", 0, validationMessages.size());

    }

    @Test
    public void cardinalityMismatchValidation() throws Exception {
        Archetype archetype = parse("/adl2-tests/rmobjectvalidity/openEHR-EHR-ITEM_TREE.cardinality_testing.v1.0.0.adls");
        OperationalTemplate opt = createOpt(archetype);

        ItemTree itemTree = (ItemTree) testUtil.constructEmptyRMObject(archetype.getDefinition());
        List<Item> items = itemTree.getItems();
        items.remove(0);
        items.remove(0);

        List<RMObjectValidationMessage> validationMessages = validatorWithoutInvariants.validate(opt, itemTree);
        assertEquals("There should be 1 error", 1, validationMessages.size());
        assertEquals("Attribute does not match cardinality 1..2", validationMessages.get(0).getMessage());
        // Type should be REQUIRED
        assertEquals(RMObjectValidationMessageType.CARDINALITY_MISMATCH, validationMessages.get(0).getType());
    }

    private OperationalTemplate createOpt(Archetype archetype) {
        return (OperationalTemplate) new Flattener(emptyRepo, BuiltinReferenceModels.getMetaModels(), FlattenerConfiguration.forOperationalTemplate()).flatten(archetype);
    }

    @Test
    public void testEmptyElementWithoutArchetype() {
        Element element = new Element();

        element.setValue(new DvText("something"));

        List<RMObjectValidationMessage> messages = validator.validate(element);
        assertEquals(2, messages.size());
        for(RMObjectValidationMessage message:messages) {
            assertTrue(message.getPath() + " unexpected value", Sets.newHashSet("/name", "/archetype_node_id", "/").contains(message.getPath()));
            assertTrue(EnumSet.of(RMObjectValidationMessageType.REQUIRED, RMObjectValidationMessageType.INVARIANT_ERROR).contains(message.getType()));
        }
    }

    @Test
    public void testClusterWithoutArchetype() {
        Cluster cluster = new Cluster();
        cluster.setName(new DvText("test cluster"));
        cluster.setArchetypeNodeId("id12");
        Element element = new Element();
        element.setValue(new DvText("hi!"));
        cluster.setItems(Lists.newArrayList(element));

        List<RMObjectValidationMessage> messages = validator.validate(cluster);
        assertEquals(messages.toString() ,2, messages.size());
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
        element.setValue(new DvText("value"));
        element.setArchetypeNodeId("id15");
        cluster.setItems(Lists.newArrayList(element));

        List<RMObjectValidationMessage> messages = validator.validate(cluster);
        assertEquals(messages.toString(), 0, messages.size());

    }

    @Test
    public void skipInvariantValidation(){
        //create element with every required field filled, that does not pass invariant
        Element element = new Element();
        element.setArchetypeNodeId("id5");
        element.setName(new DvText("name"));


        List<RMObjectValidationMessage> messages = validator.validate(element);
        assertEquals(messages.toString(), 1, messages.size());

        messages = validatorWithoutInvariants.validate(element);
        assertEquals(messages.toString(), 0, messages.size());

    }

    @Test
    @Deprecated
    public void skipInvariantValidationOld(){
        //create element with every required field filled, that does not pass invariant
        Element element = new Element();
        element.setArchetypeNodeId("id5");
        element.setName(new DvText("name"));

        RMObjectValidator oldValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance(), emptyRepo);
        List<RMObjectValidationMessage> messages = oldValidator.validate(element);
        assertEquals(messages.toString(), 1, messages.size());

        oldValidator.setRunInvariantChecks(false);
        messages = oldValidator.validate(element);
        assertEquals(messages.toString(), 0, messages.size());

    }

    @Test
    @Deprecated
    public void skipInvariantValidationDouble(){
        RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance(), emptyRepo,
                new ValidationConfiguration.Builder().build());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> validator.setRunInvariantChecks(true));
        assertEquals("validateInvariants is already set via validationConfiguration, cannot set it again via setRunInvariantChecks", ex.getMessage());
    }


    @Test
    public void testNestedEmptyElementWithoutArchetype() {
        Element element = new Element();

        List<RMObjectValidationMessage> validate = validator.validate(element);
        assertFalse(validate.isEmpty());
    }


    private Archetype parse(String filename) throws IOException, ADLParseException {
        return TestUtil.parseFailOnErrors(filename);
    }

}