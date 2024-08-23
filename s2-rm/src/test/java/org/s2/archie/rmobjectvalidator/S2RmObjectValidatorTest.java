package org.s2.archie.rmobjectvalidator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageType;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;
import org.s2.rm.base.data_types.quantity.Proportion;
import org.s2.rm.base.data_types.quantity.Quantity;
import org.s2.rm.base.data_types.text.CodedText;
import org.s2.rm.base.data_types.text.PlainText;
import org.s2.rm.base.data_types.text.Text;
import org.s2.rm.base.foundation_types.terminology.TerminologyCode;
import org.s2.rm.base.foundation_types.terminology.TerminologyTerm;
import org.s2.rm.base.patterns.data_structures.Node;
import org.s2.rminfo.S2RmInfoLookup;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.*;

public class S2RmObjectValidatorTest {

    private TestUtil testUtil;
    InMemoryFullArchetypeRepository emptyRepo;
    private RMObjectValidator validator;


    @Before
    public void setup() {

        testUtil = new TestUtil(S2RmInfoLookup.getInstance());

        emptyRepo = new InMemoryFullArchetypeRepository();
        validator = new RMObjectValidator(S2RmInfoLookup.getInstance(), emptyRepo);
    }

    @Test
    public void requiredAttributesShouldBePresent() throws Exception {
        Archetype archetype = parse("/adl2-tests/rmobjectvalidity/s2-EHR-Node.element_with_required_attributes.v1.0.0.adls");
        OperationalTemplate opt = createOpt(archetype);

        Node node = (Node) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Proportion proportion = (Proportion) node.getValue();
        assert proportion != null;
        proportion.setDenominator(new Quantity(new BigDecimal("4.0"), new CodedText(new TerminologyTerm("ml", new TerminologyCode("snomed", "258773002")), "mL")));

        validator.setRunInvariantChecks(false);
        List<RMObjectValidationMessage> validationMessages = validator.validate(opt, node);
        assertEquals("There should be 2 errors", 2, validationMessages.size());
        assertEquals("There should be a validation message about the numerator", "Attribute numerator of class Proportion does not match existence 1..1", validationMessages.get(1).getMessage());
        assertEquals("There should be a validation message about the magnitiude", "Attribute magnitude of class Proportion does not match existence 1..1", validationMessages.get(0).getMessage());
        assertEquals("The path should be correct", "/value/numerator", validationMessages.get(1).getPath());
        assertEquals("The archetype path should be correct", "/value[id2]/numerator", validationMessages.get(1).getArchetypePath());

        proportion.setMagnitude(BigDecimal.valueOf(0.5));
        proportion.setNumerator(new Quantity(new BigDecimal("2.0"), new CodedText(new TerminologyTerm("ml", new TerminologyCode("snomed", "258773002")), "mL")));

        validationMessages = validator.validate(opt, node);
        assertEquals("There should be 0 errors", 0, validationMessages.size());
    }

    private OperationalTemplate createOpt(Archetype archetype) {
        return (OperationalTemplate) new Flattener(emptyRepo, AllMetaModelsInitialiser.getMetaModels(), FlattenerConfiguration.forOperationalTemplate()).flatten(archetype);
    }

    @Test
    public void testEmptyNodeWithoutArchetype() {
        Node node = new Node();

        node.setValue(new PlainText("something"));

        List<RMObjectValidationMessage> messages = validator.validate(node);
        assertEquals(2, messages.size());
        for(RMObjectValidationMessage message:messages) {
            assertTrue(message.getPath() + " unexpected value", Sets.newHashSet("/name", "/archetype_node_id", "/").contains(message.getPath()));
            assertTrue(EnumSet.of(RMObjectValidationMessageType.REQUIRED, RMObjectValidationMessageType.INVARIANT_ERROR).contains(message.getType()));
        }
    }

    @Test
    public void testNodeWithoutArchetype() {
        Node node = new Node();
        node.setName("test cluster");
        node.setArchetypeNodeId("id12");
        Node element = new Node();
        element.setValue(new PlainText("hi!"));
        node.setItems(Lists.newArrayList(element));

        List<RMObjectValidationMessage> messages = validator.validate(node);
        assertEquals(messages.toString() ,2, messages.size());
        for(RMObjectValidationMessage message:messages) {
            assertTrue(message.getPath(), Sets.newHashSet("/items[1]/name", "/items[1]/archetype_node_id").contains(message.getPath()));
            assertEquals(RMObjectValidationMessageType.REQUIRED, message.getType());
        }
    }

    @Test
    public void testValidNodeWithoutArchetype() {
        Node node1 = new Node();
        node1.setName("test cluster");
        node1.setArchetypeNodeId("id12");
        Node node2 = new Node();
        node2.setName("test element");
        node2.setValue(new PlainText("value"));
        node2.setArchetypeNodeId("id15");
        node1.setItems(Lists.newArrayList(node2));

        List<RMObjectValidationMessage> messages = validator.validate(node1);
        assertEquals(messages.toString(), 0, messages.size());

    }

    // ignore until invariants implemented in BMM, to allow code generation
    @Ignore
    @Test
    public void skipInvariantValidation(){
        //create element with every required field filled, that does not pass invariant
        Node node = new Node();
        node.setArchetypeNodeId("id5");
        node.setName("name");

        List<RMObjectValidationMessage> messages = validator.validate(node);
        assertEquals(messages.toString(), 1, messages.size());

        validator.setRunInvariantChecks(false);
        messages = validator.validate(node);
        assertEquals(messages.toString(), 0, messages.size());
    }


    @Test
    public void testNestedEmptyNodeWithoutArchetype() {
        Node node = new Node();

        List<RMObjectValidationMessage> validate = validator.validate(node);
        assertFalse(validate.isEmpty());
    }

    private Archetype parse(String filename) throws IOException, ADLParseException {
        return TestUtil.parseFailOnErrors(this.getClass(), filename);
    }

}