package com.nedap.archie.diff;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rules.RuleStatement;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;
import com.nedap.archie.serializer.adl.ADLRulesSerializer;
import com.nedap.archie.serializer.adl.ADLStringBuilder;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import static org.junit.Assert.*;

public class RulesDifferentiatorTest {

    private String archetypesResourceLocation = "/com/nedap/archie/diff/rulesdifferentiator/";
    private final String PARENT_ARCHETYPE_ID = "openEHR-EHR-OBSERVATION.rules_parent.v0.0.1";
    private final String CHILD_ARCHETYPE_ID = "openEHR-EHR-OBSERVATION.rules_child.v0.0.1";
    private final String GRANDCHILD_ARCHETYPE_ID = "openEHR-EHR-OBSERVATION.rules_grandchild.v0.0.1";

    @Test
    public void differentiateRulesInChildArchetype() throws Exception {
        InMemoryFullArchetypeRepository inMemoryFullArchetypeRepository = new InMemoryFullArchetypeRepository();
        Archetype parent = TestUtil.parseFailOnErrors(archetypesResourceLocation + PARENT_ARCHETYPE_ID + ".adls");
        Archetype child = TestUtil.parseFailOnErrors(archetypesResourceLocation + CHILD_ARCHETYPE_ID + ".adls");
        inMemoryFullArchetypeRepository.addArchetype(parent);
        inMemoryFullArchetypeRepository.addArchetype(child);

        inMemoryFullArchetypeRepository.compile(BuiltinReferenceModels.getMetaModels());
        Archetype result = inMemoryFullArchetypeRepository.getFlattenedArchetype(CHILD_ARCHETYPE_ID);
        Archetype flatParent = inMemoryFullArchetypeRepository.getFlattenedArchetype(PARENT_ARCHETYPE_ID);
        new RulesDifferentiator().differentiate(result, flatParent);

        // Tests
        assertEquals(1, result.getRules().getRules().size());
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id0.4]/value/magnitude = round(\n" +
                "    /data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude + \n" +
                "    /data[id2]/events[id3]/data[id4]/items[id0.2]/value/magnitude)\n", serializeRuleStatement(result.getRules().getRules().get(0)));
    }

    @Test
    public void differentiateRulesInGrandchildArchetype() throws Exception {
        InMemoryFullArchetypeRepository inMemoryFullArchetypeRepository = new InMemoryFullArchetypeRepository();
        Archetype parent = TestUtil.parseFailOnErrors(archetypesResourceLocation + PARENT_ARCHETYPE_ID + ".adls");
        Archetype child = TestUtil.parseFailOnErrors(archetypesResourceLocation + CHILD_ARCHETYPE_ID + ".adls");
        Archetype grandchild = TestUtil.parseFailOnErrors(archetypesResourceLocation + GRANDCHILD_ARCHETYPE_ID + ".adls");
        inMemoryFullArchetypeRepository.addArchetype(parent);
        inMemoryFullArchetypeRepository.addArchetype(child);
        inMemoryFullArchetypeRepository.addArchetype(grandchild);

        inMemoryFullArchetypeRepository.compile(BuiltinReferenceModels.getMetaModels());
        Archetype result = inMemoryFullArchetypeRepository.getFlattenedArchetype(GRANDCHILD_ARCHETYPE_ID);
        Archetype flatParent = inMemoryFullArchetypeRepository.getFlattenedArchetype(CHILD_ARCHETYPE_ID);
        new RulesDifferentiator().differentiate(result, flatParent);

        // Tests
        assertEquals(1, result.getRules().getRules().size());
        assertEquals("/data[id2]/events[id3]/data[id4]/items[id0.0.4]/value/magnitude = round(\n" +
                "    /data[id2]/events[id3]/data[id4]/items[id5]/value/magnitude + \n" +
                "    /data[id2]/events[id3]/data[id4]/items[id0.0.2]/value/magnitude)\n", serializeRuleStatement(result.getRules().getRules().get(0)));
    }

    /**
     * Convert RuleStatement into a String
     */
    public static String serializeRuleStatement(RuleStatement ruleStatement) {
        ADLStringBuilder builder = new ADLStringBuilder();
        new ADLRulesSerializer(builder, new ADLDefinitionSerializer(builder, null, null)).serializeRuleElement(ruleStatement);
        return builder.toString();
    }
}
