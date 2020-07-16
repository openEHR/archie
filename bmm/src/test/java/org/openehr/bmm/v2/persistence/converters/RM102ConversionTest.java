package org.openehr.bmm.v2.persistence.converters;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openehr.bmm.core.BmmContainerType;
import org.openehr.bmm.core.BmmGenericType;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmSimpleType;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openehr.bmm.v2.persistence.converters.BmmTestUtil.parse;

public class RM102ConversionTest {

    private static BmmModel rm102Model;
    private static BmmRepository repo;

    @BeforeClass
    public static void setup() throws Exception {
        repo = new BmmRepository();
        repo.addPersistentSchema(parse("/openehr/openehr_basic_types_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_demographic_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_ehr_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_primitive_types_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_rm_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_structures_102.bmm"));

        BmmSchemaConverter converter = new BmmSchemaConverter(repo);
        converter.validateAndConvertRepository();

        rm102Model = repo.getModel("openehr_ehr_1.0.2").getModel();
    }

    @AfterClass
    public static void tearDown() {
        //not very important, but frees up some memory
        rm102Model = null;
        repo = null;
    }

    @Test
    public void testRm102Valid() throws Exception {

        for (BmmValidationResult validationResult:repo.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the OpenEHR RM 1.0.2 BMM files should pass validation", validationResult.passes());
        }
    }

    @Test
    public void testRm102ModelPaths() throws Exception {
        // check some paths through the model
        assertTrue ("property defined in class: (\"CARE_ENTRY\", \"/protocol\")", rm102Model.hasPropertyAtPath("CARE_ENTRY", "/protocol"));
        assertTrue ("property defined in descendant: (\"CARE_ENTRY\", \"/data/events/data\")", rm102Model.hasPropertyAtPath("CARE_ENTRY", "/data/events/data"));
        assertTrue ("property defined in ancestor: (\"OBSERVATION\", \"/protocol\")", rm102Model.hasPropertyAtPath("OBSERVATION", "/protocol"));
        assertTrue ("property defined in class: (\"OBSERVATION\", \"/data/events/data\")", rm102Model.hasPropertyAtPath("OBSERVATION", "/data/events/data"));
        assertTrue ("property defined in descendant: (\"OBSERVATION\", \"/data/events/data/items\")", rm102Model.hasPropertyAtPath("OBSERVATION", "/data/events/data/items"));
        assertFalse ("non-existent property: (\"OBSERVATION\", \"/data/events/data/xxx\")", rm102Model.hasPropertyAtPath("OBSERVATION", "/data/events/data/xxx"));
        assertTrue ("property defined in class: (\"COMPOSITION\", \"/context\")", rm102Model.hasPropertyAtPath("COMPOSITION", "/context"));
        assertTrue ("property defined in descendant: (\"CLUSTER\", \"/items/items/items\")", rm102Model.hasPropertyAtPath("CLUSTER", "/items/items/items"));
        assertTrue ("property defined in class: (\"DV_QUANTITY\", \"/normal_range\")", rm102Model.hasPropertyAtPath("DV_QUANTITY", "/normal_range"));
    }

    @Test
    public void testRm102PropertyMetatypes() throws Exception {
        // check meta-types of some properties at paths
        assertTrue ("BmmSimpleType at path: (\"OBSERVATION\", \"/protocol\")", rm102Model.propertyAtPath("OBSERVATION", "/protocol").getType() instanceof BmmSimpleType);
        assertTrue ("BmmContainerType at path: (\"OBSERVATION\", \"/data/events/data/items\")", rm102Model.propertyAtPath("OBSERVATION", "/data/events/data/items").getType() instanceof BmmContainerType);
        assertTrue ("BmmGenericType at path: (\"DV_QUANTITY\", \"/normal_range\")", rm102Model.propertyAtPath("DV_QUANTITY", "/normal_range").getType() instanceof BmmGenericType);
        assertTrue ("BmmGSimpleType at path: (\"CARE_ENTRY\", \"/subject\")", rm102Model.propertyAtPath("CARE_ENTRY", "/subject").getType() instanceof BmmSimpleType);
    }

