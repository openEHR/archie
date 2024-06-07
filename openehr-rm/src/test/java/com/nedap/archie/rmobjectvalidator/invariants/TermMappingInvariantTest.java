package com.nedap.archie.rmobjectvalidator.invariants;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.TermMapping;
import org.junit.Test;

public class TermMappingInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new TermMapping(new CodePhrase("local::ac3"), '=', new DvCodedText("public health", new CodePhrase("openehr::669"))));
        InvariantTestUtil.assertValid(new TermMapping(new CodePhrase("local::ac3"), '=', null));
    }

    @Test
    public void purposeInvalid() {
        InvariantTestUtil.assertInvariantInvalid(
                new TermMapping(new CodePhrase("local::ac3"), '=', new DvCodedText("public health", new CodePhrase("openehr::331"))),
                "Purpose_valid",
                "/"
                );
    }

    @Test
    public void matchInvalid() {
        InvariantTestUtil.assertInvariantInvalid(
                new TermMapping(new CodePhrase("local::ac3"), 'Q', new DvCodedText("public health", new CodePhrase("openehr::669"))),
                "Match_valid",
                "/"
        );
    }
}
