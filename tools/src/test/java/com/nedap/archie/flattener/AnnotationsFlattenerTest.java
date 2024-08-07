package com.nedap.archie.flattener;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ResourceAnnotations;
import com.nedap.archie.rminfo.ReferenceModels;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AnnotationsFlattenerTest {

    private Archetype parent;
    private Archetype child;
    private Archetype clusterWithAnnotations;
    private Archetype withUsedArchetype;

    private SimpleArchetypeRepository repository;

    private Flattener flattener;

    private ReferenceModels models;

    @Before
    public void setup() throws Exception {
        models = BuiltinReferenceModels.getAvailableModelInfoLookups();

        parent = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.to_flatten_parent_with_annotations.v1.adls"));
        child = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.to_flatten_child_with_annotations.v1.adls"));
        clusterWithAnnotations = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-CLUSTER.cluster_with_annotations.v1.adls"));
        withUsedArchetype = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.with_used_archetype.v1.adls"));

        repository = new SimpleArchetypeRepository();
        repository.addArchetype(parent);
        repository.addArchetype(child);
        repository.addArchetype(clusterWithAnnotations);
        repository.addArchetype(withUsedArchetype);
    }

    @Test
    public void flattenAnnotationsOperationalTemplate() throws Exception {

        flattener = new Flattener(repository, models).createOperationalTemplate(true);

        Archetype flattened = flattener.flatten(child);
        ResourceAnnotations annotations = flattened.getAnnotations();
        assertEquals("xxxxxx", annotations.getDocumentation().get("en").get("/subject").get("design note"));
        assertEquals("this is a design note on allergic reaction, with some extra information", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id5]").get("design note"));
        assertEquals("this is a design note on intelerance", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id6]").get("design note"));
        assertEquals("this is also a design note on intelerance", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id7]").get("design note"));

    }

    @Test
    public void flattenParentAnnotationsOperationalTemplate() throws Exception{

        flattener = new Flattener(repository, models).createOperationalTemplate(true);

        Archetype flattened = flattener.flatten(parent);
        ResourceAnnotations annotations = flattened.getAnnotations();
        assertEquals("xxxxxx", annotations.getDocumentation().get("en").get("/subject").get("design note"));
        assertEquals("this is a design note on allergic reaction", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id5]").get("design note"));
        assertEquals("this is a design note on intelerance", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id6]").get("design note"));

    }

    @Test
    public void flattenAnnotations() throws Exception {

        flattener = new Flattener(repository, models);

        Archetype flattened = flattener.flatten(child);
        ResourceAnnotations annotations = flattened.getAnnotations();
        assertEquals("xxxxxx", annotations.getDocumentation().get("en").get("/subject").get("design note"));
        assertEquals("this is a design note on allergic reaction, with some extra information", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id5]").get("design note"));
        assertEquals("this is a design note on intelerance", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id6]").get("design note"));
        assertEquals("this is also a design note on intelerance", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id7]").get("design note"));

    }

    @Test
    public void flattenParentAnnotations() throws Exception {

        flattener = new Flattener(repository, models);

        Archetype flattened = flattener.flatten(parent);
        ResourceAnnotations annotations = flattened.getAnnotations();
        assertEquals("xxxxxx", annotations.getDocumentation().get("en").get("/subject").get("design note"));
        assertEquals("this is a design note on allergic reaction", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id5]").get("design note"));
        assertEquals("this is a design note on intelerance", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id6]").get("design note"));

    }

    @Test
    public void flattenUsedArchetypeWithAnnotations() throws Exception {

        flattener = new Flattener(repository, models).createOperationalTemplate(true);

        Archetype flattend = flattener.flatten(withUsedArchetype);
        ResourceAnnotations annotations = flattend.getAnnotations();
        assertEquals("xxxxxx", annotations.getDocumentation().get("en").get("/subject").get("design note"));
        assertEquals("this is a design note on allergic reaction", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id5]").get("design note"));
        assertEquals("this is a design note on intelerance", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id6]").get("design note"));
        assertEquals("this is a design note for a cluster", annotations.getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id8]/items[id3]/value[id4]").get("design note"));
    }

    @Test
    public void flattenArchetypeWithZeroOccurrencesObject() throws Exception {
        Archetype childWithExcluded = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.to_flatten_child_with_annotations_exclude_object.v1.adls"));
        repository.addArchetype(childWithExcluded);

        flattener = new Flattener(repository, models).createOperationalTemplate(false);

        Archetype flattened = flattener.flatten(childWithExcluded);
        assertNull(flattened.getAnnotations().getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id6]"));
    }

    @Test
    public void flattenArchetypeWithZeroOccurrencesObjectOPT() throws Exception {
        Archetype childWithExcluded = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.to_flatten_child_with_annotations_exclude_object.v1.adls"));
        repository.addArchetype(childWithExcluded);

        flattener = new Flattener(repository, models).createOperationalTemplate(true);

        Archetype flattened = flattener.flatten(childWithExcluded);
        assertNull(flattened.getAnnotations().getDocumentation().get("en").get("/data[id2]/events[id3]/data[id4]/items[id6]"));
    }

    @Test
    public void flattenArchetypeWithNoAnnotations() throws Exception {
        parent.setAnnotations(null);
        repository.addArchetype(parent);
        Archetype childWithExcluded = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.to_flatten_child_with_annotations_exclude_object.v1.adls"));
        childWithExcluded.setAnnotations(null);
        repository.addArchetype(childWithExcluded);

        flattener = new Flattener(repository, models).createOperationalTemplate(true);

        Archetype flattened = flattener.flatten(childWithExcluded);
        assertNull(flattened.getAnnotations());
    }

    @Test
    public void flattenArchetypeWithNoDocumentation() throws Exception {
        parent.setAnnotations(new ResourceAnnotations());
        repository.addArchetype(parent);
        Archetype childWithExcluded = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.to_flatten_child_with_annotations_exclude_object.v1.adls"));
        childWithExcluded.setAnnotations(new ResourceAnnotations());
        repository.addArchetype(childWithExcluded);

        flattener = new Flattener(repository, models).createOperationalTemplate(true);

        Archetype flattened = flattener.flatten(childWithExcluded);
        assertNull(flattened.getAnnotations().getDocumentation());
    }

    @Test
    public void flattenArchetypeWithEmptyDocumentation() throws Exception {
        parent.getAnnotations().setDocumentation(new HashMap<>());
        repository.addArchetype(parent);
        Archetype childWithExcluded = new ADLParser().parse(FlattenerTest.class.getResourceAsStream("openEHR-EHR-OBSERVATION.to_flatten_child_with_annotations_exclude_object.v1.adls"));
        childWithExcluded.getAnnotations().setDocumentation(new HashMap<>());
        repository.addArchetype(childWithExcluded);

        flattener = new Flattener(repository, models).createOperationalTemplate(true);

        Archetype flattened = flattener.flatten(childWithExcluded);
        assertEquals(0, flattened.getAnnotations().getDocumentation().size());
    }
}