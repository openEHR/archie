package com.nedap.archie.serializer.adl;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerTest;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author markopi
 */
public class ADLArchetypeSerializerParserRoundtripTest {

    private static final Logger logger = LoggerFactory.getLogger(ADLArchetypeSerializer.class);

    @Test
    public void basic() throws Exception {
        Archetype basic = loadRoot("basic.adl");
        Archetype archetype = roundtrip(basic);

        CAttribute defining_code = archetype.itemAtPath("/category[id10]/defining_code");
        CTerminologyCode termCode = (CTerminologyCode) defining_code.getChildren().get(0);

        assertThat(termCode.getConstraint(), hasItem("at1"));
        assertThat(archetype.getDescription().getDetails().get("en").getKeywords(), hasItems("ADL", "test"));
        assertThat(archetype.getTerminology().getTermBinding("openehr", "at1"),
                equalTo(URI.create("http://openehr.org/id/433")));

        assertThat(archetype.getAnnotations().getDocumentation().get("en").get("/context/start_time").get("local_name"),
                equalTo("consultation start time"));

        TestUtil.assertCObjectEquals(basic.getDefinition(), archetype.getDefinition());
    }

    @Test
    public void device() throws Exception {
        Archetype archetype = roundtrip(load("openEHR-EHR-CLUSTER.device.v1.adls"));

        CComplexObject dvDeviceId = archetype.itemAtPath("/items[id22]/value[id32]");
        assertThat(dvDeviceId.getRmTypeName(), equalTo("DV_IDENTIFIER"));

        assertThat(archetype.getDescription().getOriginalAuthor().get("name"), equalTo("Heather Leslie"));
    }

    @Test
    public void escapeQuotes() throws Exception {
        Archetype archetype = load("openEHR-EHR-COMPOSITION.report.v1.adls");
        archetype.getDescription().setLicence("license with a \"-mark");
        String serialized = ADLArchetypeSerializer.serialize(archetype);

        Assert.assertThat(serialized, containsString("license with a \\\"-mark" ));
        Archetype parsed = new ADLParser().parse(serialized);
        Assert.assertThat(parsed.getDescription().getLicence(), is("license with a \"-mark" ));
    }

    @Test
    public void escapeQuotes2() throws Exception {
        Archetype archetype = load("openEHR-EHR-COMPOSITION.report.v1.adls");
        archetype.getDescription().setLicence("license with a \\-mark");
        String serialized = ADLArchetypeSerializer.serialize(archetype);

        Assert.assertThat(serialized, containsString("license with a \\\\-mark" ));
        Archetype parsed = new ADLParser().parse(serialized);
        Assert.assertThat(parsed.getDescription().getLicence(), is("license with a \\-mark" ));
    }

    @Test
    public void escapeQuotes3() throws Exception {
        Archetype archetype = load("openEHR-EHR-COMPOSITION.report.v1.adls");
        archetype.getDescription().setLicence("license with a \\\"-mark");
        String serialized = ADLArchetypeSerializer.serialize(archetype);

        Assert.assertThat(serialized, containsString("license with a \\\\\\\"-mark" ));
        Archetype parsed = new ADLParser().parse(serialized);
        Assert.assertThat(parsed.getDescription().getLicence(), is("license with a \\\"-mark" ));
    }

    private Archetype roundtrip(Archetype archetype) throws IOException {
        String serialized = ADLArchetypeSerializer.serialize(archetype);
        logger.info(serialized);

        ADLParser parser = new ADLParser();
        Archetype result = parser.parse(serialized);

        assertTrue("roundtrip parsing should never cause errors: " + parser.getErrors().toString(), parser.getErrors().hasNoErrors());
        return result;
    }

    private Archetype load(String resourceName) throws IOException {
        return new ADLParser().parse(ADLArchetypeSerializerTest.class.getResourceAsStream(resourceName));
    }

    private Archetype loadRoot(String resourceName) throws IOException {
        return new ADLParser().parse(ADLArchetypeSerializerTest.class.getClassLoader().getResourceAsStream(resourceName));
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

        Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModels()).createOperationalTemplate(true);
        Archetype operationalTemplate = flattener.flatten(bloodPressureComposition);
        Archetype parsed = roundtrip(operationalTemplate);
        TestUtil.assertCObjectEquals(operationalTemplate.getDefinition(), parsed.getDefinition());
    }

}
