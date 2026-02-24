package com.nedap.archie.rmobjectvalidator.invariants;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.TermMapping;
import org.junit.jupiter.api.Test;

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
