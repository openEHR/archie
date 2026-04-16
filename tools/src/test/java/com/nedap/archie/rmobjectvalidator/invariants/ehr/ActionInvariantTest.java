package com.nedap.archie.rmobjectvalidator.invariants.ehr;

import com.nedap.archie.rm.composition.Action;
import com.nedap.archie.rm.composition.InstructionDetails;
import com.nedap.archie.rm.composition.IsmTransition;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.support.identification.HierObjectId;
import com.nedap.archie.rm.support.identification.LocatableRef;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.jupiter.api.Test;

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
