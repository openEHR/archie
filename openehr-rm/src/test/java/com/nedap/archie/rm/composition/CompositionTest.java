package com.nedap.archie.rm.composition;

import com.nedap.archie.xml.JAXBUtil;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import static org.junit.Assert.assertEquals;

public class CompositionTest {

    @Test
    public void testEqual() throws JAXBException {
        Unmarshaller unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
        Composition composition1 = (Composition) unmarshaller.unmarshal(getClass().getClassLoader().getResourceAsStream("test_data/test_all_types.fixed.v1.xml"));
        Composition composition2 = (Composition) unmarshaller.unmarshal(getClass().getClassLoader().getResourceAsStream("test_data/test_all_types.fixed.v1.xml"));
        assertEquals(composition1,composition2);
    }
}