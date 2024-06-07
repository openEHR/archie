package com.nedap.archie.rmobjectvalidator.invariants;

import org.openehr.rm.composition.EventContext;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.junit.Test;

import java.time.LocalDateTime;

public class EventContextInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(new EventContext(new DvDateTime(LocalDateTime.now()), new DvCodedText("home", "openehr::225")));
        EventContext withLocation = new EventContext(new DvDateTime(LocalDateTime.now()), new DvCodedText("home", "openehr::225"));
        withLocation.setLocation("somewhere");
        InvariantTestUtil.assertValid(withLocation);
    }

    @Test
    public void invalidSetting() {
        InvariantTestUtil.assertInvariantInvalid(new EventContext(new DvDateTime(LocalDateTime.now()), new DvCodedText("home", "openehr::325")), "Setting_valid", "/");
        InvariantTestUtil.assertInvariantInvalid(new EventContext(new DvDateTime(LocalDateTime.now()), new DvCodedText("home", "invalid_terminology_id::225")), "Setting_valid", "/");
    }

    @Test
    public void invalidLocation() {
        EventContext eventContext = new EventContext(new DvDateTime(LocalDateTime.now()), new DvCodedText("home", "openehr::225"));
        eventContext.setLocation("");
        InvariantTestUtil.assertInvariantInvalid(eventContext, "Location_validity", "/");
    }

}
