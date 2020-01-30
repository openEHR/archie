package com.nedap.archie.diff;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.flattener.specexamples.FlattenerTestUtil;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import com.nedap.archie.testutil.TestUtil;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.referencemodels.BuiltinReferenceModels;

import static org.junit.Assert.assertEquals;

public class DiffTestUtil {

    //package where the archetypes are located
    private String archetypesResourceLocation;
    //package where explicit expected diff results are stored
    private String expectationsResourceLocation;

    private InMemoryFullArchetypeRepository repository;
    private MetaModels models;

    public DiffTestUtil(String archetypesResourceLocation, String expectationsResourceLocation) {
        this.archetypesResourceLocation = archetypesResourceLocation;
        this.expectationsResourceLocation = expectationsResourceLocation;
        repository = new InMemoryFullArchetypeRepository();
        models = new MetaModels(BuiltinReferenceModels.getAvailableModelInfoLookups(), (BmmRepository) null);
    }

    public void test(String parentFileName, String childFileName) throws Exception {
        Archetype parent = FlattenerTestUtil.parse(archetypesResourceLocation + parentFileName);
        repository.addArchetype(parent);
        Archetype child = FlattenerTestUtil.parse(archetypesResourceLocation + childFileName);
        Archetype flattened = new Flattener(repository, models).flatten(child);
        assertEquals(child.getParentArchetypeId(), flattened.getParentArchetypeId());

        Archetype diffed = new Differentiator(BuiltinReferenceModels.getMetaModels()).differentiate(flattened, parent);
        child.setGenerated(true);//this is set by the diff tool :)
        String originalSerialized = ADLArchetypeSerializer.serialize(child);
        String diffedSerialized = ADLArchetypeSerializer.serialize(diffed);

        assertEquals(originalSerialized, diffedSerialized);

        TestUtil.assertCObjectEquals(child.getDefinition(), diffed.getDefinition());
        //now a byte-for-byte serialized comparison

    }

    public void testWithExplicitExpect(String parentFileName, String childFileName) throws Exception {
        Archetype parent = FlattenerTestUtil.parse(archetypesResourceLocation + parentFileName);
        repository.addArchetype(parent);
        Archetype child = FlattenerTestUtil.parse(archetypesResourceLocation + childFileName);
        Archetype expectedDiff = FlattenerTestUtil.parse(expectationsResourceLocation + childFileName);
        Archetype flattened = new Flattener(repository, models).flatten(child);
        assertEquals(child.getParentArchetypeId(), flattened.getParentArchetypeId());

        Archetype diffed = new Differentiator(BuiltinReferenceModels.getMetaModels()).differentiate(flattened, parent);
        expectedDiff.setGenerated(true);//this is set by the diff tool :)
        String expectedSerialized = ADLArchetypeSerializer.serialize(expectedDiff);
        String diffedSerialized = ADLArchetypeSerializer.serialize(diffed);

        assertEquals(expectedSerialized, diffedSerialized);

        TestUtil.assertCObjectEquals(expectedDiff.getDefinition(), diffed.getDefinition());

    }
}
