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

import static org.junit.Assert.*;
import static org.openehr.bmm.v2.persistence.converters.BmmTestUtil.parse;

public class RM102ConversionTest {

    private static BmmModel rm102Model;
    private static BmmModel demographic102Model;
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
        demographic102Model = repo.getModel("openehr_demographic_1.0.2").getModel();
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
        assertEquals ("\"COMPOSITION\" has suppliers {\"CODE_PHRASE\", \"DV_TEXT\", ...})", testResult, conformanceResult);
    }

    @Test
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

        assertEquals ("\"PARTY_IDENTIFIED\" has suppliers {\"String\", \"PARTY_REF\", ...})", testResult, conformanceResult);
    }

    @Test
    public void testFindAllNonPrimitiveClasses() throws Exception {

        Set<String> bmmClassNames = new LinkedHashSet<>();
        bmmClassNames.addAll(rm102Model.getClassDefinitions().keySet());
        bmmClassNames.addAll(demographic102Model.getClassDefinitions().keySet());
        bmmClassNames.removeAll(rm102Model.getPrimitiveTypes());

        // conformance result from ADL Workbench
        Set<String> conformanceResult = new LinkedHashSet<>(
                Arrays.asList(
                        "ACCESS_CONTROL_SETTINGS", "ACTION", "ACTIVITY", "ACTOR", "ADDRESS", "ADMIN_ENTRY", "AGENT", "ARCHETYPED", "ARCHETYPE_ID",
                        "ATTESTATION", "AUDIT_DETAILS", "CAPABILITY", "CARE_ENTRY", "CLUSTER", "CODE_PHRASE", "COMPOSITION", "CONTACT",
                        "CONTENT_ITEM", "CONTRIBUTION", "DATA_STRUCTURE", "DATA_VALUE", "DV_ABSOLUTE_QUANTITY", "DV_AMOUNT", "DV_BOOLEAN",
                        "DV_CODED_TEXT", "DV_COUNT", "DV_DATE", "DV_DATE_TIME", "DV_DURATION", "DV_EHR_URI", "DV_ENCAPSULATED", "DV_GENERAL_TIME_SPECIFICATION",
                        "DV_IDENTIFIER", "DV_INTERVAL", "DV_MULTIMEDIA", "DV_ORDERED", "DV_ORDINAL", "DV_PARAGRAPH", "DV_PARSABLE", "DV_PERIODIC_TIME_SPECIFICATION",
                        "DV_PROPORTION", "DV_QUANTIFIED", "DV_QUANTITY", "DV_STATE", "DV_TEMPORAL", "DV_TEXT", "DV_TIME", "DV_TIME_SPECIFICATION",
                        "DV_URI", "EHR", "EHR_ACCESS", "EHR_STATUS", "ELEMENT", "ENTRY", "EVALUATION", "EVENT", "EVENT_CONTEXT", "FEEDER_AUDIT",
                        "FEEDER_AUDIT_DETAILS", "FOLDER", "GENERIC_ENTRY", "GENERIC_ID", "GROUP", "HIER_OBJECT_ID", "HISTORY", "IMPORTED_VERSION",
                        "INSTRUCTION", "INSTRUCTION_DETAILS", "INTERNET_ID", "INTERVAL_EVENT", "ISM_TRANSITION", "ISO_OID", "ITEM", "ITEM_LIST",
                        "ITEM_SINGLE", "ITEM_STRUCTURE", "ITEM_TABLE", "ITEM_TREE", "LINK", "LOCATABLE", "LOCATABLE_REF", "OBJECT_ID", "OBJECT_REF",
                        "OBJECT_VERSION_ID", "OBSERVATION", "ORGANISATION", "ORIGINAL_VERSION", "PARTICIPATION", "PARTY", "PARTY_IDENTIFIED",
                        "PARTY_IDENTITY", "PARTY_PROXY", "PARTY_REF", "PARTY_RELATED", "PARTY_RELATIONSHIP", "PARTY_SELF", "PATHABLE", "PERSON",
                        "POINT_EVENT", "PROPORTION_KIND", "REFERENCE_RANGE", "REVISION_HISTORY", "REVISION_HISTORY_ITEM", "ROLE", "SECTION",
                        "TEMPLATE_ID", "TERMINOLOGY_ID", "TERM_MAPPING", "UID", "UID_BASED_ID", "UUID", "VERSION", "VERSIONED_OBJECT", "VERSION_TREE_ID"
                )
        );

        assertEquals ("\"RM 1.0.2 BMM\" has classes {\"EHR\", \"COMPOSITION\", ...})", bmmClassNames, conformanceResult);
    }

}
