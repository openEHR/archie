package com.nedap.archie.creation;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.composition.Observation;
import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datavalues.DvBoolean;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by pieter.bos on 10/05/2017.
 */
public class RMObjectCreatorTest {

    RMObjectCreator creator = new RMObjectCreator(OpenEhrRmInfoLookup.getInstance());

    @Test
    public void createElement() {
        Archetype archetype = new AuthoredArchetype();
        archetype.setTerminology(new ArchetypeTerminology());
        LinkedHashMap<String, ArchetypeTerm> termDefinitions = new LinkedHashMap<>();
        termDefinitions.put("id6", new ArchetypeTerm("id6", "text", "description"));
        archetype.getTerminology().getTermDefinitions().put("en", termDefinitions);

        CComplexObject elementConstraint = new CComplexObject();
        elementConstraint.setRmTypeName("ELEMENT");
        elementConstraint.setNodeId("id6");
        archetype.setDefinition(elementConstraint);

        Object o = creator.create(elementConstraint);
        assertEquals(Element.class, o.getClass());
        Element e = (Element) o;
        assertEquals("text", e.getName().getValue());
        assertEquals("id6", e.getArchetypeNodeId());
    }

    @Test
    public void createdArchetypedObject() {
        OperationalTemplate archetype = new OperationalTemplate();
        archetype.setTerminology(new ArchetypeTerminology());
        LinkedHashMap<String, ArchetypeTerm> termDefinitions = new LinkedHashMap<>();
        termDefinitions.put("id6", new ArchetypeTerm("id6", "text", "description"));
        archetype.getTerminology().getTermDefinitions().put("en", termDefinitions);

        CArchetypeRoot elementConstraint = new CArchetypeRoot();
        elementConstraint.setRmTypeName("OBSERVATION");
        elementConstraint.setNodeId("id6");
        elementConstraint.setArchetypeRef("openEHR-EHR-OBSERVATION.test.v1.0.0");

        archetype.setDefinition(elementConstraint);

        Object o = creator.create(elementConstraint);
        assertEquals(Observation.class, o.getClass());
        Observation e = (Observation) o;

        assertEquals("id6", e.getArchetypeNodeId());
        Archetyped archetypeDetails = e.getArchetypeDetails();
        assertNotNull(archetypeDetails);
        assertEquals("openEHR-EHR-OBSERVATION.test.v1.0.0", archetypeDetails.getArchetypeId().getValue());
        assertEquals("1.1.0", archetypeDetails.getRmVersion());
    }

    @Test(expected=IllegalArgumentException.class)
    public void createUnknownType() {
        Archetype archetype = new AuthoredArchetype();
        archetype.setTerminology(new ArchetypeTerminology());
        LinkedHashMap<String, ArchetypeTerm> termDefinitions = new LinkedHashMap<>();
        termDefinitions.put("id6", new ArchetypeTerm("id6", "text", "description"));
        archetype.getTerminology().getTermDefinitions().put("en", termDefinitions);

        CComplexObject elementConstraint = new CComplexObject();
        elementConstraint.setRmTypeName("DOUBLE");
        elementConstraint.setNodeId("id6");
        archetype.setDefinition(elementConstraint);

        Object o = creator.create(elementConstraint);
    }

    @Test
    public void setSingleValuedValue() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        creator.set(element, "value", Lists.newArrayList(booleanValue));
        assertEquals(booleanValue, element.getValue());
    }

    @Test
    public void setSingleValuedValuePrimitive() {
        DvBoolean booleanValue = new DvBoolean();
        creator.set(booleanValue, "value", Lists.newArrayList(true));
        assertEquals(true, booleanValue.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSingleValuedValueIncorrectly() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        DvBoolean booleanValue2 = new DvBoolean();
        creator.set(element, "value", Lists.newArrayList(booleanValue, booleanValue2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSingleValuedValueUnknownArgument() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        creator.set(element, "values", Lists.newArrayList(booleanValue));
    }

    @Test
    public void setMultiValuedValue() {
        Cluster cluster = new Cluster();
        Element element = new Element();
        creator.set(cluster, "items", Lists.newArrayList(element));
        assertEquals(Lists.newArrayList(element), cluster.getItems());
    }

    @Test
    public void setMultiValuedValue2() {
        Cluster cluster = new Cluster();
        Element element = new Element();
        Element element2 = new Element();
        creator.set(cluster, "items", Lists.newArrayList(element, element2));
        assertEquals(Lists.newArrayList(element, element2), cluster.getItems());
    }

    @Test
    public void addToListOrSetSingleValue() {
        Cluster cluster = new Cluster();
        Element element = new Element();
        Element element2 = new Element();
        creator.set(cluster, "items", Lists.newArrayList(element));
        creator.addElementToListOrSetSingleValues(cluster, "items", element2);
        assertEquals(Lists.newArrayList(element, element2), cluster.getItems());
    }

    @Test
    public void addToListOrSetSingleValue2() {
        Cluster cluster = new Cluster();
        Element element = new Element();
        Element element2 = new Element();
        Element element3 = new Element();
        creator.set(cluster, "items", Lists.newArrayList(element));
        creator.addElementToListOrSetSingleValues(cluster, "items", Lists.newArrayList(element2, element3));
        assertEquals(Lists.newArrayList(element, element2, element3), cluster.getItems());
    }

    @Test
    public void addToListOrSetSingleValueWithSingleValue() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        creator.addElementToListOrSetSingleValues(element, "value", booleanValue);
        assertEquals(booleanValue, element.getValue());
    }

    @Test
    public void addToListOrSetSingleValueWithSingleValue2() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        creator.addElementToListOrSetSingleValues(element, "value", Lists.newArrayList(booleanValue));
        assertEquals(booleanValue, element.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addToListOrSetSingleValueWithSingleValueIncorrect() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        DvBoolean booleanValue2 = new DvBoolean();
        creator.addElementToListOrSetSingleValues(element, "value", Lists.newArrayList(booleanValue, booleanValue2));
        assertEquals(booleanValue, element.getValue());
    }


}
