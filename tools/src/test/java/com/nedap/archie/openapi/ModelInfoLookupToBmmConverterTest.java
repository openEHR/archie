package com.nedap.archie.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nedap.archie.json.flat.AttributeReference;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import org.junit.Test;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;
import org.openehr.bmm.v2.persistence.odin.BmmOdinSerializer;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.openehr.bmm.v2.validation.BmmValidationResult;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ModelInfoLookupToBmmConverterTest {

    @Test
    public void testAom() throws Exception {
        Set<AttributeReference> ignoredAttributes = Sets.newHashSet(
                new AttributeReference("C_OBJECT", "archetype"),
                new AttributeReference("C_COMPLEX_OBJECT", "archetype"),
                new AttributeReference("C_ATTRIBUTE", "archetype"),
                new AttributeReference("ARCHETYPE_TERMINOLOGY", "owner_archetype"),
                new AttributeReference("TEMPLATE_OVERLAY", "owning_template"),
                new AttributeReference("ARCHETYPE_CONSTRAINT", "parent"),
                new AttributeReference("ARCHETYPE_CONSTRAINT", "soc_parent")
        );




        BmmRepository bmmRepository = new BmmRepository();
        try(InputStream stream = getClass().getResourceAsStream("openehr_base_for_aom.bmm")) {
            PBmmSchema baseShema = BmmOdinParser.convert(stream);
            bmmRepository.addPersistentSchema(baseShema);
        }
        BmmSchemaConverter bmmSchemaConverter = new BmmSchemaConverter(bmmRepository);
        bmmSchemaConverter.validateAndConvertRepository();
        BmmValidationResult baseModel = bmmRepository.getModels().get(0);
        assertTrue(baseModel.getLogger().toString(), baseModel.passes());
        PBmmSchema schema = new ModelInfoLookupToPBmmConverter().convert(ArchieAOMInfoLookup.getInstance(), ignoredAttributes, baseModel.getModel());
        System.out.println(new BmmOdinSerializer().serialize(schema));
        BmmValidationResult bmmValidationResult = bmmSchemaConverter.validateConvertAndAddToRepo(schema);
        assertTrue(bmmValidationResult.getLogger().toString(), bmmValidationResult.passes());

    }
}
