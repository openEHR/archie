package com.nedap.archie.openehr.serialisation.xml;

import com.google.common.collect.Lists;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.xml.JAXBUtil;
import com.nedap.archie.xml.types.XmlResourceDescriptionItem;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 06/05/16.
 */
public class OpenEhrRmJAXBUtil extends JAXBUtil {

    private static JAXBContext jaxbContext;

    /**
     * Get the JAXBContext to work with archie reference model objects.
     *
     * Can marshal and unmarshal reference model object trees.
     *
     * @return
     */
    public static JAXBContext getArchieJAXBContext() {
        if(jaxbContext == null) {
            initJaxbContext();
        }
        return jaxbContext;
    }

    private static synchronized void initJaxbContext() {
        if(jaxbContext == null) {
            try {
                List<Class<?>> classes = new ArrayList<>();
                classes.addAll(ArchieAOMInfoLookup.getInstance().getRmTypeNameToClassMap().values());
                classes.addAll(OpenEhrRmInfoLookup.getInstance().getRmTypeNameToClassMap().values());
                //extra classes from the adapters package that are not directly referenced.\
                classes.add(XmlResourceDescriptionItem.class);
                removeClasses(classes);
                jaxbContext = JAXBContext.newInstance(classes.toArray(new Class[0]));
            } catch (JAXBException e) {
                throw new RuntimeException(e);//programmer error, tests will fail
            }
        }
    }

    public static synchronized JAXBContext createRMContext() {
        try {
            List<Class<?>> classes = Lists.newArrayList(OpenEhrRmInfoLookup.getInstance().getRmTypeNameToClassMap().values());
            removeClasses(classes);
            return JAXBContext.newInstance(classes.toArray(new Class[0]));
        } catch (JAXBException e) {
            throw new RuntimeException(e);//programmer error, tests will fail
        }
    }

}
