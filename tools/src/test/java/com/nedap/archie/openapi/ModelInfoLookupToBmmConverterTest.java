package com.nedap.archie.openapi;

import com.google.common.collect.Sets;
import com.nedap.archie.json.flat.AttributeReference;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import org.junit.Test;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;
import org.openehr.bmm.v2.persistence.odin.BmmOdinSerializer;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.openehr.bmm.v2.validation.BmmValidationResult;
import org.w3c.dom.Attr;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ModelInfoLookupToBmmConverterTest {

    @Test
    public void testAom() throws Exception {
        Set<AttributeReference> ignoredAttributes = Sets.newHashSet();
//                new AttributeReference("C_OBJECT", "archetype"),
//                new AttributeReference("C_COMPLEX_OBJECT", "archetype"),
//                new AttributeReference("C_ATTRIBUTE", "archetype"),
//                new AttributeReference("ARCHETYPE_TERMINOLOGY", "owner_archetype"),
//                new AttributeReference("TEMPLATE_OVERLAY", "owning_template"),
//                new AttributeReference("ARCHETYPE_CONSTRAINT", "parent"),
//                new AttributeReference("ARCHETYPE_CONSTRAINT", "soc_parent")
//        );




        BmmRepository bmmRepository = new BmmRepository();
        try(InputStream stream = getClass().getResourceAsStream("openehr_base_for_aom.bmm")) {
            PBmmSchema baseShema = BmmOdinParser.convert(stream);
            bmmRepository.addPersistentSchema(baseShema);
        }
        BmmSchemaConverter bmmSchemaConverter = new BmmSchemaConverter(bmmRepository);
        bmmSchemaConverter.validateAndConvertRepository();
        BmmValidationResult baseModel = bmmRepository.getModels().get(0);
        assertTrue(baseModel.getLogger().toString(), baseModel.passes());
        ModelInfoLookupToPBmmConverter modelInfoLookupToPBmmConverter = new ModelInfoLookupToPBmmConverter();

        //this makes sure we get the JSON version, not something else
        modelInfoLookupToPBmmConverter.setExcludedJsonIgnoreFields(true);

        PBmmSchema schema = modelInfoLookupToPBmmConverter.convert(ArchieAOMInfoLookup.getInstance(), ignoredAttributes, baseModel.getModel());
        System.out.println(new BmmOdinSerializer().serialize(schema));
        BmmValidationResult bmmValidationResult = bmmSchemaConverter.validateConvertAndAddToRepo(schema);
        assertTrue(bmmValidationResult.getLogger().toString(), bmmValidationResult.passes());

    }


    @Test
    public void testAomOpenAPI() throws Exception {
        Set<AttributeReference> ignoredAttributes = Sets.newHashSet();
        ignoredAttributes.add(new AttributeReference("C_PRIMITIVE_OBJECT", "node_id"));

        BmmRepository bmmRepository = new BmmRepository();
        try(InputStream stream = getClass().getResourceAsStream("openehr_base_for_aom.bmm")) {
            PBmmSchema baseSchema = BmmOdinParser.convert(stream);
            bmmRepository.addPersistentSchema(baseSchema);
        }
        BmmSchemaConverter bmmSchemaConverter = new BmmSchemaConverter(bmmRepository);
        bmmSchemaConverter.validateAndConvertRepository();
        BmmValidationResult baseModel = bmmRepository.getModels().get(0);
        assertTrue(baseModel.getLogger().toString(), baseModel.passes());

        ModelInfoLookupToPBmmConverter modelInfoLookupToPBmmConverter = new ModelInfoLookupToPBmmConverter();
        //this makes sure we get the JSON version, not something else
        modelInfoLookupToPBmmConverter.setExcludedJsonIgnoreFields(true);
        PBmmSchema schema = modelInfoLookupToPBmmConverter.convert(ArchieAOMInfoLookup.getInstance(), ignoredAttributes, baseModel.getModel());

        BmmValidationResult bmmValidationResult = bmmSchemaConverter.validateConvertAndAddToRepo(schema);
        assertTrue(bmmValidationResult.getLogger().toString(), bmmValidationResult.passes());

        OpenAPIModelCreator creator = new OpenAPIModelCreator()
                .allowAdditionalProperties(true)
                .withTypePropertyName("@type")
                .writeOneOf(true)
                .withExampleRootTypeName("Archetype");

        //not part of the AOM, but get added because of reasons, remove them.
        creator.addIgnoredType("OBJECT_REF");
        creator.addIgnoredType("LOCATABLE_REF");
        creator.addIgnoredType("PARTY_REF");
        creator.addIgnoredType("ACCESS_GROUP_REF");
        //this class should not be added, it is an internal Archie construct and not used in json
        creator.addIgnoredType("LANGUAGE_SECTION");
        //TODO: these should not be present by default, but should be added to the RM generator!
        creator.removeIgnoredType("TRANSLATION_DETAILS");
        creator.removeIgnoredType("AUTHORED_RESOURCE");
        creator.removeIgnoredType("RESOURCE_DESCRIPTION");
        creator.removeIgnoredType("RESOURCE_DESCRIPTION_ITEM");
        creator.ignoreAncestors("MULTIPLICITY_INTERVAL");
        //this is not picked up correctly by generics (and it shouldn't be), so needs manual mapping
        creator.overrideType("MULTIPLICITY_INTERVAL", "lower", "integer");
        creator.overrideType("MULTIPLICITY_INTERVAL", "upper", "integer");

        JsonObject jsonObject = creator.create(bmmValidationResult.getModel());
        Map<String, Object> config = new HashMap();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);


        jsonWriterFactory.createWriter(System.out).write(jsonObject);

    }
}
