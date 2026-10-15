package com.nedap.archie.json;

import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.testutil.TestUtil;
import com.nedap.archie.xml.JAXBUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openehr.referencemodels.BuiltinReferenceModels;

import jakarta.xml.bind.Marshaller;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A template overlay has no RM release of its own, it uses the RM release of its owning template. So the owning
 * template must also be set when a template is deserialized from JSON or XML, not only when it is parsed from ADL.
 */
public class TemplateOverlayOwningTemplateTest {

    private static final String TEMPLATE = "/com/nedap/archie/flattener/openEHR-EHR-COMPOSITION.blood_pressure.v1.0.0.adlt";

    @ParameterizedTest
    @MethodSource("com.nedap.archie.json.JacksonTestMappers#mappers")
    public void owningTemplateSetAfterJsonDeserialization(JacksonTestMappers.JsonMapperFactory factory) throws Exception {
        Template template = (Template) TestUtil.parseFailOnErrors(TEMPLATE);
        JacksonTestMappers.JsonMapper mapper = factory.create();
        Template parsed = mapper.readValue(mapper.writeValueAsString(template), Template.class);
        assertOwningTemplateAndMetaModel(parsed);
    }

    @Test
    public void owningTemplateSetAfterXmlUnmarshalling() throws Exception {
        Template template = (Template) TestUtil.parseFailOnErrors(TEMPLATE);
        Marshaller marshaller = JAXBUtil.getArchieJAXBContext().createMarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(template, writer);
        Template parsed = (Template) JAXBUtil.getArchieJAXBContext().createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertOwningTemplateAndMetaModel(parsed);
    }

    private void assertOwningTemplateAndMetaModel(Template template) {
        assertFalse(template.getTemplateOverlays().isEmpty());
        for (TemplateOverlay overlay : template.getTemplateOverlays()) {
            assertSame(template, overlay.getOwningTemplate());
            MetaModel metaModel = BuiltinReferenceModels.getMetaModelProvider().getMetaModel(overlay);
            assertEquals(template.getRmRelease(), metaModel.getBmmModel().getRmRelease());
        }
    }
}
