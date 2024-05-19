package com.nedap.archie.rmobjectvalidator.invariants.datastructures;

import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.DvText;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElementInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(createValid());
        Element nullFlavoured = createValid();
        nullFlavoured.setValue(null);
        nullFlavoured.setNullFlavour(new DvCodedText("unknown", "openehr::253"));
        InvariantTestUtil.assertValid(nullFlavoured);

        Element nullReasoned = createValid();
        nullReasoned.setValue(null);
        nullReasoned.setNullFlavour(new DvCodedText("unknown", "openehr::253"));
        nullReasoned.setNullReason(new DvCodedText("unknown", "local::at5"));
        InvariantTestUtil.assertValid(nullReasoned);
    }

    @Test
    public void nullFlavourIndicated() {
        Element element = createValid();
        element.setNullFlavour(new DvCodedText("unknown", "openehr::253"));
        InvariantTestUtil.assertInvariantInvalid(element, "Inv_null_flavour_indicated", "/");
    }

    @Test
    public void nullFlavourValid() {
        Element two = createValid();
        two.setValue(null);
        two.setNullFlavour(new DvCodedText("unknown", "openehr::666"));
        InvariantTestUtil.assertInvariantInvalid(two, "Inv_null_flavour_valid", "/");
    }

    @Test
    public void nullReasonInvalid() {
        Element element = createValid();
        element.setNullReason(new DvCodedText("unknown", "local::at5"));
        InvariantTestUtil.assertInvariantInvalid(element, "Inv_null_reason_valid", "/");
    }

    public Element createValid() {
        Element element = new Element();
        element.setValue(new DvText("something"));
        element.setArchetypeNodeId("id25");
        element.setNameAsString("Hi, I'm an element!");
        return element;
    }
}
