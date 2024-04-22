package com.nedap.archie.rmobjectvalidator.invariants.ehr;

import org.openehr.rm.composition.IsmTransition;
import org.openehr.rm.datavalues.DvCodedText;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class IsmTransitionInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new IsmTransition(new DvCodedText("planned", "openehr::526"), null, null, null));
        InvariantTestUtil.assertValid(new IsmTransition(new DvCodedText("planned", "openehr::526"), new DvCodedText("schedule", "openehr::539"), null, null));
    }

    @Test
    public void invalidState() {
        InvariantTestUtil.assertInvariantInvalid(new IsmTransition(new DvCodedText("never done", "openehr::999"), null, null, null), "Current_state_valid", "/");
    }

    @Test
    public void invalidTransition() {
        InvariantTestUtil.assertInvariantInvalid(new IsmTransition(new DvCodedText("planned", "openehr::526"), new DvCodedText("schedule", "openehr::9"), null, null), "Transition_valid", "/");
    }
}
