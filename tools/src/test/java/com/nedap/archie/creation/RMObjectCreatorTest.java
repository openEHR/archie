package com.nedap.archie.creation;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

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
}
