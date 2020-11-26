package com.nedap.archie.rmobjectvalidatortest;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.support.identification.TerminologyId;
import com.nedap.archie.rminfo.Invariant;
import org.junit.Test;

public class CodePhraseInvariantTest {
    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new CodePhrase("terminologyId::value"));
    }

    @Test
    public void codeSringInvvalid() {
        InvariantTestUtil.assertInvariantInvalid(new CodePhrase(new TerminologyId("terminologyId"), ""), "Code_string_valid", "/");
    }
}
