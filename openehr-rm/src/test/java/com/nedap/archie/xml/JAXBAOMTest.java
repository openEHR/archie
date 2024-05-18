package com.nedap.archie.xml;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.primitives.ConstraintStatus;
import com.nedap.archie.aom.rmoverlay.VisibilityType;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.openehr.serialisation.xml.OpenEhrRmJAXBUtil;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.temporal.TemporalAmount;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JAXBAOMTest {

    private Archetype archetype;
    private CComplexObject elementRootNode;
    private CAttribute valueAttribute;
    @Before
    public void setup() {
        //create an empty archetype
        archetype = new AuthoredArchetype();
        archetype.setArchetypeId(new ArchetypeHRID("openEHR-EHR-ELEMENT.test.v0.0.1"));
        ResourceDescription resourceDescription = new ResourceDescription();
        archetype.setDescription(resourceDescription);
        archetype.setTerminology(new ArchetypeTerminology());
        archetype.setOriginalLanguage(TerminologyCode.createFromString("[ISO_639-1::en]"));
        elementRootNode = new CComplexObject();
        elementRootNode.setNodeId("id1");
        elementRootNode.setRmTypeName("ELEMENT");
        valueAttribute = new CAttribute("value");
        elementRootNode.addAttribute(valueAttribute);
        archetype.setDefinition(elementRootNode);
    }
    @Test
    public void serializeCDuration() throws Exception {

        CDuration cDuration = new CDuration();
        cDuration.setConstraint(Lists.newArrayList(new Interval<>(
                DateTimeParsers.parseDurationValue("-P10D"),
                DateTimeParsers.parseDurationValue("PT10S")
                )));
        valueAttribute.addChild(cDuration);
        StringWriter writer = new StringWriter();
        Marshaller marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(archetype, writer);
        String xml = writer.toString();
        assertTrue(xml, xml.contains("-P10D"));
        assertTrue(xml, xml.contains("PT10S"));
    }

    @Test
    public void testCTerminologyCode() throws Exception {

        CTerminologyCode cTerminologyCode = new CTerminologyCode();
        cTerminologyCode.setConstraint(Lists.newArrayList("ac23"));
        cTerminologyCode.setConstraintStatus(ConstraintStatus.PREFERRED);
        valueAttribute.addChild(cTerminologyCode);
        StringWriter writer = new StringWriter();
        Marshaller marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(archetype, writer);
        String xml = writer.toString();

        assertTrue(xml, xml.contains("<constraintStatus>preferred</constraintStatus>"));

        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Archetype unmarshalled = (Archetype) unmarshaller.unmarshal(new StringReader(xml));
        CTerminologyCode parsedTerm = (CTerminologyCode) unmarshalled.getDefinition().getAttribute("value").getChildren().get(0);
        assertEquals(cTerminologyCode.getConstraint(), parsedTerm.getConstraint());
        assertEquals(ConstraintStatus.PREFERRED, parsedTerm.getConstraintStatus());
    }

    @Test
    public void parseCDuration() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<archetype is_generated=\"false\" is_differential=\"false\" xmlns:ns2=\"http://schemas.openehr.org/v1\">\n" +
                "    <description/>\n" +
                "    <original_language>\n" +
                "        <terminology_id>ISO_639-1</terminology_id>\n" +
                "        <code_string>en</code_string>\n" +
                "    </original_language>\n" +
                "    <archetype_id concept_id=\"test\" rm_publisher=\"openEHR\" rm_package=\"EHR\" rm_class=\"ELEMENT\" release_version=\"0.0.1\" version_status=\"RELEASED\" build_count=\"\"/>\n" +
                "    <definition rm_type_name=\"ELEMENT\" node_id=\"id1\">\n" +
                "        <attributes rm_attribute_name=\"value\">\n" +
                "            <is_multiple>false</is_multiple>\n" +
                "            <children xsi:type=\"C_DURATION\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "                <constraint lower_unbounded=\"false\" upper_unbounded=\"false\" lower_included=\"true\" upper_included=\"true\">\n" +
                "                    <lower xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">-P10D</lower>\n" +
                "                    <upper xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">P10YT10S</upper>\n" +
                "                </constraint>\n" +
                "            </children>\n" +
                "        </attributes>\n" +
                "    </definition>\n" +
                "    <terminology/>\n" +
                "    <otherMetaData/>\n" +
                "</archetype>";
        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        AuthoredArchetype unmarshalled = (AuthoredArchetype) unmarshaller.unmarshal(new StringReader(xml));
        CDuration parsedDuration = unmarshalled.itemAtPath("/value[1]");
        Interval<TemporalAmount> constraint = parsedDuration.getConstraint().get(0);
        assertEquals(1, parsedDuration.getConstraint().size());
        assertEquals(
                new Interval<TemporalAmount>(DateTimeParsers.parseDurationValue("-P10D"), DateTimeParsers.parseDurationValue("P10YT10S")),
                constraint);
    }

    @Test
    public void rmOverlay() throws Exception {
        Archetype archetype = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.to_flatten_parent_with_overlay.v1.0.0.adls");

        Marshaller marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        StringWriter writer = new StringWriter();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(archetype, writer);

        assertTrue(writer.toString().contains("<rm_overlay>\n" +
                "        <rm_visibility>\n" +
                "            <path>/subject</path>\n" +
                "            <visibility>hide</visibility>\n" +
                "            <alias>\n" +
                "                <terminology_id>local</terminology_id>\n" +
                "                <code_string>at12</code_string>\n" +
                "            </alias>\n" +
                "        </rm_visibility>\n" +
                "        <rm_visibility>\n" +
                "            <path>/data[id2]/events[id3]/data[id4]/items[id5]</path>\n" +
                "            <visibility>show</visibility>\n" +
                "        </rm_visibility>\n" +
                "        <rm_visibility>\n" +
                "            <path>/data[id2]/events[id3]/data[id4]/items[id6]</path>\n" +
                "            <visibility>show</visibility>\n" +
                "        </rm_visibility>\n" +
                "    </rm_overlay>"));

        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Archetype unmarshalled = (Archetype) unmarshaller.unmarshal(new StringReader(writer.toString()));

        assertEquals(VisibilityType.HIDE, unmarshalled.getRmOverlay().getRmVisibility().get("/subject").getVisibility());
        assertEquals("at12", unmarshalled.getRmOverlay().getRmVisibility().get("/subject").getAlias().getCodeString());

    }

    @Test
    public void otherMetaData() throws Exception {
        archetype.setOtherMetaData(new LinkedHashMap<>());
        archetype.getOtherMetaData().put("test key", "test value");
        archetype.getOtherMetaData().put("second test key", "second test value");

        Marshaller marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        StringWriter writer = new StringWriter();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(archetype, writer);

        assertTrue(writer.toString().contains("<other_meta_data id=\"test key\">test value</other_meta_data>"));
        assertTrue(writer.toString().contains("<other_meta_data id=\"second test key\">second test value</other_meta_data>"));

        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Archetype unmarshalled = (Archetype) unmarshaller.unmarshal(new StringReader(writer.toString()));
        assertEquals("test value", unmarshalled.getOtherMetaData().get("test key"));
        assertEquals("second test value", unmarshalled.getOtherMetaData().get("second test key"));
    }

    @Test
    public void opt2Xml() throws Exception {
        //create an empty archetype
        OperationalTemplate opt2 = new OperationalTemplate();
        opt2.setArchetypeId(new ArchetypeHRID("openEHR-EHR-ELEMENT.test.v0.0.1"));
        ResourceDescription resourceDescription = new ResourceDescription();
        opt2.setDescription(resourceDescription);
        opt2.setTerminology(new ArchetypeTerminology());
        opt2.setOriginalLanguage(TerminologyCode.createFromString("[ISO_639-1::en]"));
        elementRootNode = new CComplexObject();
        elementRootNode.setNodeId("id1");
        elementRootNode.setRmTypeName("ELEMENT");
        valueAttribute = new CAttribute("value");
        elementRootNode.addAttribute(valueAttribute);
        archetype.setDefinition(elementRootNode);
        ArchetypeTerminology terminology = new ArchetypeTerminology();
        terminology.setOwnerArchetype(opt2);
        Map<String, ArchetypeTerm> terms = new LinkedHashMap<>();
        terms.put("id2", new ArchetypeTerm("id2", "test term", "test term"));
        terminology.getTermDefinitions().put("en", terms);

        opt2.setComponentTerminologies(new LinkedHashMap<>());
        opt2.getComponentTerminologies().put("openEHR-EHR-CLUSTER.non_existing_test-archetype.v1.0.0", terminology);

        opt2.setTerminologyExtracts(new LinkedHashMap<>());
        opt2.getTerminologyExtracts().put("openEHR-EHR-CLUSTER.non_existing_test-archetype_2.v1.0.0", terminology);

        Marshaller marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        StringWriter writer = new StringWriter();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(opt2, writer);

        assertTrue(writer.toString().contains("<component_terminologies archetype_id=\"openEHR-EHR-CLUSTER.non_existing_test-archetype.v1.0.0\">\n" +
                "        <term_definitions language=\"en\">\n" +
                "            <items id=\"id2\">\n" +
                "                <description>test term</description>\n" +
                "                <text>test term</text>\n" +
                "            </items>\n" +
                "        </term_definitions>\n" +
                "    </component_terminologies>"));

        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        OperationalTemplate unmarshalled = (OperationalTemplate) unmarshaller.unmarshal(new StringReader(writer.toString()));
        assertEquals(terms.get("id2").getText(), unmarshalled.getComponentTerminologies().get("openEHR-EHR-CLUSTER.non_existing_test-archetype.v1.0.0").getTermDefinition("en", "id2").getText());
        assertEquals(terms.get("id2").getText(), unmarshalled.getTerminologyExtracts().get("openEHR-EHR-CLUSTER.non_existing_test-archetype_2.v1.0.0").getTermDefinition("en", "id2").getText());

    }

    @Test
    public void templateXml() throws Exception {
        //create an empty archetype
        Template template = new Template();
        template.setArchetypeId(new ArchetypeHRID("openEHR-EHR-ELEMENT.test.v0.0.1"));
        ResourceDescription resourceDescription = new ResourceDescription();
        template.setDescription(resourceDescription);

        template.setOriginalLanguage(TerminologyCode.createFromString("[ISO_639-1::en]"));
        elementRootNode = new CComplexObject();
        elementRootNode.setNodeId("id1");
        elementRootNode.setRmTypeName("ELEMENT");
        valueAttribute = new CAttribute("value");
        elementRootNode.addAttribute(valueAttribute);
        archetype.setDefinition(elementRootNode);
        ArchetypeTerminology terminology = new ArchetypeTerminology();
        terminology.setOwnerArchetype(template);
        Map<String, ArchetypeTerm> terms = new LinkedHashMap<>();
        terms.put("id2", new ArchetypeTerm("id2", "test term", "test term"));
        terminology.getTermDefinitions().put("en", terms);
        template.setTerminology(terminology);

        TemplateOverlay overlay = new TemplateOverlay();
        overlay.setArchetypeId(new ArchetypeHRID("openEHR-EHR-ELEMENT.test.v0.0.1"));
        overlay.setParentArchetypeId("openEHR-EHR-ELEMENT.parent.v0.0.1");
        ResourceDescription overlayResourceDescription = new ResourceDescription();
        overlay.setDescription(overlayResourceDescription);
        overlay.setDefinition(new CComplexObject());
        overlay.getDefinition().setNodeId("id1.1");
        overlay.setOriginalLanguage(TerminologyCode.createFromString("[ISO_639-1::en]"));

        template.addTemplateOverlay(overlay);

        Marshaller marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        StringWriter writer = new StringWriter();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(template, writer);

        assertTrue(writer.toString().contains(" <template_overlay is_generated=\"false\" is_differential=\"false\">\n" +
                "        <description/>\n" +
                "        <original_language>\n" +
                "            <terminology_id>ISO_639-1</terminology_id>\n" +
                "            <code_string>en</code_string>\n" +
                "        </original_language>\n" +
                "        <archetype_id concept_id=\"test\" rm_publisher=\"openEHR\" rm_package=\"EHR\" rm_class=\"ELEMENT\" release_version=\"0.0.1\" version_status=\"RELEASED\" build_count=\"\"/>\n" +
                "        <parent_archetype_id>openEHR-EHR-ELEMENT.parent.v0.0.1</parent_archetype_id>\n" +
                "        <definition node_id=\"id1.1\"/>\n" +
                "    </template_overlay>"));

        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Template unmarshalled = (Template) unmarshaller.unmarshal(new StringReader(writer.toString()));
        assertEquals("one template overlay should have been unmarshalled", 1, unmarshalled.getTemplateOverlays().size());
        assertEquals("openEHR-EHR-ELEMENT.test.v0.0.1", unmarshalled.getTemplateOverlays().get(0).getArchetypeId().getFullId());

    }

}
