package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.adl14.log.ADL2ConversionRunLog;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constraint;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.InputStream;

import static junit.framework.TestCase.assertEquals;

public class ArchetypeSlotConversionTest {

    @Test
    public void testSlotConversion() throws Exception {
        ADL14ConversionConfiguration conversionConfiguration = ConversionConfigForTest.getConfig();
        ADL14Converter converter = new ADL14Converter(AllMetaModelsInitialiser.getMetaModels(), conversionConfiguration);
        ADL2ConversionRunLog log = null;

        try(InputStream stream = getClass().getResourceAsStream("/com/nedap/archie/adl14/openEHR-EHR-OBSERVATION.respiration.v1.adl")) {
            ADL2ConversionResultList result = converter.convert(
                    Lists.newArrayList(new ADL14Parser(AllMetaModelsInitialiser.getMetaModels()).parse(stream, conversionConfiguration)));
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

    private String getPattern(Assertion assertion) {
        BinaryOperator matches = (BinaryOperator) assertion.getExpression();
        Constraint<?> constraint = (Constraint<?>) matches.getRightOperand();
        CString stringConstraint = (CString) constraint.getItem();
        String pattern = stringConstraint.getConstraint().get(0);
        return pattern;
    }

}
