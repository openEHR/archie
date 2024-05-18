package com.nedap.archie.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;

import com.nedap.archie.query.RMQueryContext;
import com.nedap.archie.base.RMObject;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.datavalues.DvURI;
import org.openehr.rm.datavalues.quantity.DvQuantity;
import org.openehr.rm.datavalues.quantity.datetime.DvDate;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.datavalues.quantity.datetime.DvTime;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.UIDBasedId;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.openehr.serialisation.json.OpenEhrRmJacksonUtil;
import com.nedap.archie.testutil.TestUtil;
import com.nedap.archie.openehr.serialisation.xml.OpenEhrRmJAXBUtil;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests JSON serialization and deserialization of RM Objects using Jackson
 *
 * Created by pieter.bos on 30/06/16.
 */
public class JacksonRMRoundTripTest {

    private ADLParser parser;
    private Archetype archetype;

    private TestUtil testUtil;

    @Before
    public void setup() {
        testUtil = new TestUtil(OpenEhrRmInfoLookup.getInstance());
        parser = new ADLParser(new RMConstraintImposer());
    }

    @Test
    public void dataValues() throws Exception {
        archetype = parser.parse(JacksonRMRoundTripTest.class.getResourceAsStream("openEHR-EHR-CLUSTER.datavalues.v1.adls"));
        Cluster cluster =  (Cluster) testUtil.constructEmptyRMObject(archetype.getDefinition());
        UIDBasedId uid = new HierObjectId();
        uid.setValue("SOME_UUID");
        cluster.setUid(uid);
        RMQueryContext queryContext = getQueryContext(cluster);
        DvText text = queryContext.find("/items['Text']/value");
        text.setValue("test-text");
        DvQuantity quantity = queryContext.find("/items['Quantity']/value");
        quantity.setMagnitude(23d);
        DvDate date = queryContext.find("/items['Date']/value");
        date.setValue(LocalDate.of(2016, 1, 1));

        DvDateTime datetime = queryContext.find("/items['Datetime']/value");
        datetime.setValue(LocalDateTime.of(2016, 1, 1, 12, 00));

        DvTime time = queryContext.find("/items['Time']/value");
        time.setValue(LocalTime.of(12, 0));

        DvURI uri = queryContext.find("/items['Uri']/value");
        uri.setValue(URI.create("http://test.example.com"));

        String json = OpenEhrRmJacksonUtil.getObjectMapper().writeValueAsString(cluster);
        System.out.println(json);
        Cluster parsedCluster = (Cluster) OpenEhrRmJacksonUtil.getObjectMapper().readValue(json, Cluster.class);
        RMQueryContext parsedQueryContext = getQueryContext(parsedCluster);

        assertThat(parsedQueryContext.<DvText>find("/items['Text']/value").getValue(), is("test-text"));
        assertThat(parsedQueryContext.<DvQuantity>find("/items['Quantity']/value").getMagnitude(), is(23d));
        assertThat(parsedQueryContext.<DvDate>find("/items['Date']/value").getValue(), is(LocalDate.of(2016, 1, 1)));
        assertThat(parsedQueryContext.<DvDateTime>find("/items['Datetime']/value").getValue(), is(LocalDateTime.of(2016, 1, 1, 12, 00)));
        assertThat(parsedQueryContext.<DvTime>find("/items['Time']/value").getValue(), is(LocalTime.of(12, 0)));
        assertThat(parsedQueryContext.<DvURI>find("/items['Uri']/value").getValue(), is(URI.create("http://test.example.com")));
        assertThat(parsedCluster.getUid().getValue(), is("SOME_UUID"));
        assertThat(parsedCluster.getArchetypeNodeId(), is("id1"));

    }

    private RMQueryContext getQueryContext(Cluster cluster) {
        return new RMQueryContext(OpenEhrRmInfoLookup.getInstance(), cluster, OpenEhrRmJAXBUtil.getArchieJAXBContext());
    }

    @Test
    public void composition() throws Exception {
        Composition composition = new Composition();
        composition.setCategory("persistent", "openEhr::123");
        composition.setTerritory("openEhr::456");
        composition.setLanguage("openEhr::nl");
        ArchieJacksonConfiguration config = ArchieJacksonConfiguration.createStandardsCompliant();
        //include the type property name so we can parse it as an RmObject here.
        config.setAlwaysIncludeTypeProperty(true);

        ObjectMapper objectMapper = OpenEhrRmJacksonUtil.getObjectMapper();
        String json = objectMapper.writeValueAsString(composition);

        Composition parsedComposition = (Composition) objectMapper.readValue(json, Composition.class);
        assertEquals(composition.getCategory().getDefiningCode().getCodeString(), parsedComposition.getCategory().getDefiningCode().getCodeString());
        assertEquals(composition.getLanguage().getCodeString(), parsedComposition.getLanguage().getCodeString());
        assertEquals(composition.getTerritory().getCodeString(), parsedComposition.getTerritory().getCodeString());

    }

    /**
     * Parse a 0.5.5 archie generated json and make sure it parses
     */
    @Test
    public void check055BackwardsCompatibility() throws Exception {
        InputStream stream = getClass().getResourceAsStream("rm_object.json");
        ArchieJacksonConfiguration standardsCompliant = ArchieJacksonConfiguration.createStandardsCompliant();
        //unfortunately, we cannot handle two different type propety names
        //that is sort of possible in jackson, but would require overriding internal jackson classes
        standardsCompliant.setTypePropertyName("@type");
        OpenEhrRmJacksonUtil.getObjectMapper(standardsCompliant).readValue(stream, RMObject.class);

    }


}
