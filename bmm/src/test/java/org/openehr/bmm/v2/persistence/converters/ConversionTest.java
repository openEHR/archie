package org.openehr.bmm.v2.persistence.converters;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openehr.bmm.core.*;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;
import org.openehr.bmm.v2.persistence.odin.BmmOdinSerializer;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openehr.bmm.v2.persistence.converters.BmmTestUtil.parse;

public class ConversionTest {

    private static BmmRepository repo1;
    private static BmmRepository repo2;

    @BeforeClass
    public static void setup() throws Exception {
        BmmSchemaConverter converter;

        repo1 = new BmmRepository();
        repo1.addPersistentSchema(parse("/openehr/openehr_primitive_types_102.bmm"));
        repo1.addPersistentSchema(parse("/openehr/openehr_basic_types_102.bmm"));
        repo1.addPersistentSchema(parse("/openehr/openehr_adltest_100.bmm"));
        repo1.addPersistentSchema(parse("/openehr/openehr_base_110.bmm"));

        converter = new BmmSchemaConverter(repo1);
        converter.validateAndConvertRepository();
        for (BmmValidationResult validationResult:repo1.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the OpenEHR schema " + validationResult.getSchemaId() + " should pass validation", validationResult.passes());
        }

        repo2 = new BmmRepository();
        repo2.addPersistentSchema(parse("/openehr/openehr_base_for_aom.bmm"));
        repo2.addPersistentSchema(parse("/openehr/openEHR_aom_206.bmm"));
        converter = new BmmSchemaConverter(repo2);
        converter.validateAndConvertRepository();
        for (BmmValidationResult validationResult:repo2.getModels()) {
            System.out.println(validationResult.getLogger());
            assertTrue("the OpenEHR schema " + validationResult.getSchemaId() + " should pass validation", validationResult.passes());
        }
    }

    @AfterClass
    public static void tearDown() {
        repo1 = null;
        repo2 = null;
    }

    @Test
    public void testAdlTestSchema() throws Exception {
        BmmModel bmmModel = repo1.getModel("openehr_adltest_1.0.0").getModel();

        // Descendant relations
        assertTrue ("\"GENERIC_CHILD_CLOSED\" descendant of \"GENERIC_PARENT<SUPPLIER_A,SUPPLIER_B>\"",
                bmmModel.descendantOf("GENERIC_CHILD_CLOSED", "GENERIC_PARENT<SUPPLIER_A,SUPPLIER_B>"));

        // Ancestor relations
        List<String> testResult = bmmModel.getAllAncestorClasses("GENERIC_CHILD_OPEN_T");
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

    /*
     * This tests the following (old) BMM way of representing a property of type Hash<String,String>
     * that should now be represented using P_BMM_INDEXED_CONTAINER_PROPERTY
     *     ["original_resource_uri"] = (P_BMM_GENERIC_PROPERTY) <
     *          name = <"original_resource_uri">
     *         type_def = <
     *             root_type = <"Hash">
     *             generic_parameters = <"String", "String">
     *         >
     *         is_mandatory = <True>
     *     >
     */
    @Test
    public void generateGenericParametersTest() throws Exception {
        BmmModel bmmModel = repo1.getModel("openehr_base_1.1.0").getModel();
        BmmClass bmmClass = bmmModel.getClassDefinition("RESOURCE_DESCRIPTION_ITEM");
        BmmGenericType hashContentType = (BmmGenericType) bmmClass.getFlatProperties().get("original_resource_uri").getType().getEffectiveType();
        assertEquals(2, hashContentType.getGenericParameters().size());
        assertEquals("Hash<String,String>", hashContentType.toDisplayString());
    }

    /*
     * This tests the BMM way of representing a generic property of type DV_INTERVAL<DV_QUANTITY>
     *     		["qty_interval_attr_1"] = (P_BMM_GENERIC_PROPERTY) <
     * 				name = <"qty_interval_attr_1">
     * 				type_def = <
     * 					root_type = <"DV_INTERVAL">
     * 					generic_parameters = <"DV_QUANTITY">
     * 				>
     * 			>
     */
    @Test
    public void generateGenericParametersTest2() throws Exception {
        BmmModel bmmModel = repo1.getModel("openehr_adltest_1.0.0").getModel();
        BmmClass bmmClass = bmmModel.getClassDefinition("SOME_TYPE");
        BmmGenericType genericType = (BmmGenericType) bmmClass.getFlatProperties().get("qty_interval_attr_1").getType().getEffectiveType();
        assertEquals(1, genericType.getGenericParameters().size());
        assertEquals("DV_INTERVAL<DV_QUANTITY>", genericType.toDisplayString());
    }

    /*
     * This tests the BMM way of representing a container property of type Hash<String,String>
     * using the P_BMM_INDEXED_CONTAINER_PROPERTY property meta-type.
     *     		["other_details"] = (P_BMM_INDEXED_CONTAINER_PROPERTY) <
     * 				name = <"other_details">
     *                 type_def = <
     *                     container_type = <"Hash">
     *                     index_type = <"String">
     *                     type = <"String">
     *                 >
     * 			>
     */
    @Test
    public void generateIndexedContainerTest() throws Exception {
        BmmModel bmmModel = repo1.getModel("openehr_base_1.1.0").getModel();
        BmmClass bmmClass = bmmModel.getClassDefinition("RESOURCE_DESCRIPTION_ITEM");
        BmmIndexedContainerType hashType = (BmmIndexedContainerType) bmmClass.getFlatProperties().get("other_details").getType();

        BmmSimpleType hashIndexType = hashType.getIndexType();
        assertEquals(hashIndexType.getTypeName(), "String");

        BmmUnitaryType hashBaseType = hashType.getBaseType();
        assertEquals(hashBaseType.getTypeName(), "String");

        BmmGenericClass hashContainerClass = hashType.getContainerType();
        assertEquals(hashContainerClass.getName(), "Hash");

        assertEquals("Hash<String,String>", hashType.toDisplayString());
    }

    /*
     * RESOURCE_DESCRIPTION_ITEM.original_resource_uri in openehr_aom_2.0.6
     * should be a LIST<HASH<STRING, STRING>>
     */
    @Test
    public void aomParseAndConvertTest() throws Exception {
        BmmModel bmmModel = repo2.getModel("openehr_aom_2.0.6").getModel();
        BmmClass bmmClass = bmmModel.getClassDefinition("RESOURCE_DESCRIPTION_ITEM");
        BmmGenericType hashContentType = (BmmGenericType) bmmClass.getFlatProperties().get("original_resource_uri").getType().getEffectiveType();
        assertEquals(2, hashContentType.getGenericParameters().size());
        assertEquals("Hash<String,Uri>", hashContentType.toDisplayString());//this is not according to spec perhaps, but it is how the Archie AOM is implemented, and this is a direct conversion
    }

}
