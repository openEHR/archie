package com.nedap.archie.rmobjectvalidator.invariants.ehr;

import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.datastructures.History;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.rm.datastructures.PointEvent;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * Also includes History invariants
 */
public class ObservationInvariantTest {

    @Test
    public void validObservation() {
        InvariantTestUtil.assertValid(createValidObservation());
        Observation withSummary = createValidObservation();
        ItemStructure data = withSummary.getData().getEvents().get(0).getData();
        withSummary.getData().setEvents(null);
        withSummary.getData().setSummary(data);
        InvariantTestUtil.assertValid(withSummary);
    }

    @Test
    public void invalidHistoryEventsOrSummary() {
        Observation withSummary = createValidObservation();

        withSummary.getData().setEvents(null);
        withSummary.getData().setSummary(null);
        InvariantTestUtil.assertInvariantInvalid(withSummary, "Events_valid", "HISTORY", "/data[id1]");
    }

    private Observation createValidObservation() {
        Observation observation = new Observation();
        InvariantTestUtil.setEntryBasics(observation);

        History history = new History();
        InvariantTestUtil.setLocatableBasics(history);
        history.setOrigin(new DvDateTime(LocalDateTime.now()));
        PointEvent event = new PointEvent();
        event.setTime(history.getOrigin());
        InvariantTestUtil.setLocatableBasics(event);
        history.addEvent(event);
        ItemTree itemTree = new ItemTree();
        InvariantTestUtil.setLocatableBasics(itemTree);
        event.setData(itemTree);
        observation.setData(history);
        return observation;
    }
}
