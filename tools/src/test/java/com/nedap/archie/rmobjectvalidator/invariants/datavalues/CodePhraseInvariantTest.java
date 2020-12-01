package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.support.identification.TerminologyId;
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
