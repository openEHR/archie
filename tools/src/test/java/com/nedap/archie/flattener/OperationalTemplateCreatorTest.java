package com.nedap.archie.flattener;

import com.google.common.collect.Lists;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.*;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.flattener.specexamples.FlattenerTestUtil;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

public class OperationalTemplateCreatorTest {

    @Test
    public void includeZeroExistence() throws Exception {
        SimpleArchetypeRepository repository = new SimpleArchetypeRepository();
        Archetype emptyObservation = parse("openEHR-EHR-OBSERVATION.empty_observation.v1.0.0.adls");
        repository.addArchetype(emptyObservation);

        try(InputStream stream = getClass().getResourceAsStream("specexamples/openEHR-EHR-OBSERVATION.protocol_exclusion.v1.0.0.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());

            // Assert protocol existence matches {0}
            CAttribute protocol = flattener.flatten(archetype).getDefinition().getAttribute("protocol");
            MultiplicityInterval existence = protocol.getExistence();
            Integer zeroInt = Integer.valueOf(0);
            assertEquals(zeroInt, existence.getLower());
            assertEquals(zeroInt, existence.getUpper());
            assertTrue(protocol.getChildren().isEmpty());
        }
    }

    @Test
    public void fillEmptyOccurrences() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.cluster_with_annotations.v1.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModelProvider(), FlattenerConfiguration.forOperationalTemplate());
            OperationalTemplate template = (OperationalTemplate) flattener.flatten(archetype);

            Stack<CObject> workList = new Stack<>();
            workList.push(template.getDefinition());
            while(!workList.isEmpty()) {
                CObject cObject = workList.pop();
                if(cObject instanceof CComplexObject) {
                    assertNotNull(cObject.getOccurrences());
                    CObject objectInOriginal = archetype.itemAtPath(cObject.getPath());
                    assertEquals(objectInOriginal.effectiveOccurrences(ArchieRMInfoLookup.getInstance()::referenceModelPropMultiplicity), cObject.getOccurrences());
                }
                for(CAttribute attribute:cObject.getAttributes()) {
                    workList.addAll(attribute.getChildren());
                }
            }
        }
    }

    @Test
    public void dontFillEmptyOccurrencesUnlessSet() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.cluster_with_annotations.v1.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            FlattenerConfiguration flattenerConfiguration = FlattenerConfiguration.forOperationalTemplate();
            flattenerConfiguration.setFillEmptyOccurrences(false);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModelProvider(), flattenerConfiguration);
            OperationalTemplate template = (OperationalTemplate) flattener.flatten(archetype);

            Stack<CObject> workList = new Stack<>();
            workList.push(template.getDefinition());
            while(!workList.isEmpty()) {
                CObject cObject = workList.pop();
                if(cObject instanceof CComplexObject) {
                    //this archetype has no occurrences at all
                    assertNull(cObject.getOccurrences());
                }
                for(CAttribute attribute:cObject.getAttributes()) {
                    workList.addAll(attribute.getChildren());
                }
            }
        }
    }

    @Test
    public void failOnMissingArchetypeEnabled() throws Exception {
        SimpleArchetypeRepository repository = new SimpleArchetypeRepository();
        assertThrows(IllegalArgumentException.class, () -> {
            try (InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.with_used_archetype.v1.adls")) {
                Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
                FlattenerConfiguration flattenerConfiguration = FlattenerConfiguration.forOperationalTemplate();
                Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModelProvider(), flattenerConfiguration);
                OperationalTemplate template = (OperationalTemplate) flattener.flatten(archetype);
                fail();
            }
        });
    }

    @Test
    public void failOnMissingArchetypeDisabled() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.with_used_archetype.v1.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
            FlattenerConfiguration flattenerConfiguration = FlattenerConfiguration.forOperationalTemplate();
            flattenerConfiguration.setFailOnMissingUsedArchetype(false);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModelProvider(), flattenerConfiguration);
            OperationalTemplate template = (OperationalTemplate) flattener.flatten(archetype);

            CArchetypeRoot archetypeRoot = template.getDefinition().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id8]");
            assertEquals("openEHR-EHR-CLUSTER.cluster_with_annotations.v1", archetypeRoot.getArchetypeRef());
            assertTrue(archetypeRoot.getAttributes().isEmpty());
        }
    }

    @Test
    public void allowSpecializationBeforeExclusionEnabled() throws Exception {
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        Archetype parentArchetype = parse("/com/nedap/archie/flattener/siblingorder/openEHR-EHR-CLUSTER.siblingorderparent.v1.0.0.adls");
        repository.addArchetype(parentArchetype);

        FlattenerConfiguration config = FlattenerConfiguration.forOperationalTemplate();
        // Explicitly set it to true, even though it's default
        config.setAllowSpecializationAfterExclusion(true);

        Archetype flatChild =  parseAndCreateOPTWithConfig("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.specialized_nodes_order.v1.0.0.adls", repository, config);
        List<CObject> children = flatChild.getDefinition().getAttribute("items").getChildren();
        List<String> nodeIds = children.stream().map((cobject) -> cobject.getNodeId()).collect(Collectors.toList());
        assertEquals(
                Lists.newArrayList("id5.1", "id6.1", "id7.1"),
                nodeIds
        );
    }

    @Test
    public void allowSpecializationBeforeExclusionDisabled() throws Exception {
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        Archetype parentArchetype = parse("/com/nedap/archie/flattener/siblingorder/openEHR-EHR-CLUSTER.siblingorderparent.v1.0.0.adls");
        repository.addArchetype(parentArchetype);

        FlattenerConfiguration config = FlattenerConfiguration.forOperationalTemplate();
        config.setAllowSpecializationAfterExclusion(false);

        Archetype flatChild =  parseAndCreateOPTWithConfig("/com/nedap/archie/archetypevalidator/openEHR-EHR-CLUSTER.specialized_nodes_order.v1.0.0.adls", repository, config);
        List<CObject> children = flatChild.getDefinition().getAttribute("items").getChildren();
        List<String> nodeIds = children.stream().map((cobject) -> cobject.getNodeId()).collect(Collectors.toList());
        assertEquals(
                Lists.newArrayList("id6.1", "id7.1"),
                nodeIds
        );
    }

    private Archetype parseAndCreateOPTWithConfig(String fileName, InMemoryFullArchetypeRepository repository, FlattenerConfiguration config) throws IOException, ADLParseException {
        Archetype result = parse(fileName);
        ReferenceModels models = new ReferenceModels();
        models.registerModel(ArchieRMInfoLookup.getInstance());
        ValidationResult validationResult = new ArchetypeValidator(models).validate(result, repository);
        assertTrue(validationResult.getErrors().toString(), validationResult.passes());
        return new Flattener(repository, BuiltinReferenceModels.getMetaModelProvider(), config).flatten(parse(fileName));
    }

    private Archetype parse(String filePath) throws IOException, ADLParseException {
        return FlattenerTestUtil.parse(filePath);
    }
}
