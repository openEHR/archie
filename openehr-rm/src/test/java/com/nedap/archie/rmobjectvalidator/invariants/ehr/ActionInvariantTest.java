package com.nedap.archie.rmobjectvalidator.invariants.ehr;

import org.openehr.rm.composition.Action;
import org.openehr.rm.composition.InstructionDetails;
import org.openehr.rm.composition.IsmTransition;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.support.identification.HierObjectId;
import org.openehr.rm.support.identification.LocatableRef;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.time.LocalDateTime;

public class ActionInvariantTest {

    @Test
    public void valid() {
        Action action = createValid();
        InvariantTestUtil.assertValid(action);
    }

    @Test
    public void invalidActivityId() {
        Action action = createValid();
        action.getInstructionDetails().setActivityId("");
        InvariantTestUtil.assertInvariantInvalid(action, "Activity_path_valid", "INSTRUCTION_DETAILS", "/instruction_details");

    }

    private Action createValid() {
        Action action = new Action();
        action.setIsmTransition(new IsmTransition(new DvCodedText("planned", "openehr::526"), null, null, null));
        action.setTime(new DvDateTime(LocalDateTime.now()));
        action.setDescription(new ItemTree());
        action.setInstructionDetails(new InstructionDetails(new LocatableRef(HierObjectId.createRandomUUID(), "openehr", "something", "/"), "activityt id", new ItemTree()));

        InvariantTestUtil.setLocatableBasics(action.getDescription());
        InvariantTestUtil.setLocatableBasics(action.getInstructionDetails().getWfDetails());
        InvariantTestUtil.setEntryBasics(action);
        return action;
    }
}
