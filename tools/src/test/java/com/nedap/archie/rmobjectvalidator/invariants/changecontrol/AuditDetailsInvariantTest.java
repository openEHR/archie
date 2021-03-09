package com.nedap.archie.rmobjectvalidator.invariants.changecontrol;

import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.generic.AuditDetails;
import com.nedap.archie.rm.generic.PartySelf;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.time.LocalDateTime;

public class AuditDetailsInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(createValid());
    }

    @Test
    public void systemIdInvalid() {
        AuditDetails auditDetails = createValid();
        auditDetails.setSystemId("");
        InvariantTestUtil.assertInvariantInvalid(auditDetails, "System_id_valid", "/");
    }

    @Test
    public void changeTypeInvalid() {
        AuditDetails auditDetails = createValid();
        auditDetails.setChangeType(new DvCodedText("chaaaaangggeee?", "openehr:98921"));
        InvariantTestUtil.assertInvariantInvalid(auditDetails, "Change_type_valid", "/");
    }

    public AuditDetails createValid() {
        return new AuditDetails("system id", new PartySelf(), new DvDateTime(LocalDateTime.now()),
                new DvCodedText("creation", "openehr::249"), new DvText("description"));
    }
}
