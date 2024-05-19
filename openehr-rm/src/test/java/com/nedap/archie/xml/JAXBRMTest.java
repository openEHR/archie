package com.nedap.archie.xml;

import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.datavalues.quantity.datetime.DvDuration;
import com.nedap.archie.openehr.serialisation.xml.OpenEhrRmJAXBUtil;
import org.junit.Test;
import org.threeten.extra.PeriodDuration;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JAXBRMTest {

    @Test
    public void serializeDuration() throws Exception {
        Element element = new Element("id6",
                new DvText("duration"),
                new DvDuration(Period.parse("P10D")));
        StringWriter writer = new StringWriter();
        Marshaller marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
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
        Marshaller marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(element, writer);
        String xml = writer.toString();
        assertTrue(xml, xml.contains("-P10D"));
    }

    @Test
    public void serializeDvDateTime() throws Exception {
        Element fullDateElement = new Element("id6",
                new DvText("DvDateTime"),
                new DvDateTime(LocalDateTime.of(2015, 1, 1, 12, 10, 12, 0)));
        StringWriter writer = new StringWriter();
        Marshaller marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(fullDateElement, writer);
        String xml = writer.toString();
        assertTrue(xml.contains("2015-01-01T12:10:12"));
        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Element unmarshalled = (Element) unmarshaller.unmarshal(new StringReader(xml));
        assertEquals(fullDateElement, unmarshalled);

        Element yearMonthDayelement = new Element("id6",
                new DvText("DvDateTime"),
                new DvDateTime(LocalDate.of(2015, 1, 1)));
        writer = new StringWriter();
        marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(yearMonthDayelement, writer);
        xml = writer.toString();
        assertTrue(xml.contains("2015-01-01"));
        unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        unmarshalled = (Element) unmarshaller.unmarshal(new StringReader(xml));
        assertEquals(yearMonthDayelement, unmarshalled);

        Element yearMonthElement = new Element("id6",
                new DvText("DvDateTime"),
                new DvDateTime(YearMonth.of(2015, 1)));
        writer = new StringWriter();
        marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(yearMonthElement, writer);
        xml = writer.toString();
        assertTrue(xml.contains("2015-01"));
        unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        unmarshalled = (Element) unmarshaller.unmarshal(new StringReader(xml));
        assertEquals(yearMonthElement, unmarshalled);

        Element yearElement = new Element("id6",
                new DvText("DvDateTime"),
                new DvDateTime(Year.of(2015)));
        writer = new StringWriter();
        marshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(yearElement, writer);
        xml = writer.toString();
        assertTrue(xml.contains("2015"));
        unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        unmarshalled = (Element) unmarshaller.unmarshal(new StringReader(xml));
        assertEquals(yearElement, unmarshalled);
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
        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
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
        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
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
        Unmarshaller unmarshaller = OpenEhrRmJAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Element unmarshalled = (Element) unmarshaller.unmarshal(new StringReader(xml));
        assertEquals(PeriodDuration.parse("-P10YT12H"), ((DvDuration)unmarshalled.getValue()).getValue());
    }
}
