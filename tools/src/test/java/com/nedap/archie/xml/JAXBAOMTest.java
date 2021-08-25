package com.nedap.archie.xml;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.aom.rmoverlay.VisibilityType;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.datetime.DateTimeParsers;
import com.nedap.archie.json.JacksonUtil;
import com.nedap.archie.json.RMJacksonConfiguration;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.temporal.TemporalAmount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        Marshaller marshaller = JAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(archetype, writer);
        String xml = writer.toString();
        assertTrue(xml, xml.contains("-P10D"));
        assertTrue(xml, xml.contains("PT10S"));
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
        Unmarshaller unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
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
        Archetype archetype = TestUtil.parseFailOnErrors("/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.to_flatten_parent_with_overlay.v1.0.0.adls");

        Marshaller marshaller = JAXBUtil.getArchieJAXBContext().createMarshaller();
        StringWriter writer = new StringWriter();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(archetype, writer);
        
        assertTrue(writer.toString().contains("<rm_overlay>\n" +
                "        <rm_visibility>\n" +
                "            <path>/subject</path>\n" +
                "            <visibility>hide</visibility>\n" +
                "            <alias>\n" +
                "                <terminology_id>local</terminology_id>\n" +
                "                <code_string>ad12</code_string>\n" +
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

        Unmarshaller unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Archetype unmarshalled = (Archetype) unmarshaller.unmarshal(new StringReader(writer.toString()));

        assertEquals(VisibilityType.HIDE, unmarshalled.getRmOverlay().getRmVisibility().get("/subject").getVisibility());
        assertEquals("ad12", unmarshalled.getRmOverlay().getRmVisibility().get("/subject").getAlias().getCodeString());

    }


}
