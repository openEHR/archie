package com.nedap.archie.aom.primitives;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.ConformanceCheckResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies specialisation conformance of C_TERMINOLOGY_CODE value set constraints for at-coded ADL 2.4 archetypes,
 * whose value set codes are zero-padded (e.g. ac0001, as produced by the ADL 1.4 to ADL 2.4 at-coded converter).
 *
 * This exercises the value-set branch of {@link CTerminologyCode#cConformsTo}, which depends on
 * {@link com.nedap.archie.aom.utils.AOMUtils#isValidValueSetCode}. Before that method tolerated zero-padded codes,
 * ac0001 was not recognised as a value set code, so conformance fell back to comparing the codes as plain value
 * codes and an illegal value set widening would incorrectly pass.
 */
public class CTerminologyCodeConformanceTest {

    @Test
    public void atCodedValueSetSubsetConforms() {
        CTerminologyCode child = valueSetConstraint("ac0001", "at0002", "at0003");
        CTerminologyCode parent = valueSetConstraint("ac0001", "at0002", "at0003", "at0004");
        ConformanceCheckResult result = child.cConformsTo(parent, (a, b) -> true);
        assertTrue(result.doesConform(), result.getError());
    }

    @Test
    public void atCodedValueSetSupersetDoesNotConform() {
        // The child adds member at0099, which is not in the parent value set. This illegal widening is only detected
        // when the zero-padded ac0001 codes are recognised as value set codes.
        CTerminologyCode child = valueSetConstraint("ac0001", "at0002", "at0099");
        CTerminologyCode parent = valueSetConstraint("ac0001", "at0002", "at0003");
        ConformanceCheckResult result = child.cConformsTo(parent, (a, b) -> true);
        assertFalse(result.doesConform(), "child value set adds a member not present in the parent and must not conform");
    }

    /**
     * Builds a C_TERMINOLOGY_CODE constrained to the given value set code, wired into a minimal archetype whose
     * terminology defines that value set with the given members, so that value set expansion resolves.
     */
    private CTerminologyCode valueSetConstraint(String valueSetCode, String... members) {
        Archetype archetype = new Archetype();
        CComplexObject root = new CComplexObject();
        root.setRmTypeName("ELEMENT");
        root.setNodeId("at0000");
        archetype.setDefinition(root);

        CAttribute value = new CAttribute("value");
        root.addAttribute(value);
        CComplexObject dvCodedText = new CComplexObject();
        dvCodedText.setRmTypeName("DV_CODED_TEXT");
        dvCodedText.setNodeId("at0001");
        value.addChild(dvCodedText);
        CAttribute definingCode = new CAttribute("defining_code");
        dvCodedText.addAttribute(definingCode);

        CTerminologyCode code = new CTerminologyCode();
        code.setRmTypeName("CODE_PHRASE");
        code.addConstraint(valueSetCode);
        definingCode.addChild(code);

        ArchetypeTerminology terminology = new ArchetypeTerminology();
        ValueSet valueSet = new ValueSet();
        valueSet.setId(valueSetCode);
        valueSet.setMembers(Arrays.asList(members));
        Map<String, ValueSet> valueSets = new HashMap<>();
        valueSets.put(valueSetCode, valueSet);
        terminology.setValueSets(valueSets);
        archetype.setTerminology(terminology);

        return code;
    }
}
