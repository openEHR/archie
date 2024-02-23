package com.nedap.archie.xml;

import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDuration;
import org.junit.Test;
import org.threeten.extra.PeriodDuration;

import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Period;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JAXBRMTest {

    @Test
    public void serializeDuration() throws Exception {
        Element element = new Element("id6",
                new DvText("duration"),
                new DvDuration(Period.parse("P10D")));
        StringWriter writer = new StringWriter();
        Marshaller marshaller = JAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(element, writer);
        String xml = writer.toString();
        assertTrue(xml, xml.contains("P10D"));
    }

    @Test
    public void serializeNegativeDuration() throws Exception {
        Element element = new Element("id6",
                new DvText("duration"),
                new DvDuration(Period.parse("-P10D")));
        StringWriter writer = new StringWriter();
        Marshaller marshaller = JAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(element, writer);
        String xml = writer.toString();
        assertTrue(xml, xml.contains("-P10D"));
    }

    @Test
    public void parseDuration() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<element archetype_node_id=\"id6\" xmlns:ns2=\"http://schemas.openehr.org/v1\">\n" +
                "    <name>\n" +
                "        <value>duration</value>\n" +
                "    </name>\n" +
                "    <value xsi:type=\"DV_DURATION\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <value>P10D</value>\n" +
                "    </value>\n" +
                "</element>";
        Unmarshaller unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Element unmarshalled = (Element) unmarshaller.unmarshal(new StringReader(xml));
        assertEquals(Period.parse("P10D"), ((DvDuration)unmarshalled.getValue()).getValue());
    }

    @Test
    public void parseNegativeDuration() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<element archetype_node_id=\"id6\" xmlns:ns2=\"http://schemas.openehr.org/v1\">\n" +
                "    <name>\n" +
                "        <value>duration</value>\n" +
                "    </name>\n" +
                "    <value xsi:type=\"DV_DURATION\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <value>-P10D</value>\n" +
                "    </value>\n" +
                "</element>";
        Unmarshaller unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Element unmarshalled = (Element) unmarshaller.unmarshal(new StringReader(xml));
        assertEquals(Period.parse("-P10D"), ((DvDuration)unmarshalled.getValue()).getValue());
    }

    @Test
    public void parseNegativePeriodDuration() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<element archetype_node_id=\"id6\" xmlns:ns2=\"http://schemas.openehr.org/v1\">\n" +
                "    <name>\n" +
                "        <value>duration</value>\n" +
                "    </name>\n" +
                "    <value xsi:type=\"DV_DURATION\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <value>-P10YT12H</value>\n" +
                "    </value>\n" +
                "</element>";
        Unmarshaller unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Element unmarshalled = (Element) unmarshaller.unmarshal(new StringReader(xml));
        assertEquals(PeriodDuration.parse("-P10YT12H"), ((DvDuration)unmarshalled.getValue()).getValue());
    }
}
