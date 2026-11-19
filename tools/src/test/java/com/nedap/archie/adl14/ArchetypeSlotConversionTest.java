package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.adl14.log.ADL2ConversionRunLog;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constraint;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArchetypeSlotConversionTest {

    @Test
    public void testSlotConversion() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);
        ADL2ConversionRunLog log = null;

        try(InputStream stream = getClass().getResourceAsStream("/com/nedap/archie/adl14/openEHR-EHR-OBSERVATION.respiration.v1.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream, conversionConfiguration)));
            Archetype archetype = result.getConversionResults().get(0).getArchetype();
            ArchetypeSlot slot = archetype.itemAtPath("/data/events[1]/state[id23]/items[id56]");
            String includesPattern = getPattern(slot.getIncludes().get(0));
            assertEquals("/openEHR-EHR-CLUSTER\\.inspired_oxygen(-[a-zA-Z0-9_]+)*\\.v1\\..*/", includesPattern);

            ArchetypeSlot slot2 = archetype.itemAtPath("/protocol/items[1]");
            String includesPattern2 = getPattern(slot2.getIncludes().get(0));
            String excludesPattern2 = getPattern(slot2.getExcludes().get(0));
            assertEquals("/.*/", includesPattern2);
            assertEquals("/openEHR-EHR-CLUSTER\\.level_of_exertion-excluded.v1\\..*/", excludesPattern2);
        }
    }

    @Test
    public void slotAssertionPrimitiveNodeIdFollowsConvertedCodeSystem() throws Exception {
        // an ADL 1.4 archetype is at-coded when parsed. After conversion to id-coded, primitives in slot assertions must be id9999
        assertEquals("id9999", convertedSlotAssertionNodeId(ADL14ConversionConfiguration.NodeIdCodeSystem.ID_CODED, "/protocol/items[1]"));
        assertEquals("at9999", convertedSlotAssertionNodeId(ADL14ConversionConfiguration.NodeIdCodeSystem.AT_CODED, "/protocol/items[1]"));
    }

    private String convertedSlotAssertionNodeId(ADL14ConversionConfiguration.NodeIdCodeSystem codeSystem, String slotPath) throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        conversionConfiguration.setNodeIdCodeSystem(codeSystem);
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);
        try(InputStream stream = getClass().getResourceAsStream("/com/nedap/archie/adl14/openEHR-EHR-OBSERVATION.respiration.v1.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream, conversionConfiguration)));
            Archetype archetype = result.getConversionResults().get(0).getArchetype();
            ArchetypeSlot slot = archetype.itemAtPath(slotPath);
            BinaryOperator matches = (BinaryOperator) slot.getIncludes().get(0).getExpression();
            return ((Constraint<?>) matches.getRightOperand()).getItem().getNodeId();
        }
    }

    private String getPattern(Assertion assertion) {
        BinaryOperator matches = (BinaryOperator) assertion.getExpression();
        Constraint<?> constraint = (Constraint<?>) matches.getRightOperand();
        CString stringConstraint = (CString) constraint.getItem();
        return stringConstraint.getConstraint().get(0);
    }

}
