package com.nedap.archie.adlparser;

import com.nedap.archie.antlr.errors.ANTLRParserMessage;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.primitives.ConstraintStatus;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AtCodedGrammarTest {

    /**
     * A leaf node such as DV_TEXT[at9056] could be parsed both as a complex object and as a terminology code constraint
     * with a constraint status, which made the parser report an ambiguity for every such node in at-coded archetypes.
     */
    @Test
    public void atCodedLeafNodesAreNotAmbiguous() throws Exception {
        ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModelProvider());
        Archetype archetype;
        try (InputStream stream = getClass().getResourceAsStream("/com/nedap/archie/adl14/openEHR-EHR-OBSERVATION.demo_adl2_at.v1.0.0.adls")) {
            archetype = parser.parse(stream);
        }
        List<ANTLRParserMessage> warnings = parser.getErrors().getWarnings();
        assertTrue(parser.getErrors().hasNoErrors(), parser.getErrors().toString());
        assertTrue(warnings.isEmpty(), warnings.stream().map(ANTLRParserMessage::getMessage).collect(Collectors.joining("\n")));

        CObject dvText = archetype.itemAtPath("/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0005]/value[at9056]");
        assertTrue(dvText instanceof CComplexObject);
        assertEquals("DV_TEXT", dvText.getRmTypeName());
    }

    @Test
    public void constraintStatusIsParsed() throws Exception {
        ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModelProvider());
        Archetype archetype;
        try (InputStream stream = getClass().getResourceAsStream("/com/nedap/archie/archetypevalidator/primitives/openEHR-EHR-CLUSTER.constraint_strength_parent.v1.0.0.adls")) {
            archetype = parser.parse(stream);
        }
        assertTrue(parser.getErrors().hasNoErrors(), parser.getErrors().toString());
        assertEquals(ConstraintStatus.REQUIRED, getTerminologyCode(archetype, "/items[id2]/value[id3]/defining_code").getConstraintStatus());
        assertEquals(ConstraintStatus.EXTENSIBLE, getTerminologyCode(archetype, "/items[id4]/value[id5]/defining_code").getConstraintStatus());
        assertEquals(ConstraintStatus.PREFERRED, getTerminologyCode(archetype, "/items[id6]/value[id7]/defining_code").getConstraintStatus());
        assertEquals(ConstraintStatus.EXAMPLE, getTerminologyCode(archetype, "/items[id8]/value[id9]/defining_code").getConstraintStatus());
    }

    private CTerminologyCode getTerminologyCode(Archetype archetype, String attributePath) {
        CAttribute attribute = archetype.itemAtPath(attributePath);
        return (CTerminologyCode) attribute.getChildren().get(0);
    }
}
