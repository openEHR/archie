package com.nedap.archie.rmobjectvalidator.invariants.ehr;

import org.openehr.rm.composition.Instruction;
import org.openehr.rm.datavalues.DvText;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

public class InstructionInvariantTest {

    @Test
    public void valid() {
        InvariantTestUtil.assertValid(createValid());
    }

    private Instruction createValid() {
        Instruction instruction = new Instruction();
        instruction.setNarrative(new DvText("Beam me up, Scotty"));
        InvariantTestUtil.setEntryBasics(instruction);
        return instruction;
    }
    //the only invariant is ignored, but good to test a basic empty instruction anyway
}
