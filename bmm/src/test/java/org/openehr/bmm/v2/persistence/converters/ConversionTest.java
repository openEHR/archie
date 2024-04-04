package org.openehr.bmm.v2.persistence.converters;

import org.junit.Test;
import org.openehr.bmm.core.*;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;
import org.openehr.bmm.v2.persistence.odin.BmmOdinSerializer;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openehr.bmm.v2.persistence.converters.BmmTestUtil.parse;

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
        BmmModel adlTestModel = repo.getModel("openehr_adltest_1.0.0").getModel();

        // Descendant relations
        assertTrue ("\"GENERIC_CHILD_CLOSED\" descendant of \"GENERIC_PARENT<SUPPLIER_A,SUPPLIER_B>\"",
                adlTestModel.descendantOf("GENERIC_CHILD_CLOSED", "GENERIC_PARENT<SUPPLIER_A,SUPPLIER_B>"));

        // Ancestor relations
        List<String> testResult = adlTestModel.getAllAncestorClasses("GENERIC_CHILD_OPEN_T");
        // conformance result from ADL Workbench
        List<String> conformanceResult = Arrays.asList("GENERIC_PARENT<T,SUPPLIER_B>");

        assertEquals ("\"GENERIC_CHILD_CLOSED\" descendant of \"GENERIC_PARENT\")", testResult, conformanceResult);

    }


    @Test
    public void generateOdinTest() throws  Exception {
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
    public void generateGenericParametersTest2() throws Exception {
        BmmRepository repo = new BmmRepository();
        repo.addPersistentSchema(parse("/openehr/openehr_primitive_types_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_basic_types_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_adltest_100.bmm"));
        BmmSchemaConverter converter = new BmmSchemaConverter(repo);
        converter.validateAndConvertRepository();
        for (BmmValidationResult validationResult:repo.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the OpenEHR ADL test 1.0.0 Base file should pass validation", validationResult.passes());
        }
        // SOME_TYPE.qty_interval_attr_1 should be a DV_INTERVAL<DV_QUANTITY>
        BmmModel model = repo.getModel("openehr_adltest_1.0.0").getModel();
        BmmClass aClass = model.getClassDefinition("SOME_TYPE");
        BmmGenericType genericType = (BmmGenericType) aClass.getFlatProperties().get("qty_interval_attr_1").getType().getEffectiveType();
        assertEquals(1, genericType.getGenericParameters().size());
        assertEquals("DV_INTERVAL<DV_QUANTITY>", genericType.toDisplayString());
    }

    @Test
    public void generateIndexedContainerTest() throws Exception {
        BmmRepository repo = new BmmRepository();
        repo.addPersistentSchema(parse("/openehr/openehr_primitive_types_102.bmm"));
        repo.addPersistentSchema(parse("/openehr/openehr_base_110.bmm"));
        BmmSchemaConverter converter = new BmmSchemaConverter(repo);
        converter.validateAndConvertRepository();
        for (BmmValidationResult validationResult:repo.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the OpenEHR RM 1.1.0 Base file should pass validation", validationResult.passes());
        }
        // RESOURCE_DESCRIPTION_ITEM.other_details should be a HASH<STRING, STRING>
        BmmModel baseModel = repo.getModel("openehr_base_1.1.0").getModel();
        BmmClass resourceDescriptionItem = baseModel.getClassDefinition("RESOURCE_DESCRIPTION_ITEM");
        BmmIndexedContainerType hashType = (BmmIndexedContainerType) resourceDescriptionItem.getFlatProperties().get("other_details").getType();

        BmmSimpleType hashIndexType = hashType.getIndexType();
        assertEquals(hashIndexType.getTypeName(), "String");

        BmmUnitaryType hashBaseType = hashType.getBaseType();
        assertEquals(hashBaseType.getTypeName(), "String");

        BmmGenericClass hashContainerclass = hashType.getContainerType();
        assertEquals(hashContainerclass.getName(), "Hash");

        assertEquals("Hash<String,String>", hashType.toDisplayString());
    }

    @Test
    public void aomParseAndConvertTest() throws Exception {
        BmmRepository repo = new BmmRepository();
        repo.addPersistentSchema(parse("/openehr/openehr_base_for_aom.bmm"));
        repo.addPersistentSchema(parse("/openehr/openEHR_aom_206.bmm"));
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

}
