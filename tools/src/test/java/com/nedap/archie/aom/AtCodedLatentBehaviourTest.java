package com.nedap.archie.aom;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Characterization probes for the LATENT at-coded gaps #5 and #7.
 *
 * <p>These are deliberately not failing behavioural tests. Both gaps are latent: no caller exercises the broken path
 * with at-coded input today (#5: "no core callers"; #7: verified the id9999 stamp does not leak to serialized ADL).
 * Because there is no observable wrong behaviour, a test cannot prove the fixes are necessary - it can only pin the
 * current behaviour as a regression guard and document the conceptual landmine. If a future change makes one of these
 * paths reachable, these tests turn the silent assumption into an explicit, reviewable fact.
 */
public class AtCodedLatentBehaviourTest {

    private static final String AT_CODED = "/com/nedap/archie/adl14/openEHR-EHR-OBSERVATION.demo_adl2_at.v1.0.0.adls";

    /**
     * Gap #5: node ids and value codes both use the "at" prefix in at-coded archetypes, so prefix-based classification
     * cannot tell them apart. getUsedIdCodes() (filters "id") finds none of the at-coded node ids, and
     * getUsedValueCodes() (filters "at") sweeps up node ids alongside genuine value codes - here the at0000 root.
     */
    @Test
    public void usedCodeClassificationConflatesAtCodedNodeIdsAndValues() throws Exception {
        Archetype archetype = parseAtCoded(AT_CODED);

        Set<String> usedIdCodes = archetype.getUsedIdCodes();
        Set<String> usedValueCodes = archetype.getUsedValueCodes();

        // no at-coded node id is recognised as an id code (the root at0000 is a node id, yet absent here)
        assertFalse(usedIdCodes.contains("at0000"));
        assertTrue(usedIdCodes.isEmpty(), "expected no id-prefixed codes in an at-coded archetype, but got: " + usedIdCodes);

        // instead the at-coded root node id is misclassified as a value code - the latent conflation
        assertTrue(usedValueCodes.contains("at0000"),
                "expected the at0000 node id to be (mis)classified as a value code; got: " + usedValueCodes);
    }

    /**
     * Gap #7: ArchetypeParsePostProcessor stamps every primitive object with the id-coded sentinel id9999, regardless
     * of the archetype's code system. So an at-coded archetype contains an id-code in memory. This is verified to be
     * internal only: it does not appear in serialized ADL.
     */
    @Test
    public void primitivesCarryIdCodedSentinelButItDoesNotLeak() throws Exception {
        Archetype archetype = parseAtCoded(AT_CODED);

        CPrimitiveObject primitive = findFirstPrimitive(archetype.getDefinition());
        assertNotNull(primitive, "expected at least one primitive object in the fixture");
        assertEquals("id9999", primitive.getNodeId(),
                "primitive objects are stamped with the id-coded sentinel even in an at-coded archetype");

        // ...but the sentinel is internal only and must not leak into serialized ADL
        String serialized = ADLArchetypeSerializer.serialize(archetype);
        assertFalse(serialized.contains("id9999"), "the id9999 sentinel must not leak into serialized ADL");
    }

    private CPrimitiveObject findFirstPrimitive(CObject root) {
        Deque<CObject> workList = new ArrayDeque<>();
        workList.add(root);
        while (!workList.isEmpty()) {
            CObject next = workList.pop();
            if (next instanceof CPrimitiveObject) {
                return (CPrimitiveObject) next;
            }
            for (CAttribute attribute : next.getAttributes()) {
                workList.addAll(attribute.getChildren());
            }
        }
        return null;
    }

    private Archetype parseAtCoded(String resourcePath) throws Exception {
        ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModelProvider());
        try (InputStream stream = AtCodedLatentBehaviourTest.class.getResourceAsStream(resourcePath)) {
            assertNotNull(stream, "cannot find fixture: " + resourcePath);
            Archetype archetype = parser.parse(stream);
            assertTrue(parser.getErrors().hasNoErrors(), parser.getErrors().toString());
            return archetype;
        }
    }
}
