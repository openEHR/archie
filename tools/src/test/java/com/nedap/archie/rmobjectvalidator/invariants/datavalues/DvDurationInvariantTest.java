package com.nedap.archie.rmobjectvalidator.invariants.datavalues;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.datetime.DvDuration;
import org.openehr.rm.support.identification.TerminologyId;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.time.Period;

public class DvDurationInvariantTest {

    @Test
    public void validateDvDuration(){

        DvDuration dvDuration = new DvDuration(Period.of(10,5,5));
        dvDuration.setNormalStatus(new CodePhrase(new TerminologyId("openehr_normal_statuses"),"N"));

        dvDuration.setNormalRange(new DvInterval<>());
        dvDuration.getNormalRange().setLower(new DvDuration(Period.of(10,5,5)));
        dvDuration.getNormalRange().setUpper(new DvDuration(Period.of(10,6,5)));

        InvariantTestUtil.assertValid(dvDuration);
    }
}
