package com.nedap.archie.text;

import com.esotericsoftware.kryo.kryo5.serializers.DefaultSerializers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.creation.ExampleJsonInstanceGenerator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.json.ArchieJacksonConfiguration;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.FeederAuditDetails;
import com.nedap.archie.rm.composition.*;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datastructures.ItemList;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.integration.GenericEntry;
import com.nedap.archie.testutil.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;
import org.openehr.utils.message.I18n;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RmToMarkdownSerializerTest {

    private static final String TYPE_PROPERTY_NAME = "_type";

    @Before
    public void setLocale() {
        I18n.setCurrentLocale(Locale.ENGLISH);
    }

    @After
    public void unsetLocale() {
        I18n.setCurrentLocale(null);
    }

    @Test
    public void bloodPressure() throws Exception {
        OperationalTemplate opt = createOPT("/ckm-mirror/local/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);
        //System.out.println(s);

        Observation observation = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, Observation.class);
        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(observation);
        String serialized = rmToMarkdownSerializer.toString();

        assertTrue(serialized, serialized.contains("Systolic: 0.0mm[Hg]  \n")); //'  \n' = newline in Markdown
        assertTrue(serialized, serialized.contains("Position: Standing  \n")); //'  \n' = newline in Markdown
        assertTrue(serialized, serialized.contains("### Blood Pressure"));

    }

    @Test
    public void dutchBloodPressure() throws Exception {
        OperationalTemplate opt = createOPT("/ckm-mirror/local/archetypes/entry/observation/openEHR-EHR-OBSERVATION.blood_pressure.v1.1.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator("nl");
        I18n.setCurrentLocale(Locale.forLanguageTag("nl"));

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);
        //System.out.println(s);

        Observation observation = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, Observation.class);
        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(observation);
        String serialized = rmToMarkdownSerializer.toString();

        assertTrue(serialized, serialized.contains("Systole: 0.0mm[Hg]  \n")); //'  \n' = newline in Markdown
        assertTrue(serialized, serialized.contains("Houding: Staand  \n")); //'  \n' = newline in Markdown
        assertTrue(serialized, serialized.contains("### Bloeddruk"));
        //and a Dutch translation from the I18n po file
        assertTrue(serialized, serialized.contains("Tijd van observatie: 1 jan. 2018 12:00:00"));

    }

    @Test
    public void bloodPressureComposition() throws Exception {
        Archetype report = TestUtil.parseFailOnErrors("/com/nedap/archie/flattener/openEHR-EHR-COMPOSITION.report.v1.adls");
        Archetype reportResult = TestUtil.parseFailOnErrors("/com/nedap/archie/flattener/openEHR-EHR-COMPOSITION.report-result.v1.adls");
        Archetype device = TestUtil.parseFailOnErrors("/com/nedap/archie/flattener/openEHR-EHR-CLUSTER.device.v1.adls");

        Archetype bloodPressureObservation = TestUtil.parseFailOnErrors("/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.blood_pressure.v1.adls");
        Archetype bloodPressureComposition = TestUtil.parseFailOnErrors("/com/nedap/archie/flattener/openEHR-EHR-COMPOSITION.blood_pressure.v1.0.0.adlt");

        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(report);
        repository.addArchetype(reportResult);
        repository.addArchetype(device);
        repository.addArchetype(bloodPressureObservation);
        repository.addArchetype(bloodPressureComposition);

        OperationalTemplate bloodPressureCompositionOPT = (OperationalTemplate) new Flattener(
                repository,
                BuiltinReferenceModels.getMetaModels())
                .createOperationalTemplate(true)
                .flatten(bloodPressureComposition);

        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(bloodPressureCompositionOPT);
        String s = serializeToJson(structure, true);

        Composition composition = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, Composition.class);

        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(composition);
        String serialized = rmToMarkdownSerializer.toString();

        assertTrue(serialized, serialized.contains("Systolic: 0.0mm[Hg]  \n")); //'  \n' = newline in Markdown
        assertTrue(serialized, serialized.contains("Position: Standing  \n")); //'  \n' = newline in Markdown
        assertTrue(serialized, serialized.contains("# Blood pressure composition"));
        assertTrue(serialized, serialized.contains("## Context"));
        assertTrue(serialized, serialized.contains("### Blood pressure"));
        assertTrue(serialized, serialized.contains("#### Exertion"));

    }

    @Test
    public void evaluation() throws Exception {
        OperationalTemplate opt = createOPT("/ckm-mirror/local/archetypes/entry/evaluation/openEHR-EHR-EVALUATION.recommendation.v1.1.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);

        Evaluation evaluation = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, Evaluation.class);
        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(evaluation);
        String serialized = rmToMarkdownSerializer.toString();
        assertTrue(serialized, serialized.contains("### Recommendation"));
        assertTrue(serialized, serialized.contains("Recommendation: string  \n")); //'  \n' = newline in Markdown
        assertTrue(serialized, serialized.contains("Rationale: string  \n")); //'  \n' = newline in Markdown


    }

    @Test
    public void instruction() throws Exception {
        OperationalTemplate opt = createOPT("/ckm-mirror/local/archetypes/entry/instruction/openEHR-EHR-INSTRUCTION.medication_order.v1.0.1.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);

        Instruction instruction = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, Instruction.class);
        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(instruction);
        String serialized = rmToMarkdownSerializer.toString();
        assertTrue(serialized, serialized.contains("### Medication order  "));
        assertTrue(serialized, serialized.contains("#### Preparation details  "));
        assertTrue(serialized, serialized.contains("Substitution direction: Permitted  \n")); //'  \n' = newline in Markdown
    }

    @Test
    public void action() throws Exception {
        OperationalTemplate opt = createOPT("/ckm-mirror/local/archetypes/entry/action/openEHR-EHR-ACTION.medication.v0.0.1-alpha.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);

        Action data = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, Action.class);
        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(data);
        String serialized = rmToMarkdownSerializer.toString();
        assertTrue(serialized, serialized.contains("### Medication"));
        assertTrue(serialized, serialized.contains("Sequence number: 42  \n"));
        assertTrue(serialized, serialized.contains("##### State transition"));
        assertTrue(serialized, serialized.contains("Care flow step: Medication recommended  "));
    }

    @Test
    public void adminEntry() throws Exception {
        OperationalTemplate opt = createOPT("/ckm-mirror/local/archetypes/entry/admin_entry/openEHR-EHR-ADMIN_ENTRY.admission.v1.0.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);

        AdminEntry data = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, AdminEntry.class);
        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(data);
        String serialized = rmToMarkdownSerializer.toString();
        assertTrue(serialized, serialized.contains("### Patient admission"));
        assertTrue(serialized, serialized.contains("Location type: Clinic  \n"));
        assertTrue(serialized, serialized.contains("#### Attending doctor "));
    }

    @Test
    public void genericEntry() throws Exception {
        OperationalTemplate opt = createOPT("/com/nedap/archie/aom/openEHR-EHR-GENERIC_ENTRY.included.v1.0.0.adls");
        ExampleJsonInstanceGenerator structureGenerator = createExampleJsonInstanceGenerator();

        Map<String, Object> structure = structureGenerator.generate(opt);
        String s = serializeToJson(structure, true);

        GenericEntry data = JacksonUtil.getObjectMapper(ArchieJacksonConfiguration.createStandardsCompliant()).readValue(s, GenericEntry.class);
        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(data);
        String serialized = rmToMarkdownSerializer.toString();
        //this is a nearly empty nonsensical test archetype, so the output will also be
        assertTrue(serialized, serialized.contains("### root node generic entry en"));
        assertTrue(serialized, serialized.contains("an element: Value 1  \n"));
    }

    @Test
    public void compositionParticipants() throws Exception {
        Composition composition = new Composition();
        composition.setName(new DvText("composition name"));
        EventContext context = new EventContext();
        composition.setContext(context);
        List<Participation> participations = new ArrayList<Participation>();
        Participation participation1 = new Participation();
        participation1.setFunction(new DvText("some function"));
        participation1.setMode(new DvCodedText("some mode", new CodePhrase("[local::at2]")));
        Participation participation2 = new Participation();
        participation2.setFunction(new DvText("some function 2"));
        participation2.setMode(new DvCodedText("some mode 2", new CodePhrase("[local::at3]")));

        participations.add(participation1);
        participations.add(participation2);
        context.setParticipations(participations);

        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(composition);
        String serialized = rmToMarkdownSerializer.toString();

        assertTrue(serialized, serialized.startsWith("# composition name  \n## Context  \n" +
                "##### participations  \n" +
                "Function of participant: some function  \n" +
                "Mode of participant: some mode  \n" +
                "  \n" +
                "Function of participant: some function 2  \n" +
                "Mode of participant: some mode 2  \n"));
    }

    @Test
    public void compositionSection() throws Exception {
        Composition composition = new Composition();
        composition.setName(new DvText("composition name"));
        EventContext context = new EventContext();
        composition.setContext(context);
        Section section = new Section();
        section.setNameAsString("section");
        Section innerSection = new Section();
        innerSection.setNameAsString("inner section");
        section.addItem(innerSection);
        composition.addContent(section);

        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(composition);
        String serialized = rmToMarkdownSerializer.toString();

        assertTrue(serialized, serialized.startsWith("# composition name  \n" +
                "## section  \n" +
                "## inner section  \n" +
                "  \n" +
                "  \n" +
                "## Context  \n" +
                "  "));
    }

    @Test
    public void compositionFeederAudit() throws Exception {
        Composition composition = new Composition();
        composition.setName(new DvText("composition name"));

        FeederAudit feederAudit = new FeederAudit();
        FeederAuditDetails systemAudit = new FeederAuditDetails();
        systemAudit.setSystemId("some system");
        systemAudit.setTime(new DvDateTime(LocalDateTime.of(2023, 5, 1, 10, 23, 5)));
        ItemList otherDetails = new ItemList();
        Element content = new Element();
        content.setNameAsString("system audit name");
        content.setNullFlavour(new DvCodedText("unknown", "at26"));
        otherDetails.addItem(content);
        systemAudit.setOtherDetails(otherDetails);
        feederAudit.setFeederSystemAudit(systemAudit);
        feederAudit.setOriginatingSystemAudit(systemAudit);
        composition.setFeederAudit(feederAudit);

        RmToMarkdownSerializer rmToMarkdownSerializer = new RmToMarkdownSerializer();
        rmToMarkdownSerializer.append(composition);
        String serialized = rmToMarkdownSerializer.toString();

        assertEquals(serialized, "# composition name  \n" +
                "#### Originating system audit  \n" +
                "Originating system: some system  \n" +
                "time: May 1, 2023, 10:23:05 AM  \n" +
                "##### other details  \n" +
                "system audit name: No value. Reason: unknown  \n" +
                "  \n" +
                "  \n" +
                "#### Feeder system audit  \n" +
                "Originating system: some system  \n" +
                "time: May 1, 2023, 10:23:05 AM  \n" +
                "##### other details  \n" +
                "system audit name: No value. Reason: unknown  \n" +
                "  \n" +
                "  \n");
    }


    private ExampleJsonInstanceGenerator createExampleJsonInstanceGenerator() {
        return createExampleJsonInstanceGenerator("en");
    }
    private ExampleJsonInstanceGenerator createExampleJsonInstanceGenerator(String language) {
        ExampleJsonInstanceGenerator structureGenerator = new ExampleJsonInstanceGenerator(BuiltinReferenceModels.getMetaModels(), language);
        structureGenerator.setAddTwoInstancesWherePossible(false);
        structureGenerator.setTypePropertyName(TYPE_PROPERTY_NAME);
        return structureGenerator;
    }

    private String serializeToJson(Map<String, Object> structure, boolean indent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if(indent) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        } else {
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper.writeValueAsString(structure);
    }

    private OperationalTemplate createOPT(String s2) throws IOException, ADLParseException {
        Archetype archetype = TestUtil.parseFailOnErrors(s2);
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(archetype);
        return (OperationalTemplate) new Flattener(repository, BuiltinReferenceModels.getMetaModels()).createOperationalTemplate(true).flatten(archetype);
    }
}
