package com.nedap.archie.query;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.EventContext;
import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.Item;
import org.openehr.rm.datastructures.ItemStructure;
import org.openehr.rm.datavalues.DataValue;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
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
        testUtil = new TestUtil(OpenEhrRmInfoLookup.getInstance());
    }

    @Test
    public void testGetAttributeValueFromRMObject() {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        ModelInfoLookup modelInfoLookup = OpenEhrRmInfoLookup.getInstance();

        EventContext expectedContext = composition.getContext();
        Object actualContext = getAttributeValueFromRMObject(composition, "context", modelInfoLookup);
        assertSame(expectedContext, actualContext);

        ItemStructure expectedOtherContext = expectedContext.getOtherContext();
        Object actualOtherContext = getAttributeValueFromRMObject(actualContext, "other_context", modelInfoLookup);
        assertSame(expectedOtherContext, actualOtherContext);

        List<?> expectedItems = expectedOtherContext.getItems();
        Object actualItems = getAttributeValueFromRMObject(actualOtherContext, "items", modelInfoLookup);
        assertSame(expectedItems, actualItems);

        Cluster cluster = (Cluster) expectedItems.get(0);

        List<Item> expectedClusterItems = cluster.getItems();
        Object actualClusterItems = getAttributeValueFromRMObject(cluster, "items", modelInfoLookup);
        assertSame(expectedClusterItems, actualClusterItems);

        Element element = (Element) expectedClusterItems.get(1);

        DataValue expectedValue = element.getValue();
        Object actualValue = getAttributeValueFromRMObject(element, "value", modelInfoLookup);
        assertSame(expectedValue,actualValue);
    }
}
