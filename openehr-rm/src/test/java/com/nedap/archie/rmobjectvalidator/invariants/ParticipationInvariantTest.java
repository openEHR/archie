package com.nedap.archie.rmobjectvalidator.invariants;

import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.DvText;
import org.openehr.rm.generic.Participation;
import org.openehr.rm.generic.PartySelf;
import org.junit.Test;

public class ParticipationInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new Participation(new PartySelf(), new DvText("joined"), null, null));
        InvariantTestUtil.assertValid(new Participation(new PartySelf(), new DvCodedText("unknown", "openehr::253"), null, null));
        InvariantTestUtil.assertValid(new Participation(new PartySelf(), new DvCodedText("unknown", "openehr::253"), new DvCodedText("face to face communication", "openehr::216"), null));
    }

    @Test
    public void invalidMode() {
        InvariantTestUtil.assertInvariantInvalid(new Participation(new PartySelf(), new DvText("joined"), new DvCodedText("interplanetary", "openehr::999"), null), "Mode_valid", "/");
    }

    @Test
    public void invalidFunction() {
        InvariantTestUtil.assertInvariantInvalid(new Participation(new PartySelf(), new DvCodedText("something", "openehr::123"),null, null), "Function_valid", "/");
    }
}
