
package org.openehr.bmm.v2.persistence.converters;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openehr.bmm.core.*;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.openehr.bmm.v2.persistence.converters.BmmTestUtil.parse;

public class S2Care065ConversionTest {

    private static BmmModel s2care065model;
    private static BmmRepository repo;

    @BeforeClass
    public static void setup() throws Exception {
        repo = new BmmRepository();
        repo.addPersistentSchema(parse("/s2/s2_base_foundation_types_070.bmm"));
        repo.addPersistentSchema(parse("/s2/s2_base_data_types_070.bmm"));
        repo.addPersistentSchema(parse("/s2/s2_base_model_support_070.bmm"));
        repo.addPersistentSchema(parse("/s2/s2_base_patterns_070.bmm"));
        repo.addPersistentSchema(parse("/s2/s2_base_resource_070.bmm"));
        repo.addPersistentSchema(parse("/s2/s2_base_change_control_070.bmm"));
        repo.addPersistentSchema(parse("/s2/s2_base_070.bmm"));
        repo.addPersistentSchema(parse("/s2/s2_care_entry_065.bmm"));
        repo.addPersistentSchema(parse("/s2/s2_care_ehr_065.bmm"));
        repo.addPersistentSchema(parse("/s2/s2_care_065.bmm"));

        BmmSchemaConverter converter = new BmmSchemaConverter(repo);
        converter.validateAndConvertRepository();

        s2care065model = repo.getModel("s2_care_0.6.5").getModel();
    }

    @AfterClass
    public static void tearDown() {
        //not very important, but frees up some memory
        s2care065model = null;
        repo = null;
    }

    @Test
    public void testValid() throws Exception {

        for (BmmValidationResult validationResult:repo.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the S2 RM 0.6.5 BMM files should pass validation", validationResult.passes());
        }
    }

    @Test
    public void testModelPaths() throws Exception {
        // check some paths through the model
        assertTrue ("property defined in class: (\"Care_entry\", \"/qualifiers\")", s2care065model.hasPropertyAtPath("Care_entry", "/qualifiers"));
        assertTrue ("property defined in descendant: (\"Care_entry\", \"/reporter/external_ref\")", s2care065model.hasPropertyAtPath("Care_entry", "/reporter/external_ref"));
        assertTrue ("property defined in ancestor: (\"Observation\", \"/data/items\")", s2care065model.hasPropertyAtPath("Observation", "/data/items"));
        assertTrue ("property defined in class: (\"Observation\", \"/state/value\")", s2care065model.hasPropertyAtPath("Observation", "/state/value"));
        assertTrue ("property defined in descendant: (\"Direct_observation\", \"/total_duration\")", s2care065model.hasPropertyAtPath("Direct_observation", "/total_duration"));
        assertTrue ("property defined in descendant: (\"Direct_observation\", \"/data_series/items\")", s2care065model.hasPropertyAtPath("Direct_observation", "/data_series/items"));
        assertTrue ("property defined in descendant: (\"Questionnaire_response\", \"/questionnaire_title/concept/uri\")", s2care065model.hasPropertyAtPath("Questionnaire_response", "/questionnaire_title/concept/uri"));
        assertTrue ("property defined in class: (\"Composition\", \"/context\")", s2care065model.hasPropertyAtPath("Composition", "/context"));
        assertTrue ("property defined in descendant: (\"Node\", \"/items/items/items\")", s2care065model.hasPropertyAtPath("Node", "/items/items/items"));
        assertTrue ("property defined in class: (\"Quantity\", \"/magnitude\")", s2care065model.hasPropertyAtPath("Quantity", "/magnitude"));
    }

    @Test
    public void testPropertyMetatypes() throws Exception {
        // check meta-types of some properties at paths
        assertTrue ("BmmSimpleType at path: (\"Observation\", \"/uid\")", s2care065model.propertyAtPath("Observation", "/uid").getType() instanceof BmmSimpleType);
        assertTrue ("BmmContainerType at path: (\"Observation\", \"/data\")", s2care065model.propertyAtPath("Observation", "/data").getType() instanceof BmmContainerType);
        assertTrue ("BmmIndexedContainerType at path: (\"Locatable\", \"/feeder_audit/feeder_system_audit/other_details\")", s2care065model.propertyAtPath("Locatable", "/feeder_audit/feeder_system_audit/other_details").getType() instanceof BmmIndexedContainerType);
        assertTrue ("BmmGenericType at path: (\"Reference_range\", \"/range\")", s2care065model.propertyAtPath("Reference_range", "/range").getType() instanceof BmmGenericType);
        assertTrue ("BmmGSimpleType at path: (\"Care_entry\", \"/subject\")", s2care065model.propertyAtPath("Care_entry", "/subject").getType() instanceof BmmSimpleType);
    }

    @Test
    public void testModelDescendants() throws Exception {
        // test descendant relations
        assertTrue ("\"Observation\" descendant of \"Locatable\")", s2care065model.descendantOf("Observation", "Locatable"));
        assertFalse ("\"Locatable\" not descendant of \"Composition\")", s2care065model.descendantOf("Locatable", "Composition"));
        assertTrue ("\"Quantity\" descendant of \"Measurable\")", s2care065model.descendantOf("Quantity", "Measurable"));
        assertTrue ("\"Duration_value\" descendant of \"Ordered_datum\")", s2care065model.descendantOf("Duration_value", "Ordered_datum"));
    }

    @Test
    public void testCompositionImmediateSuppliers() throws Exception {
        // test supplier relations
        Set<String> testResult = s2care065model.suppliers("Composition");

        // conformance result from ADL Workbench
        Set<String> conformanceResult = new LinkedHashSet<>(
                Arrays.asList(
                        "Party_self", "Terminology_code", "Content_item", "Sample_function_kind", "String", "Archetyped", "Version_status", "Comparison_operator", "Uri",
                        "Party_identified", "Section", "Feeder_audit", "Party_proxy", "Event_context", "Order_execution_state", "Entry", "Uuid", "Terminology_term",
                        "Order_execution_transition", "Trend_kind", "Validity_kind", "Link"
                )
        );
        assertEquals ("\"Composition\" has suppliers {\"Terminology_code\", \"Party_proxy\", ...})", testResult, conformanceResult);
    }

    @Test
    public void testPartyIdentifiedSuppliers() throws Exception {
        // test supplier relations
        Set<String> testResult = s2care065model.supplierClosure("Party_identified");

        // conformance result from ADL Workbench
        Set<String> conformanceResult = new LinkedHashSet<>(
            Arrays.asList(
                "Sample_function_kind", "String", "Version_status", "Comparison_operator", "Object_version_id", "Uri", "Order_execution_state",
                        "Locatable_ref", "Object_id", "Rwe_id_ref", "Object_ref", "Order_execution_transition", "Trend_kind", "Validity_kind"
            )
        );

        assertEquals ("\"Party_identified\" has suppliers {\"String\", \"Rwe_id_ref\", ...})", testResult, conformanceResult);
    }

}
