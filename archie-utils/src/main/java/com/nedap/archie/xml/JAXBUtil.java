package com.nedap.archie.xml;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.LanguageSection;
import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.ResourceDescriptionItem;
import com.nedap.archie.aom.TranslationDetails;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.xml.types.XmlIncludedTerminology;
import com.nedap.archie.xml.types.XmlResourceDescriptionItem;
import com.nedap.archie.xml.types.XmlTranslationDetails;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 06/05/16.
 */
public class JAXBUtil {

    private static JAXBContext archieJaxbContext;

    /**
     * Get the JAXBContext to work with archie reference model objects.
     *
     * Can marshal and unmarshal reference model object trees.
     *
     * @return
     */
    public static JAXBContext getArchieJAXBContext() {
        if(archieJaxbContext == null) {
            initArchieJaxbContext();
        }
        return archieJaxbContext;
    }

    private static synchronized void initArchieJaxbContext() {
        if(archieJaxbContext == null) {
            try {
                List<Class> classes = new ArrayList<>();
                classes.addAll(ArchieAOMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
                classes.addAll(ArchieRMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
                //extra classes from the adapters package that are not directly referenced.\
                classes.add(XmlResourceDescriptionItem.class);
                archieJaxbContext = JAXBContext.newInstance(classes.toArray(new Class[0]));
            } catch (JAXBException e) {
                throw new RuntimeException(e);//programmer error, tests will fail
            }
        }
    }

    public static synchronized JAXBContext createAOMContext() {
        try {
            List<Class> classes = Lists.newArrayList(ArchieAOMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
            classes.remove(ResourceDescription.class);
            classes.remove(ResourceDescriptionItem.class);
            classes.remove(LanguageSection.class);
            classes.remove(TranslationDetails.class);
            //extra classes from the adapters package that are not directly referenced.\
            classes.add(XmlResourceDescriptionItem.class);
            classes.add(XmlIncludedTerminology.class);
            classes.add(XmlTranslationDetails.class);
            return JAXBContext.newInstance(classes.toArray(new Class[0]));
        } catch (JAXBException e) {
            throw new RuntimeException(e);//programmer error, tests will fail
        }
    }

    public static synchronized JAXBContext createRMContext() {
        try {
            List<Class> classes = Lists.newArrayList(ArchieRMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
            return JAXBContext.newInstance(classes.toArray(new Class[0]));
        } catch (JAXBException e) {
            throw new RuntimeException(e);//programmer error, tests will fail
        }
    }

}
