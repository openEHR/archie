package com.nedap.archie.rmobjectvalidator.invariants;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.generic.PartyRelated;
import org.junit.Test;

public class PartyRelatedInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new PartyRelated(null, "brother", null, new DvCodedText("brother", "openehr::23")));
    }
    @Test
    public void relationshipInvalid() {
        InvariantTestUtil.assertInvariantInvalid(new PartyRelated(null, "brother", null, new DvCodedText("brother", "openehr::555")), "Relationship_valid", "/");
    }
}