    @Test
    public void testRm102ModelDescendants() throws Exception {
        // test descendant relations
        assertTrue ("\"OBSERVATION\" descendant of \"LOCATABLE\")", rm102Model.descendantOf("OBSERVATION", "LOCATABLE"));
        assertFalse ("\"LOCATABLE\" not descendant of \"COMPOSITION\")", rm102Model.descendantOf("LOCATABLE", "COMPOSITION"));
        assertTrue ("\"ITEM_TREE\" descendant of \"ITEM_STRUCTURE\")", rm102Model.descendantOf("ITEM_TREE", "ITEM_STRUCTURE"));
        assertTrue ("\"DV_DURATION\" descendant of \"DV_ORDERED\")", rm102Model.descendantOf("DV_DURATION", "DV_ORDERED"));
    }

    @Test
    public void testRm102CompositionImmediateSuppliers() throws Exception {
        // test supplier relations
        Set<String> testResult = rm102Model.suppliers("COMPOSITION");

        // conformance result from ADL Workbench
        Set<String> conformanceResult = new LinkedHashSet<>(
                Arrays.asList(
                        "String", "UID_BASED_ID", "OBJECT_VERSION_ID", "HIER_OBJECT_ID",
                        "DV_TEXT", "DV_CODED_TEXT", "ARCHETYPED", "FEEDER_AUDIT", "LINK", "CODE_PHRASE", "PARTY_PROXY",
                        "PARTY_IDENTIFIED", "PARTY_SELF", "EVENT_CONTEXT", "CONTENT_ITEM", "SECTION", "ENTRY", "GENERIC_ENTRY"
                )
        );

        // if anyone wants to see the contents.
//        System.out.println("Test result: ");
//        String[] sortedTestResult = testResult.toArray(new String[testResult.size()]);
//        Arrays.sort (sortedTestResult);
//        System.out.println(Arrays.toString (sortedTestResult));
//
//        System.out.println("Conformance result: ");
//        String[] sortedConformanceResult = conformanceResult.toArray(new String[conformanceResult.size()]);
//        Arrays.sort (sortedConformanceResult);
//        System.out.println(Arrays.toString (sortedConformanceResult));

        assertTrue ("\"COMPOSITION\" has suppliers {\"CODE_PHRASE\", \"DV_TEXT\", ...})", testResult.equals (conformanceResult));
    }

    public void testRm102PartyIdentifiedSuppliers() throws Exception {
        // test supplier relations
        Set<String> testResult = rm102Model.supplierClosure("PARTY_IDENTIFIED");

        // conformance result from ADL Workbench
        Set<String> conformanceResult = new LinkedHashSet<>(
                Arrays.asList(
                        "String", "PARTY_REF", "DV_IDENTIFIER", "OBJECT_ID",
                        "TERMINOLOGY_ID", "UID_BASED_ID", "GENERIC_ID", "ARCHETYPE_ID", "TEMPLATE_ID"
                )
        );

        // if anyone wants to see the contents.
//        System.out.println("Test result: ");
//        String[] sortedTestResult = testResult.toArray(new String[testResult.size()]);
//        Arrays.sort (sortedTestResult);
//        System.out.println(Arrays.toString (sortedTestResult));
//
//        System.out.println("Conformance result: ");
//        String[] sortedConformanceResult = conformanceResult.toArray(new String[conformanceResult.size()]);
//        Arrays.sort (sortedConformanceResult);
//        System.out.println(Arrays.toString (sortedConformanceResult));

        assertTrue ("\"PARTY_IDENTIFIED\" has suppliers {\"String\", \"PARTY_REF\", ...})", testResult.equals (conformanceResult));
    }

}
