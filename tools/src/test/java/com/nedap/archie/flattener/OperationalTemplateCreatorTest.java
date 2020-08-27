package com.nedap.archie.flattener;

import com.google.common.collect.Lists;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import javax.json.JsonPatch;
import java.io.InputStream;
import java.util.List;
import java.util.Stack;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
}
