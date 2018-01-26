package com.nedap.archie.xml;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerTest;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by pieter.bos on 22/07/16.
 */
public class JAXBAOMRoundTripTest {

    private ADLParser parser;

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private StringWriter writer;

    @Before
    public void setup() throws Exception {
        parser = new ADLParser(); //no constraints imposer in this test!
        writer = new StringWriter();
        marshaller = createMarshaller();
        unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller marshaller = JAXBUtil.getArchieJAXBContext().createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }

    @Test
    public void dataValues() throws Exception {
        Archetype archetype = parser.parse(JAXBRMRoundTripTest.class.getResourceAsStream("/com/nedap/archie/json/openEHR-EHR-CLUSTER.datavalues.v1.adls"));

        String xml = marshal(archetype);
        System.out.println(xml);

        Archetype parsedArchetype = (Archetype) unmarshaller.unmarshal(new StringReader(xml));

        assertThat(parsedArchetype.getTerminology().getTermDefinitions().size(), is(archetype.getTerminology().getTermDefinitions().size()));
        TestUtil.assertCObjectEquals(archetype.getDefinition(), parsedArchetype.getDefinition());

    }

    private String marshal(Archetype archetype ) throws JAXBException {
        writer = new StringWriter();
        marshaller.marshal(archetype, writer);
        return writer.toString();
    }

    @Test
    public void operationalTemplate() throws Exception {

        // reportresult specializes report.
        // blood pressure composition specializes report result.

        // it adds a blood pressure observation
        // it also adds a device
        // it contains specific template overlays for both blood pressure observation and device
        Archetype report = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("/com/nedap/archie/flattener/openEHR-EHR-COMPOSITION.report.v1.adls"));
        Archetype reportResult = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("/com/nedap/archie/flattener/openEHR-EHR-COMPOSITION.report-result.v1.adls"));
        Archetype device = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("/com/nedap/archie/flattener/openEHR-EHR-CLUSTER.device.v1.adls"));

        Archetype bloodPressureObservation = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.blood_pressure.v1.adls"));
        Archetype bloodPressureComposition = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("/com/nedap/archie/flattener/openEHR-EHR-COMPOSITION.blood_pressure.v1.0.0.adlt"));


        Archetype height = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.height.v1.adls"));
        Archetype heightTemplate = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("/com/nedap/archie/flattener/openEHR-EHR-COMPOSITION.length.v1.0.0.adlt"));

        SimpleArchetypeRepository repository = new SimpleArchetypeRepository();
        repository.addArchetype(report);
        repository.addArchetype(device);
        repository.addArchetype(bloodPressureComposition);
        repository.addArchetype(bloodPressureObservation);
        repository.addArchetype(reportResult);
        repository.addArchetype(height);
        repository.addArchetype(heightTemplate);

        Flattener flattener = new Flattener(repository, TestUtil.getReferenceModels()).createOperationalTemplate(true);
        Archetype operationalTemplate = flattener.flatten(bloodPressureComposition);
        String xml = marshal(operationalTemplate);
        System.out.println(xml);

        Archetype parsedArchetype = (Archetype) unmarshaller.unmarshal(new StringReader(xml));
        TestUtil.assertCObjectEquals(operationalTemplate.getDefinition(), parsedArchetype.getDefinition());
    }

}
