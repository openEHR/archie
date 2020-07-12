package org.openehr.bmm.v2.persistence.converters;

import org.junit.Test;
import org.openehr.bmm.core.*;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;
import org.openehr.bmm.v2.persistence.odin.BmmOdinSerializer;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.junit.Assert.*;

public class ConversionTest {

    @Test
    public void testAdlTestSchema() throws Exception {
        BmmRepository repo = new BmmRepository();
        repo.addPersistentSchema(parse("/openehr/openehr_primitive_types_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_basic_types_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_adltest_100.bmm"));

        BmmSchemaConverter converter = new BmmSchemaConverter(repo);
        converter.validateAndConvertRepository();
        for (BmmValidationResult validationResult:repo.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the OpenEHR ADL Test 1.0.0 BMM files should pass validation", validationResult.passes());
        }
    }

    @Test
    public void testRm102() throws Exception {
        BmmRepository repo = new BmmRepository();
        repo.addPersistentSchema(parse("/openehr/openehr_basic_types_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_demographic_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_ehr_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_primitive_types_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_rm_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_structures_102.bmm"));

        BmmSchemaConverter converter = new BmmSchemaConverter(repo);
        converter.validateAndConvertRepository();
        for (BmmValidationResult validationResult:repo.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the OpenEHR RM 1.0.2 BMM files should pass validation", validationResult.passes());
        }

        Rm102Model = repo.getModel("openehr_ehr_1.0.2").getModel();
        testRm102ModelPaths ();
        testRm102PropertyMetatypes ();
        testRm102ModelDescendants();
        testRm102ImmediateSuppliers();
    }

    public void testRm102ModelPaths() throws Exception {
        // check some paths through the model
        assertTrue ("property defined in class: (\"CARE_ENTRY\", \"/protocol\")", Rm102Model.hasPropertyAtPath("CARE_ENTRY", "/protocol"));
        assertTrue ("property defined in descendant: (\"CARE_ENTRY\", \"/data/events/data\")", Rm102Model.hasPropertyAtPath("CARE_ENTRY", "/data/events/data"));
        assertTrue ("property defined in ancestor: (\"OBSERVATION\", \"/protocol\")", Rm102Model.hasPropertyAtPath("OBSERVATION", "/protocol"));
        assertTrue ("property defined in class: (\"OBSERVATION\", \"/data/events/data\")", Rm102Model.hasPropertyAtPath("OBSERVATION", "/data/events/data"));
        assertTrue ("property defined in descendant: (\"OBSERVATION\", \"/data/events/data/items\")", Rm102Model.hasPropertyAtPath("OBSERVATION", "/data/events/data/items"));
        assertFalse ("non-existent property: (\"OBSERVATION\", \"/data/events/data/xxx\")", Rm102Model.hasPropertyAtPath("OBSERVATION", "/data/events/data/xxx"));
        assertTrue ("property defined in class: (\"COMPOSITION\", \"/context\")", Rm102Model.hasPropertyAtPath("COMPOSITION", "/context"));
        assertTrue ("property defined in descendant: (\"CLUSTER\", \"/items/items/items\")", Rm102Model.hasPropertyAtPath("CLUSTER", "/items/items/items"));
        assertTrue ("property defined in class: (\"DV_QUANTITY\", \"/normal_range\")", Rm102Model.hasPropertyAtPath("DV_QUANTITY", "/normal_range"));
    }

    public void testRm102PropertyMetatypes() throws Exception {
        // check meta-types of some properties at paths
        assertTrue ("BmmSimpleType at path: (\"OBSERVATION\", \"/protocol\")", Rm102Model.propertyAtPath("OBSERVATION", "/protocol").getType() instanceof BmmSimpleType);
        assertTrue ("BmmContainerType at path: (\"OBSERVATION\", \"/data/events/data/items\")", Rm102Model.propertyAtPath("OBSERVATION", "/data/events/data/items").getType() instanceof BmmContainerType);
        assertTrue ("BmmGenericType at path: (\"DV_QUANTITY\", \"/normal_range\").getType() is BmmGenericType", Rm102Model.propertyAtPath("DV_QUANTITY", "/normal_range").getType() instanceof BmmGenericType);
    }

