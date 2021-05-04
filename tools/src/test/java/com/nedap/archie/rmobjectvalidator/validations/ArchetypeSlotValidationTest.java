package com.nedap.archie.rmobjectvalidator.validations;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.creation.ExampleJsonInstanceGenerator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.composition.Section;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.support.identification.ArchetypeID;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessage;
import com.nedap.archie.rmobjectvalidator.RMObjectValidationMessageType;
import com.nedap.archie.rmobjectvalidator.RMObjectValidator;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ArchetypeSlotValidationTest {

    private Archetype parent;
    private Archetype included;
    private Archetype parentOfIncluded;
    private InMemoryFullArchetypeRepository repository;
    private OperationalTemplate parentOpt;
    private OperationalTemplate includedOpt;
    private OperationalTemplate parentOfIncludedOpt;
    private ExampleJsonInstanceGenerator generator;
    private Section example;
    private RMObjectValidator rmObjectValidator;

    @Before
    public void setup() throws IOException {
        Archetype parent = TestUtil.parseFailOnErrors("/adl2-tests/validity/slots/openEHR-EHR-SECTION.slot_parent.v1.0.0.adls");

        included = TestUtil.parseFailOnErrors("/com/nedap/archie/rmobjectvalidation/openEHR-EHR-OBSERVATION.redefine_child.v1.0.0.adls");
        parentOfIncluded = TestUtil.parseFailOnErrors("/com/nedap/archie/rmobjectvalidation/openEHR-EHR-OBSERVATION.spec_test_parent_2.v1.0.0.adls");
        repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parent);
        repository.addArchetype(included);
        repository.addArchetype(parentOfIncluded);

        parentOpt = (OperationalTemplate) createFlattener().flatten(parent);
        includedOpt = (OperationalTemplate) createFlattener().flatten(included);
        parentOfIncludedOpt = (OperationalTemplate) createFlattener().flatten(parentOfIncluded);

        repository.setOperationalTemplate(parentOpt);
        repository.setOperationalTemplate(includedOpt);
        repository.setOperationalTemplate(parentOfIncludedOpt);

        generator = new ExampleJsonInstanceGenerator(BuiltinReferenceModels.getMetaModels(), "en");
        Map<String, Object> generated = generator.generate(parentOpt);
        example = JacksonUtil.getObjectMapper().readValue(JacksonUtil.getObjectMapper().writeValueAsString(generated), Section.class);
        rmObjectValidator = new RMObjectValidator(ArchieRMInfoLookup.getInstance(), repository);
    }

    private Flattener createFlattener() {
        return new Flattener(repository, BuiltinReferenceModels.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
    }

    @Test
    public void validateWithCorrectArchetypeInSlot() throws Exception {
        //the example instance generator generated an empty example observation. Remove it
        example.getItems().remove(0);

        //generate a valid content, and validate
        Observation exampleIncluded = JacksonUtil.getObjectMapper().readValue(JacksonUtil.getObjectMapper().writeValueAsString(generator.generate(includedOpt)), Observation.class);
        example.addItem(exampleIncluded);
        exampleIncluded.setArchetypeNodeId("id2");

        List<RMObjectValidationMessage> validated = rmObjectValidator.validate(parentOpt, example);
        assertEquals(validated.toString(), 0, validated.size());

        //now do something invalid
        Element element = (Element) example.itemAtPath("/items[id2]/data/events[id3]/data/items[id4.1]");
        DvCodedText value = (DvCodedText) element.getValue();
        value.getDefiningCode().setCodeString("at27");


        validated = rmObjectValidator.validate(parentOpt, example);
        assertEquals(validated.toString(), 1, validated.size());
        RMObjectValidationMessage rmObjectValidationMessage = validated.get(0);
        assertEquals("/items[id2, 1]/data[id9]/events[id3, 1]/data[id10]/items[id4.1, 3]/value/defining_code[id9999]", rmObjectValidationMessage.getPath());
        assertEquals(RMObjectValidationMessageType.DEFAULT, rmObjectValidationMessage.getType());

    }

    @Test
    public void incorrectArchetypeInSlot() throws Exception {
        //the example instance generator generated an empty example observation. Remove it
        example.getItems().remove(0);

        //generate content with a wrong archetype, and validate
        Observation exampleIncluded = JacksonUtil.getObjectMapper().readValue(JacksonUtil.getObjectMapper().writeValueAsString(generator.generate(parentOfIncludedOpt)), Observation.class);
        example.addItem(exampleIncluded);
        exampleIncluded.setArchetypeNodeId("id2");

        List<RMObjectValidationMessage> validated = rmObjectValidator.validate(parentOpt, example);
        assertEquals(validated.toString(), 1, validated.size());
        RMObjectValidationMessage rmObjectValidationMessage = validated.get(0);
        assertEquals("/items[id2, 1]", rmObjectValidationMessage.getPath());
        assertEquals(RMObjectValidationMessageType.WRONG_ARCHETYPE_ID, rmObjectValidationMessage.getType());

    }

    @Test
    public void unknownArchetypeInSlot() throws Exception {
        //the example instance generator generated an empty example observation. Remove it
        example.getItems().remove(0);

        //generate a valid content, and validate
        Observation exampleIncluded = JacksonUtil.getObjectMapper().readValue(JacksonUtil.getObjectMapper().writeValueAsString(generator.generate(includedOpt)), Observation.class);
        exampleIncluded.setArchetypeDetails(new Archetyped(new ArchetypeID("openEHR-EHR-OBSERVATION.redefine_notfound.v1.0.0.adls"), "1.1.0"));
        example.addItem(exampleIncluded);
        exampleIncluded.setArchetypeNodeId("id2");

        List<RMObjectValidationMessage> validated = rmObjectValidator.validate(parentOpt, example);
        assertEquals(validated.toString(), 1, validated.size());
        RMObjectValidationMessage rmObjectValidationMessage = validated.get(0);
        assertEquals("/items[id2, 1]", rmObjectValidationMessage.getPath());
        assertEquals(RMObjectValidationMessageType.ARCHETYPE_NOT_FOUND, rmObjectValidationMessage.getType());
    }

}
