package com.nedap.archie.query;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.composition.EventContext;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datavalues.DataValue;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.nedap.archie.query.RMObjectAttributes.getAttributeValueFromRMObject;
import static org.junit.Assert.assertSame;

public class RMObjectAttributesTest {

    private TestUtil testUtil;
    private Archetype archetype;
    private Pathable root;

    @Before
    public void setup() throws Exception {
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
        archetype = new ADLParser(new RMConstraintImposer()).parse(getClass().getResourceAsStream("/basic.adl"));
        testUtil = new TestUtil();
    }

    @Test
    public void testGetAttributeValueFromRMObject() {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        ModelInfoLookup modelInfoLookup = ArchieRMInfoLookup.getInstance();

        EventContext expectedContext = composition.getContext();
        Object actualContext = getAttributeValueFromRMObject(composition, "context", modelInfoLookup);
        assertSame(expectedContext, actualContext);

        ItemStructure expectedOtherContext = expectedContext.getOtherContext();
        Object actualOtherContext = getAttributeValueFromRMObject(actualContext, "other_context", modelInfoLookup);
        assertSame(expectedOtherContext, actualOtherContext);

        List expectedItems = expectedOtherContext.getItems();
        Object actualItems = getAttributeValueFromRMObject(actualOtherContext, "items", modelInfoLookup);
        assertSame(expectedItems, actualItems);

        Cluster cluster = (Cluster) expectedItems.get(0);

        List expectedClusterItems = cluster.getItems();
        Object actualClusterItems = getAttributeValueFromRMObject(cluster, "items", modelInfoLookup);
        assertSame(expectedClusterItems, actualClusterItems);

        Element element = (Element) expectedClusterItems.get(1);

        DataValue expectedValue = element.getValue();
        Object actualValue = getAttributeValueFromRMObject(element, "value", modelInfoLookup);
        assertSame(expectedValue,actualValue);
    }
}