    public void testRm102ModelDescendants() throws Exception {
        // test descendant relations
        assertTrue ("\"OBSERVATION\" descendant of \"LOCATABLE\")", Rm102Model.descendantOf("OBSERVATION", "LOCATABLE"));
        assertFalse ("\"LOCATABLE\" not descendant of \"COMPOSITION\")", Rm102Model.descendantOf("LOCATABLE", "COMPOSITION"));
        assertTrue ("\"ITEM_TREE\" descendant of \"ITEM_STRUCTURE\")", Rm102Model.descendantOf("ITEM_TREE", "ITEM_STRUCTURE"));
    }

    public void testRm102ImmediateSuppliers() throws Exception {
        // test supplier relations
        Set<String> compositionSuppliers = Rm102Model.suppliers("COMPOSITION");
        assertTrue ("\"COMPOSITION\" has supplier \"CODE_PHRASE\")", compositionSuppliers.contains("CODE_PHRASE"));
        assertTrue ("\"COMPOSITION\" has supplier \"DV_CODED_TEXT\")", compositionSuppliers.contains("DV_CODED_TEXT"));
        assertTrue ("\"COMPOSITION\" has supplier \"PARTY_PROXY\")", compositionSuppliers.contains("PARTY_PROXY"));
        assertTrue ("\"COMPOSITION\" has supplier \"PARTY_IDENTIFIED\")", compositionSuppliers.contains("PARTY_IDENTIFIED"));
        assertFalse ("\"COMPOSITION\" not has supplier \"OBSERVATION\")", compositionSuppliers.contains("OBSERVATION"));
    }

    private PBmmSchema parse(String name) throws IOException  {
        try (InputStream stream = getClass().getResourceAsStream(name)) {
            return BmmOdinParser.convert(stream);
        }
    }

    @Test
    public void generateOdinTest() throws  Exception{
        PBmmSchema parsed = parse("/openehr/openehr_basic_types_102.bmm");
        String serialized = new BmmOdinSerializer().serialize(parsed);
        //check that it can be parsed again
        PBmmSchema converted = BmmOdinParser.convert(serialized);
    }


    @Test
    public void generateGenericParametersTest() throws Exception {
        BmmRepository repo = new BmmRepository();
        repo.addPersistentSchema(parse("/openehr/openehr_base_110.bmm"));
        BmmSchemaConverter converter = new BmmSchemaConverter(repo);
        converter.validateAndConvertRepository();
        for (BmmValidationResult validationResult:repo.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the OpenEHR RM 1.1.0 Base file should pass validation", validationResult.passes());
        }
        //RESOURCE_DESCRIPTION_ITEM.original_resource_uri should be a LIST<HASH<STRING, STRING>>
        BmmModel baseModel = repo.getModel("openehr_base_1.1.0").getModel();
        BmmClass resourceDescriptionItem = baseModel.getClassDefinition("RESOURCE_DESCRIPTION_ITEM");
        BmmGenericType hashContentType = (BmmGenericType) resourceDescriptionItem.getFlatProperties().get("original_resource_uri").getType().getEffectiveType();
        assertEquals(2, hashContentType.getGenericParameters().size());
        assertEquals("Hash<String,String>", hashContentType.toDisplayString());
    }

    @Test
    public void aomParseAndConvertTest() throws Exception {
        BmmRepository repo = new BmmRepository();
        repo.addPersistentSchema(parse("/openehr/openEHR_base_for_aom.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_aom_206.bmm"));
        BmmSchemaConverter converter = new BmmSchemaConverter(repo);
        converter.validateAndConvertRepository();
        for (BmmValidationResult validationResult:repo.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the AOM schema must be valid", validationResult.passes());
        }
        //RESOURCE_DESCRIPTION_ITEM.original_resource_uri should be a LIST<HASH<STRING, STRING>>
        BmmModel baseModel = repo.getModel("openehr_aom_2.0.6").getModel();
        BmmClass resourceDescriptionItem = baseModel.getClassDefinition("RESOURCE_DESCRIPTION_ITEM");
        BmmGenericType hashContentType = (BmmGenericType) resourceDescriptionItem.getFlatProperties().get("original_resource_uri").getType().getEffectiveType();
        assertEquals(2, hashContentType.getGenericParameters().size());
        assertEquals("Hash<String,Uri>", hashContentType.toDisplayString());//this is not according to spec perhaps, but it is how the Archie AOM is implemented, and this is a direct conversion
    }

    private BmmModel Rm102Model;

}
