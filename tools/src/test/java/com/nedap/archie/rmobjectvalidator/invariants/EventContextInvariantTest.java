package com.nedap.archie.rmobjectvalidator.invariants;

import com.nedap.archie.rm.composition.EventContext;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmutil.InvariantUtil;
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
