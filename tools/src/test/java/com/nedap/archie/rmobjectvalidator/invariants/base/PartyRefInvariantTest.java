package com.nedap.archie.rmobjectvalidator.invariants.base;

import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.PartyRef;
import com.nedap.archie.rm.support.identification.UIDBasedId;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.util.UUID;

public class PartyRefInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new PartyRef(new HierObjectId(UUID.randomUUID().toString()), "local", "PERSON"));
    }

    @Test
    public void invalidType() {
        InvariantTestUtil.assertInvariantInvalid(new PartyRef(new HierObjectId(UUID.randomUUID().toString()), "unknown", "SOMEONE"), "Type_validity", "/");
    }

    @Test
    public void invalidNamespace() {
        InvariantTestUtil.assertInvariantInvalid(new PartyRef(new HierObjectId(UUID.randomUUID().toString()), "1badhjklcd", "AGENT"), "Namespace_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new PartyRef(new HierObjectId(UUID.randomUUID().toString()), "A*", "AGENT"), "Namespace_valid", "/");
    }
}
