package com.nedap.archie.xml;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.AuthoredResource;
import com.nedap.archie.aom.LanguageSection;
import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.ResourceDescriptionItem;
import com.nedap.archie.aom.TranslationDetails;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.xml.types.XmlResourceDescriptionItem;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
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
                List<Class<?>> classes = new ArrayList<>();
                classes.addAll(ArchieAOMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
                classes.addAll(ArchieRMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
                //extra classes from the adapters package that are not directly referenced.\
                classes.add(XmlResourceDescriptionItem.class);
                removeClasses(classes);
                archieJaxbContext = JAXBContext.newInstance(classes.toArray(new Class[0]));
            } catch (JAXBException e) {
                throw new RuntimeException(e);//programmer error, tests will fail
            }
        }
    }

    public static synchronized JAXBContext createAOMContext() {
        try {
            List<Class<?>> classes = Lists.newArrayList(ArchieAOMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
            //extra classes from the adapters package that are not directly referenced.\
            classes.add(XmlResourceDescriptionItem.class);
            removeClasses(classes);
            return JAXBContext.newInstance(classes.toArray(new Class[0]));
        } catch (JAXBException e) {
            throw new RuntimeException(e);//programmer error, tests will fail
        }
    }

    public static synchronized JAXBContext createRMContext() {
        try {
            List<Class<?>> classes = Lists.newArrayList(ArchieRMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
            removeClasses(classes);
            return JAXBContext.newInstance(classes.toArray(new Class[0]));
        } catch (JAXBException e) {
            throw new RuntimeException(e);//programmer error, tests will fail
        }
    }

    /**
     * Removes all classes that are in the RM/AOM, but not used in the XML. In all of these cases,
     * custom mappers have been made to map these classes to XML-specific variants
     * @param classes
     */
    private static void removeClasses(List<Class<?>> classes) {
        removeAllInstances(classes, ResourceDescription.class);
        removeAllInstances(classes, ResourceDescriptionItem.class);
        removeAllInstances(classes, LanguageSection.class);
        removeAllInstances(classes, ArchetypeTerminology.class);
        removeAllInstances(classes, TranslationDetails.class);
        removeAllInstances(classes, AuthoredResource.class);
    }

    private static <T> void removeAllInstances(List<T> something, T instance) {
        boolean found;
        do {
             found = something.remove(instance);
        } while(found);
    }

}
