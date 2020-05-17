package com.nedap.archie.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nedap.archie.json.flat.AttributeReference;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import org.junit.Test;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.odin.BmmOdinSerializer;

import java.util.List;
import java.util.Set;

public class ModelInfoLookupToBmmConverterTest {

    @Test
    public void testAom() throws Exception {
        Set<AttributeReference> ignoredAttributes = Sets.newHashSet(
                new AttributeReference("C_OBJECT", "archetype"),
                new AttributeReference("C_COMPLEX_OBJECT", "archetype"),
                new AttributeReference("C_ATTRIBUTE", "archetype"),
                new AttributeReference("ARCHETYPE_TERMINOLOGY", "owner_archetype"),
                new AttributeReference("TEMPLATE_OVERLAY", "owning_template")
        );
        PBmmSchema schema = new ModelInfoLookupToPBmmConverter().convert(ArchieAOMInfoLookup.getInstance(), ignoredAttributes);
        System.out.println(new BmmOdinSerializer().serialize(schema));
    }
}
