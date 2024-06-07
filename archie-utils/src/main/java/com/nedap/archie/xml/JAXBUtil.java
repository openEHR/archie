package com.nedap.archie.xml;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.xml.types.XmlResourceDescriptionItem;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.List;

/**
 * Created by pieter.bos on 06/05/16.
 */
public class JAXBUtil {

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

    /**
     * Removes all classes that are in the RM/AOM, but not used in the XML. In all of these cases,
     * custom mappers have been made to map these classes to XML-specific variants
     * @param classes
     */
    protected static void removeClasses(List<Class<?>> classes) {
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
