package com.nedap.archie.rmobjectvalidator.invariants.base;

import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.PartyRef;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class PartyRefInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new PartyRef(HierObjectId.createRandomUUID(), "local", "PERSON"));
    }

    @Test
    public void invalidType() {
        InvariantTestUtil.assertInvariantInvalid(new PartyRef(HierObjectId.createRandomUUID(), "unknown", "SOMEONE"), "Type_validity", "/");
    }

    @Test
    public void invalidNamespace() {
        InvariantTestUtil.assertInvariantInvalid(new PartyRef(HierObjectId.createRandomUUID(), "1badhjklcd", "AGENT"), "Namespace_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new PartyRef(HierObjectId.createRandomUUID(), "A*", "AGENT"), "Namespace_valid", "/");
    }
}
