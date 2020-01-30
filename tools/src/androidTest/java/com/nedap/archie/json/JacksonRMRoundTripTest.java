package com.nedap.archie.json;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;

import com.nedap.archie.query.RMQueryContext;
import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.DvURI;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDate;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvTime;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import com.nedap.archie.xml.JAXBUtil;
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
        testUtil = new TestUtil();
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

        String json = JacksonUtil.getObjectMapper().writeValueAsString(cluster);
        System.out.println(json);
        Cluster parsedCluster = (Cluster) JacksonUtil.getObjectMapper().readValue(json, RMObject.class);
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
        return new RMQueryContext(ArchieRMInfoLookup.getInstance(), cluster, JAXBUtil.getArchieJAXBContext());
    }

    @Test
    public void composition() throws Exception {
        Composition composition = new Composition();
        composition.setCategory("openEhr::123");
        composition.setTerritory("openEhr::456");
        composition.setLanguage("openEhr::nl");
        String json = JacksonUtil.getObjectMapper().writeValueAsString(composition);

        Composition parsedComposition = (Composition) JacksonUtil.getObjectMapper().readValue(json, RMObject.class);
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
        JacksonUtil.getObjectMapper().readValue(stream, RMObject.class);

    }


}
