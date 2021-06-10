package com.nedap.archie.openapi;

import com.nedap.archie.json.flat.AttributeReference;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriterFactory;
import jakarta.json.stream.JsonGenerator;
import org.junit.Test;
import org.openehr.bmm.core.BmmModel;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OpenAPIModelCreatorTest {

    @Test
    public void createSchema() {
        BmmModel model = BuiltinReferenceModels.getBmmRepository().getModel("openehr_rm_1.0.4").getModel();
        OpenAPIModelCreator openAPIModelCreator = new OpenAPIModelCreator().allowAdditionalProperties(true);

        //let's do some annoying polymorphism mapping!
        Set<AttributeReference> ignoredAttributes = new HashSet<>();
        //abstract any item. Not useful at all.
        ignoredAttributes.add(new AttributeReference("EXTRACT_CONTENT_ITEM", "item"));
        //generics redefinition. No generics support, so just DV_INTERVAL here.
        ignoredAttributes.add(new AttributeReference("DV_QUANTITY", "normal_range"));
        ignoredAttributes.add(new AttributeReference("DV_PROPORTION", "normal_range"));
        ignoredAttributes.add(new AttributeReference("DV_COUNT", "normal_range"));
        //same generics redefinition
        ignoredAttributes.add(new AttributeReference("DV_QUANTITY", "other_reference_ranges"));
        ignoredAttributes.add(new AttributeReference("DV_PROPORTION", "other_reference_ranges"));
        ignoredAttributes.add(new AttributeReference("DV_COUNT", "other_reference_ranges"));
        //uid is always the same except in extract. Let's keep it the same everywhere.
        ignoredAttributes.add(new AttributeReference("EXTRACT_REQUEST", "uid"));
        ignoredAttributes.add(new AttributeReference("PARTY", "uid"));
        ignoredAttributes.add(new AttributeReference("EXTRACT_ACTION_REQUEST", "uid"));
        //this makes id more specific, but objectref is also concrete, so let's not do that.
        ignoredAttributes.add(new AttributeReference("LOCATABLE_REF", "id"));

        //accuracy: redefined. Ignore, define in concrete types instead
        //keep DV_AMOUNT and DV_TEMPORAL here
        ignoredAttributes.add(new AttributeReference("DV_QUANTIFIED", "accuracy"));
        ignoredAttributes.add(new AttributeReference("DV_ABSOLUTE_QUANTITY", "accuracy"));

        List<AttributeReference> attributesFromParent = new ArrayList<>();
        openAPIModelCreator.setIgnoredAttributes(ignoredAttributes);

        openAPIModelCreator.setAddAttributesFromParent(attributesFromParent);
        JsonObject jsonObject = openAPIModelCreator.create(model);

        Map<String, Object> config = new HashMap();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);


        jsonWriterFactory.createWriter(System.out).write(jsonObject);
    }
}
