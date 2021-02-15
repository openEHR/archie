package com.nedap.archie.rmobjectvalidator.invariants.datastructures;

import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElementInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(createValid());
    }

    @Test
    public void nullFlavourInvalid() {
        Element element = createValid();
        element.setNullFlavour(new DvCodedText("unknown", "openehr::253"));
        InvariantTestUtil.assertInvariantInvalid(element, "Inv_null_flavour_indicated", "/");
    }

    //TODO: null-reaosn_invalid

    public Element createValid() {
        Element element = new Element();
        element.setValue(new DvText("something"));
        element.setArchetypeNodeId("id25");
        element.setNameAsString("Hi, I'm an element!");
        return element;
    }
}
