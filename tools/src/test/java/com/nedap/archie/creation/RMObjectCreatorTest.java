package com.nedap.archie.creation;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datavalues.DvBoolean;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.*;

/**
 * Created by pieter.bos on 10/05/2017.
 */
public class RMObjectCreatorTest {

    RMObjectCreator creator = new RMObjectCreator(ArchieRMInfoLookup.getInstance());

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

    @Test
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

        assertThrows(IllegalArgumentException.class, ()-> creator.create(elementConstraint));
    }

    @Test
    @Deprecated
    public void setSingleValuedValue() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        creator.set(element, "value", Lists.newArrayList(booleanValue));
        assertEquals(booleanValue, element.getValue());
    }

    @Test
    @Deprecated
    public void setSingleValuedValuePrimitive() {
        DvBoolean booleanValue = new DvBoolean();
        creator.set(booleanValue, "value", Lists.newArrayList(true));
        assertEquals(true, booleanValue.getValue());
    }

    @Test
    @Deprecated
    public void setSingleValuedValueIncorrectly() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        DvBoolean booleanValue2 = new DvBoolean();
        assertThrows(IllegalArgumentException.class, () -> creator.set(element, "value", Lists.newArrayList(booleanValue, booleanValue2)));
    }

    @Test
    @Deprecated
    public void setSingleValuedValueUnknownArgument() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        assertThrows(IllegalArgumentException.class, () -> creator.set(element, "values", Lists.newArrayList(booleanValue)));
    }

    @Test
    @Deprecated
    public void setMultiValuedValue() {
        Cluster cluster = new Cluster();
        Element element = new Element();
        creator.set(cluster, "items", Lists.newArrayList(element));
        assertEquals(Lists.newArrayList(element), cluster.getItems());
    }

    @Test
    @Deprecated
    public void setMultiValuedValue2() {
        Cluster cluster = new Cluster();
        Element element = new Element();
        Element element2 = new Element();
        creator.set(cluster, "items", Lists.newArrayList(element, element2));
        assertEquals(Lists.newArrayList(element, element2), cluster.getItems());
    }

    @Test
    @Deprecated
    public void addToListOrSetSingleValue() {
        Cluster cluster = new Cluster();
        Element element = new Element();
        Element element2 = new Element();
        creator.set(cluster, "items", Lists.newArrayList(element));
        creator.addElementToListOrSetSingleValues(cluster, "items", element2);
        assertEquals(Lists.newArrayList(element, element2), cluster.getItems());
    }

    @Test
    @Deprecated
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
    @Deprecated
    public void addToListOrSetSingleValueWithSingleValue() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        creator.addElementToListOrSetSingleValues(element, "value", booleanValue);
        assertEquals(booleanValue, element.getValue());
    }

    @Test
    @Deprecated
    public void addToListOrSetSingleValueWithSingleValue2() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        creator.addElementToListOrSetSingleValues(element, "value", Lists.newArrayList(booleanValue));
        assertEquals(booleanValue, element.getValue());
    }

    @Test
    @Deprecated
    public void addToListOrSetSingleValueWithSingleValueIncorrect() {
        Element element = new Element();
        DvBoolean booleanValue = new DvBoolean();
        DvBoolean booleanValue2 = new DvBoolean();

        assertThrows(IllegalArgumentException.class, () ->
                creator.addElementToListOrSetSingleValues(
                        element,
                        "value",
                        Lists.newArrayList(booleanValue, booleanValue2)
                )
        );
    }


}
