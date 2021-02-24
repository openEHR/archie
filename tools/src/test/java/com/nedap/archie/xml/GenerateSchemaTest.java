package com.nedap.archie.xml;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.LanguageSection;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.ResourceDescriptionItem;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TranslationDetails;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.xml.types.XmlIncludedTerminology;
import com.nedap.archie.xml.types.XmlResourceDescriptionItem;
import com.nedap.archie.xml.types.XmlTranslationDetails;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GenerateSchemaTest {

    @Test
    public void test() throws Exception {
        createAOMContext().generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                File file = new File(suggestedFileName);
                StreamResult result = new StreamResult(file);
                result.setSystemId(file.toURI().toURL().toString());
                return result;
            }
        });
    }

    private static synchronized JAXBContext createAOMContext() {
        try {
            List<Class> classes = Lists.newArrayList(ArchieAOMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
            classes.remove(ResourceDescription.class);
            classes.remove(ResourceDescriptionItem.class);
            classes.remove(LanguageSection.class);
            classes.remove(TranslationDetails.class);
            classes.remove(Template.class);
            classes.remove(OperationalTemplate.class);
            classes.remove(AuthoredArchetype.class);
            //extra classes from the adapters package that are not directly referenced.\
            classes.add(XmlResourceDescriptionItem.class);
            classes.add(XmlIncludedTerminology.class);
            classes.add(XmlTranslationDetails.class);
            //classes.add(XmlOperationalTemplate.class);
           // classes.add(XmlArchetype.class);
            //classes.add(XmlTemplate.class);
            return JAXBContext.newInstance(classes.toArray(new Class[0]));
        } catch (JAXBException e) {
            throw new RuntimeException(e);//programmer error, tests will fail
        }
    }
}
