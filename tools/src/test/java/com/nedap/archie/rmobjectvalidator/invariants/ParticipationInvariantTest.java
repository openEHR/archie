package com.nedap.archie.rmobjectvalidator.invariants;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartySelf;
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
