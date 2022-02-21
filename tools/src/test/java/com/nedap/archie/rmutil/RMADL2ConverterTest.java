package com.nedap.archie.rmutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.creation.ExampleJsonInstanceGenerator;
import com.nedap.archie.creation.RMObjectCreator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.json.RMJacksonConfiguration;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datastructures.Item;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.integration.GenericEntry;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.RMListener;
import com.nedap.archie.rminfo.RMTreeWalker;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RMADL2ConverterTest {

    @Test
    public void convert2To14() throws Exception {
        Archetype archetype = TestUtil.parseFailOnErrors("/basic.adl");
        MetaModels metaModels = BuiltinReferenceModels.getMetaModels();
        Flattener optCreator = new Flattener(new SimpleArchetypeRepository(), metaModels, FlattenerConfiguration.forOperationalTemplate());
        ExampleJsonInstanceGenerator generator = new ExampleJsonInstanceGenerator(metaModels, "en");
        generator.setTypePropertyName("_type");
        Map<String, Object> generated = generator.generate((OperationalTemplate) optCreator.flatten(archetype));
        RMJacksonConfiguration standardsCompliant = RMJacksonConfiguration.createStandardsCompliant();
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(standardsCompliant);
        RMObject rmObject = objectMapper.readValue(objectMapper.writeValueAsString(generated), RMObject.class);
        RMADL2Converter rmAdl2Converter = new RMADL2Converter();
        rmAdl2Converter.convertToADL14(rmObject);

        rmAdl2Converter.convertToADL2(rmObject);
        //assert that all codes are in ADL 2 format again
        new RMTreeWalker(ArchieRMInfoLookup.getInstance()).walk(rmObject, new NodeIdIsADL2Checker());
    }

    @Test
    public void convertValueCodes() throws Exception {
        Archetype archetype = TestUtil.parseFailOnErrors("/com/nedap/archie/aom/openEHR-EHR-GENERIC_ENTRY.included.v1.0.0.adls");
        MetaModels metaModels = BuiltinReferenceModels.getMetaModels();
        Flattener optCreator = new Flattener(new SimpleArchetypeRepository(), metaModels, FlattenerConfiguration.forOperationalTemplate());
        ExampleJsonInstanceGenerator generator = new ExampleJsonInstanceGenerator(metaModels, "en");
        generator.setTypePropertyName("_type");
        Map<String, Object> generated = generator.generate((OperationalTemplate) optCreator.flatten(archetype));
        RMJacksonConfiguration standardsCompliant = RMJacksonConfiguration.createStandardsCompliant();
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper(standardsCompliant);
        RMObject rmObject = objectMapper.readValue(objectMapper.writeValueAsString(generated), RMObject.class);

        RMADL2Converter rmAdl2Converter = new RMADL2Converter();
        rmAdl2Converter.convertToADL14(rmObject);
        {
            GenericEntry entry = (GenericEntry) rmObject;
            assertEquals("at0000", entry.getArchetypeNodeId());
            List<Item> items = entry.getData().getItems();
            Element element1 = (Element) items.get(0);
            assertEquals("at0001", element1.getArchetypeNodeId());
            DvCodedText codedText = (DvCodedText) element1.getValue();
            assertEquals("at0003", codedText.getDefiningCode().getCodeString());
        }

        rmAdl2Converter.convertToADL2(rmObject);
        {
            GenericEntry entry = (GenericEntry) rmObject;
            assertEquals("id1", entry.getArchetypeNodeId());
            List<Item> items = entry.getData().getItems();
            Element element1 = (Element) items.get(0);
            assertEquals("id2", element1.getArchetypeNodeId());
            DvCodedText codedText = (DvCodedText) element1.getValue();
            assertEquals("at4", codedText.getDefiningCode().getCodeString());
        }

    }

    private static class NodeIdIsADL2Checker implements RMListener {

        @Override
        public void enterObject(Object object) {
            if(object instanceof Locatable) {
                Locatable locatable = (Locatable) object;
                assertTrue(AOMUtils.isIdCode(locatable.getArchetypeNodeId()));
            }
            if(object instanceof CodePhrase) {
                CodePhrase codePhrase = (CodePhrase) object;
                if(codePhrase.getTerminologyId() != null && codePhrase.getTerminologyId().getValue().equals("local")) {
                    assertTrue(AOMUtils.isValueCode(codePhrase.getCodeString()));
                }
            }
        }

        @Override
        public void exitObject(Object object) { }
    }

}
