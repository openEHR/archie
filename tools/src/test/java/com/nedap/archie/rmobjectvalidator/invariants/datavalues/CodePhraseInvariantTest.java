package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.support.identification.TerminologyId;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class CodePhraseInvariantTest {
    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new CodePhrase("terminologyId::value"));
    }

    @Test
    public void codeStringInvalid() {
        InvariantTestUtil.assertInvariantInvalid(new CodePhrase(new TerminologyId("terminologyId"), ""), "Code_string_valid", "/");
    }
}
