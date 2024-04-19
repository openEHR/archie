package com.nedap.archie.rmobjectvalidator.invariants.changecontrol;

import org.openehr.rm.changecontrol.OriginalVersion;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.generic.AuditDetails;
import org.openehr.rm.generic.PartySelf;
import org.openehr.rm.support.identification.ObjectVersionId;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.time.LocalDateTime;

public class OriginalVersionInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(createValid());
    }

    @Test
    public void lifecycleInvalid() {
        OriginalVersion version = createValid();
        version.setLifecycleState(new DvCodedText("something", "openehr::123"));
        InvariantTestUtil.assertInvariantInvalid(version, "Lifecycle_state_valid", "/");
    }

    //rest of the invariants are currently ignored.


    public OriginalVersion createValid() {
        OriginalVersion value = new OriginalVersion();
        value.setLifecycleState(new DvCodedText("complete", "openehr::532"));
        value.setUid(new ObjectVersionId("SOME_UID"));//should be an UID, but is not checked
        value.setContribution(InvariantTestUtil.createExampleRef("COMPOSITION"));
        value.setCommitAudit(new AuditDetails("system id", new PartySelf(), new DvDateTime(LocalDateTime.now()),
                new DvCodedText("creation", "openehr::249"), null));
        return value;
    }
}
