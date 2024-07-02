package com.nedap.archie.diff;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class AnnotationDifferentiatorTest {

    private Archetype flatParent;
    private Archetype child;

    @Before
    public void setup() throws Exception {
        flatParent = TestUtil.parseFailOnErrors("/com/nedap/archie/diff/openEHR-EHR-OBSERVATION.basic_annotations.v1.0.0.adls");
        child = TestUtil.parseFailOnErrors("/com/nedap/archie/diff/openEHR-EHR-OBSERVATION.annotations_specialised_flattened.v1.0.0.adls");
    }

    @Test
    public void noAdditionalAnnotationsTest() {
        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNull(child.getAnnotations());
    }

    @Test
    public void extraAnnotationsAtLevel1Test() {
        // Setup
        Map<String, String> pathMap = Map.of("newKey", "newValue");
        Map<String, Map<String, String>> languageMap = Map.of("/data[id2]/events[id3]/data[id4]/items[id5]", pathMap);
        child.getAnnotations().getDocumentation().put("en", languageMap);

        // Test
        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNotNull(child.getAnnotations());
        Map<String, Map<String, Map<String, String>>> annotations = child.getAnnotations().getDocumentation();
        assertEquals(1, annotations.size());
        assertEquals(1, annotations.get("en").size());
        assertEquals(1, annotations.get("en").get("/data[id2]/events[id3]/data[id4]/items[id5]").size());
        assertEquals("newValue", annotations.get("en").get("/data[id2]/events[id3]/data[id4]/items[id5]").get("newKey"));
    }

    @Test
    public void extraAnnotationsAtLevel2Test() {
        // Setup
        Map<String, String> pathMap = Map.of("newKey", "newValue");
        child.getAnnotations().getDocumentation().get("nl").put("/data[id2]/events[id3]/data[id4]/items[id5]", pathMap);

        // Test
        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNotNull(child.getAnnotations());
        Map<String, Map<String, Map<String, String>>> annotations = child.getAnnotations().getDocumentation();
        assertEquals(1, annotations.size());
        assertEquals(1, annotations.get("nl").size());
        assertEquals(1, annotations.get("nl").get("/data[id2]/events[id3]/data[id4]/items[id5]").size());
        assertEquals("newValue", annotations.get("nl").get("/data[id2]/events[id3]/data[id4]/items[id5]").get("newKey"));
    }

    @Test
    public void extraAnnotationsAtLevel3Test() {
        // Setup
        child.getAnnotations().getDocumentation().get("nl").get("/data[id2]/events[id3]/data[id4]/items[id7]/value[id8]").put("newKey", "newValue");

        // Test
        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNotNull(child.getAnnotations());
        Map<String, Map<String, Map<String, String>>> annotations = child.getAnnotations().getDocumentation();
        assertEquals(1, annotations.size());
        assertEquals(1, annotations.get("nl").size());
        assertEquals(1, annotations.get("nl").get("/data[id2]/events[id3]/data[id4]/items[id7]/value[id8]").size());
        assertEquals("newValue", annotations.get("nl").get("/data[id2]/events[id3]/data[id4]/items[id7]/value[id8]").get("newKey"));
    }

    @Test
    public void changeValueAtLvl3() {
        // Setup
        child.getAnnotations().getDocumentation().get("nl").get("/data[id2]/events[id3]/data[id4]/items[id7]/value[id8]").put("hideHeader", "updated");

        // Test
        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNotNull(child.getAnnotations());
        Map<String, Map<String, Map<String, String>>> annotations = child.getAnnotations().getDocumentation();
        assertEquals(1, annotations.size());
        assertEquals(1, annotations.get("nl").size());
        assertEquals(1, annotations.get("nl").get("/data[id2]/events[id3]/data[id4]/items[id7]/value[id8]").size());
        assertEquals("updated", annotations.get("nl").get("/data[id2]/events[id3]/data[id4]/items[id7]/value[id8]").get("hideHeader"));
    }

    @Test
    public void extraAnnotationsAtAllLevelsTest() {
        // Setup
        Map<String, String> pathMap = Map.of("newKey", "newValue");
        Map<String, Map<String, String>> languageMap = Map.of("/data[id2]/events[id3]/data[id4]/items[id5]", pathMap);
        child.getAnnotations().getDocumentation().put("en", languageMap);
        child.getAnnotations().getDocumentation().get("nl").put("/data[id2]/events[id3]/data[id4]/items[id5]", pathMap);
        child.getAnnotations().getDocumentation().get("nl").get("/data[id2]/events[id3]/data[id4]/items[id7]/value[id8]").put("newKey", "newValue");

        // Test
        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNotNull(child.getAnnotations());
        Map<String, Map<String, Map<String, String>>> annotations = child.getAnnotations().getDocumentation();
        assertEquals(2, annotations.size());
        assertEquals(2, annotations.get("nl").size());
        assertEquals(1, annotations.get("nl").get("/data[id2]/events[id3]/data[id4]/items[id7]/value[id8]").size());
        assertEquals(1, annotations.get("nl").get("/data[id2]/events[id3]/data[id4]/items[id5]").size());
        assertEquals(1, annotations.get("en").size());
        assertEquals(1, annotations.get("en").get("/data[id2]/events[id3]/data[id4]/items[id5]").size());
    }

    @Test
    public void childHasNoAnnotationsTest() {
        // Setup
        child.setAnnotations(null);

        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNull(child.getAnnotations());
    }

    @Test
    public void childHasNoDocumentationTest() {
        // Setup
        child.getAnnotations().setDocumentation(null);

        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNull(child.getAnnotations());
    }

    @Test
    public void parentHasNoAnnotationsTest() {
        // Setup
        flatParent.setAnnotations(null);

        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNotNull(child.getAnnotations());
    }

    @Test
    public void parentHasNoDocumentationTest() {
        // Setup
        flatParent.getAnnotations().setDocumentation(null);

        new AnnotationDifferentiator().differentiate(child, flatParent);

        assertNotNull(child.getAnnotations());
    }
}
