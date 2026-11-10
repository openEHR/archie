package com.nedap.archie.aom;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CString;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Constraint;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrimitiveNodeIdTest {

    private static final String AT_CODED_ADL =
            "archetype (adl_version=2.4.0; rm_release=1.1.0)\n" +
            "    openEHR-EHR-CLUSTER.primitive_node_ids.v1.0.0\n" +
            "\n" +
            "language\n" +
            "    original_language = <[ISO_639-1::en]>\n" +
            "\n" +
            "description\n" +
            "    original_author = <[\"name\"] = <\"Test\">>\n" +
            "    details = <\n" +
            "        [\"en\"] = <\n" +
            "            language = <[ISO_639-1::en]>\n" +
            "            purpose = <\"Test\">\n" +
            "        >\n" +
            "    >\n" +
            "\n" +
            "definition\n" +
            "    CLUSTER[at0000] matches {\n" +
            "        items matches {\n" +
            "            ELEMENT[at0001] occurrences matches {0..1} matches {\n" +
            "                value matches {\n" +
            "                    DV_COUNT[at0002] matches {\n" +
            "                        magnitude matches {|0..10|}\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "            allow_archetype CLUSTER[at0003] matches {\n" +
            "                include\n" +
            "                    archetype_id/value matches {/.*/}\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "rules\n" +
            "    /items[at0001]/value/magnitude matches {|0..5|}\n" +
            "\n" +
            "terminology\n" +
            "    term_definitions = <\n" +
            "        [\"en\"] = <\n" +
            "            [\"at0000\"] = <text = <\"root\"> description = <\"root\">>\n" +
            "            [\"at0001\"] = <text = <\"element\"> description = <\"element\">>\n" +
            "            [\"at0002\"] = <text = <\"count\"> description = <\"count\">>\n" +
            "            [\"at0003\"] = <text = <\"slot\"> description = <\"slot\">>\n" +
            "        >\n" +
            "    >\n";

    private static final String AT_CODED_TEMPLATE =
            "template (adl_version=2.4.0; rm_release=1.1.0)\n" +
            "    openEHR-EHR-CLUSTER.primitive_node_ids_template.v1.0.0\n" +
            "\n" +
            "specialize\n" +
            "    openEHR-EHR-CLUSTER.primitive_node_ids.v1.0.0\n" +
            "\n" +
            "language\n" +
            "    original_language = <[ISO_639-1::en]>\n" +
            "\n" +
            "description\n" +
            "    original_author = <[\"name\"] = <\"Test\">>\n" +
            "    details = <\n" +
            "        [\"en\"] = <\n" +
            "            language = <[ISO_639-1::en]>\n" +
            "            purpose = <\"Test\">\n" +
            "        >\n" +
            "    >\n" +
            "\n" +
            "definition\n" +
            "    CLUSTER[at0000.1] matches {\n" +
            "        items matches {\n" +
            "            use_archetype CLUSTER[at0003.1, openEHR-EHR-CLUSTER.ovl-primitive_node_ids-001.v1.0.0]\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "terminology\n" +
            "    term_definitions = <\n" +
            "        [\"en\"] = <\n" +
            "            [\"at0000.1\"] = <text = <\"template\"> description = <\"template\">>\n" +
            "            [\"at0003.1\"] = <text = <\"overlay\"> description = <\"overlay\">>\n" +
            "        >\n" +
            "    >\n" +
            "\n" +
            "------------------------------------------------------------------------\n" +
            "template_overlay\n" +
            "    openEHR-EHR-CLUSTER.ovl-primitive_node_ids-001.v1.0.0\n" +
            "\n" +
            "specialize\n" +
            "    openEHR-EHR-CLUSTER.primitive_node_ids.v1.0.0\n" +
            "\n" +
            "definition\n" +
            "    CLUSTER[at0000.1] matches {\n" +
            "        /items[at0001]/value[at0002]/magnitude matches {|0..8|}\n" +
            "        items matches {\n" +
            "            allow_archetype CLUSTER[at0003.1] matches {\n" +
            "                include\n" +
            "                    archetype_id/value matches {/.*\\.v1/}\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "terminology\n" +
            "    term_definitions = <\n" +
            "        [\"en\"] = <\n" +
            "            [\"at0000.1\"] = <text = <\"overlay\"> description = <\"overlay\">>\n" +
            "            [\"at0003.1\"] = <text = <\"slot\"> description = <\"slot\">>\n" +
            "        >\n" +
            "    >\n";

    @Test
    public void atCodedPrimitiveNodeIdsInTemplateOverlay() throws Exception {
        Template template = (Template) new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(AT_CODED_TEMPLATE);
        TemplateOverlay overlay = template.getTemplateOverlays().get(0);

        CInteger magnitude = (CInteger) overlay.itemAtPath("/items[at0001]/value[at0002]/magnitude[1]");
        assertEquals("at9999", magnitude.getNodeId());

        ArchetypeSlot slot = overlay.itemAtPath("/items[at0003.1]");
        CString slotConstraint = (CString) ((Constraint<?>) ((BinaryOperator) slot.getIncludes().get(0).getExpression()).getRightOperand()).getItem();
        assertEquals("at9999", slotConstraint.getNodeId());
    }

    @Test
    public void atCodedPrimitiveNodeIds() throws Exception {
        Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(AT_CODED_ADL);

        CInteger magnitude = (CInteger) archetype.itemAtPath("/items[at0001]/value[at0002]/magnitude[1]");
        assertEquals("at9999", magnitude.getNodeId());

        ArchetypeSlot slot = archetype.itemAtPath("/items[at0003]");
        CString slotConstraint = (CString) ((Constraint<?>) ((BinaryOperator) slot.getIncludes().get(0).getExpression()).getRightOperand()).getItem();
        assertEquals("at9999", slotConstraint.getNodeId());

        Assertion rule = (Assertion) archetype.getRules().getRules().get(0);
        CInteger ruleConstraint = (CInteger) ((Constraint<?>) ((BinaryOperator) rule.getExpression()).getRightOperand()).getItem();
        assertEquals("at9999", ruleConstraint.getNodeId());

        // a clone keeps them
        Archetype clone = archetype.clone();
        ArchetypeSlot clonedSlot = clone.itemAtPath("/items[at0003]");
        assertTrue(clonedSlot.getIncludes().get(0).getStringExpression() != null);
        assertEquals("at9999", ((Constraint<?>) ((BinaryOperator) clonedSlot.getIncludes().get(0).getExpression()).getRightOperand()).getItem().getNodeId());
    }
}
