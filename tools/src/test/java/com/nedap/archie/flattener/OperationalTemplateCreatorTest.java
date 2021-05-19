package com.nedap.archie.flattener;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.util.Stack;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

public class OperationalTemplateCreatorTest {

    @Test
    public void fillEmptyOccurrences() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-CLUSTER.cluster_with_annotations.v1.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
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
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            FlattenerConfiguration flattenerConfiguration = FlattenerConfiguration.forOperationalTemplate();
            flattenerConfiguration.setFillEmptyOccurrences(false);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModels(), flattenerConfiguration);
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

    @Test(expected = IllegalArgumentException.class)
    public void failOnMissingArchetypeEnabled() throws Exception {
        SimpleArchetypeRepository repository = new SimpleArchetypeRepository();
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.with_used_archetype.v1.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            FlattenerConfiguration flattenerConfiguration = FlattenerConfiguration.forOperationalTemplate();
            Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModels(), flattenerConfiguration);
            OperationalTemplate template = (OperationalTemplate) flattener.flatten(archetype);
            fail();
        }
    }

    @Test
    public void failOnMissingArchetypeDisabled() throws Exception {
        try(InputStream stream = getClass().getResourceAsStream("openEHR-EHR-OBSERVATION.with_used_archetype.v1.adls")) {
            Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModels()).parse(stream);
            FlattenerConfiguration flattenerConfiguration = FlattenerConfiguration.forOperationalTemplate();
            flattenerConfiguration.setFailOnMissingUsedArchetype(false);
            Flattener flattener = new Flattener(new SimpleArchetypeRepository(), BuiltinReferenceModels.getMetaModels(), flattenerConfiguration);
            OperationalTemplate template = (OperationalTemplate) flattener.flatten(archetype);

            CArchetypeRoot archetypeRoot = template.getDefinition().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id8]");
            assertEquals("openEHR-EHR-CLUSTER.cluster_with_annotations.v1", archetypeRoot.getArchetypeRef());
            assertTrue(archetypeRoot.getAttributes().isEmpty());
        }
    }


}
