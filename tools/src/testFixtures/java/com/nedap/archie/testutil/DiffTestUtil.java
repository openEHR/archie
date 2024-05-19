package com.nedap.archie.testutil;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.diff.Differentiator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

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
        models = new MetaModels(AllMetaModelsInitialiser.getNativeRms(), (BmmRepository) null);
    }

    public void test(Class<?> caller, String parentFileName, String childFileName) throws Exception {
        Archetype parent = ParseValidArchetypeTestUtil.parse(caller, archetypesResourceLocation + parentFileName);
        repository.addArchetype(parent);
        Archetype child = ParseValidArchetypeTestUtil.parse(caller, archetypesResourceLocation + childFileName);
        Archetype flattened = new Flattener(repository, models).flatten(child);
        assertEquals(child.getParentArchetypeId(), flattened.getParentArchetypeId());

        Archetype diffed = new Differentiator(AllMetaModelsInitialiser.getMetaModels()).differentiate(flattened, parent);
        child.setGenerated(true);//this is set by the diff tool :)
        String originalSerialized = ADLArchetypeSerializer.serialize(child);
        String diffedSerialized = ADLArchetypeSerializer.serialize(diffed);

        assertEquals(originalSerialized, diffedSerialized);

        TestUtil.assertCObjectEquals(child.getDefinition(), diffed.getDefinition());
        //now a byte-for-byte serialized comparison

    }

    public void testWithExplicitExpect(Class<?> caller, String parentFileName, String childFileName) throws Exception {
        Archetype parent = ParseValidArchetypeTestUtil.parse(caller, archetypesResourceLocation + parentFileName);
        repository.addArchetype(parent);
        Archetype child = ParseValidArchetypeTestUtil.parse(caller, archetypesResourceLocation + childFileName);
        Archetype expectedDiff = ParseValidArchetypeTestUtil.parse(caller, expectationsResourceLocation + childFileName);
        Archetype flattened = new Flattener(repository, models).flatten(child);
        assertEquals(child.getParentArchetypeId(), flattened.getParentArchetypeId());

        Archetype diffed = new Differentiator(AllMetaModelsInitialiser.getMetaModels()).differentiate(flattened, parent);
        expectedDiff.setGenerated(true);//this is set by the diff tool :)
        String expectedSerialized = ADLArchetypeSerializer.serialize(expectedDiff);
        String diffedSerialized = ADLArchetypeSerializer.serialize(diffed);

        assertEquals(expectedSerialized, diffedSerialized);

        TestUtil.assertCObjectEquals(expectedDiff.getDefinition(), diffed.getDefinition());

    }
}
