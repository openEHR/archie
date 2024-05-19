package com.nedap.archie.rmutil;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.openehr.rmutil.PathableUtil;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.query.RMQueryContext;
import org.openehr.rm.archetyped.Pathable;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.ItemTree;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import com.nedap.archie.openehr.serialisation.xml.OpenEhrRmJAXBUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PathableUtilTest {

    private TestUtil testUtil;
    private Archetype archetype;
    private Pathable root;

    @Before
    public void setup() throws Exception {
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
        archetype = new ADLParser(new RMConstraintImposer()).parse(getClass().getResourceAsStream("/basic.adl"));
        testUtil = new TestUtil(OpenEhrRmInfoLookup.getInstance());
    }

    private RMQueryContext getQueryContext() {
        return new RMQueryContext(OpenEhrRmInfoLookup.getInstance(), root, OpenEhrRmJAXBUtil.getArchieJAXBContext());
    }

    @Test
    public void withSimplePaths() throws Exception {
        root = (Pathable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        Composition composition = (Composition) root;

        {
            //add another cluster to the RM Object, with the same archetype id (very possible!)
            Composition composition2 = (Composition) testUtil.constructEmptyRMObject(archetype.getDefinition());
            Cluster secondCluster = (Cluster) composition2.getContext().getOtherContext().getItems().get(0);
            ItemTree clusterList = (ItemTree) composition.getContext().getOtherContext();
            clusterList.addItem(secondCluster);
        }

        RMQueryContext queryContext = getQueryContext();

        ModelInfoLookup modelInfoLookup = OpenEhrRmInfoLookup.getInstance();

        List<RMObjectWithPath> context = new RMPathQuery("/context").findList(modelInfoLookup, composition);
        assertEquals(1, context.size());
        assertEquals("/context", context.get(0).getPath());
        assertEquals("/context",
                PathableUtil.getUniquePath((Pathable) context.get(0).getObject()));

        //now check that retrieving this retrieves more than one, even with the same ID.
        List<RMObjectWithPath> items = queryContext.findListWithPaths("/context/other_context[id2]/items");
        assertEquals(2, items.size());
        assertEquals("/context/other_context[id2]/items[id3,1]", items.get(0).getPath());
        assertEquals("/context/other_context[id2]/items[id3,1]",
                PathableUtil.getUniquePath((Pathable) items.get(0).getObject()));
        assertEquals("/context/other_context[id2]/items[id3,2]", items.get(1).getPath());
        assertEquals("/context/other_context[id2]/items[id3,2]",
                PathableUtil.getUniquePath((Pathable) items.get(1).getObject()));
    }
}
