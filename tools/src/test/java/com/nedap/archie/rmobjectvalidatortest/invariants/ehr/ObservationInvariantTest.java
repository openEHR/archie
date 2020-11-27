package com.nedap.archie.rmobjectvalidatortest.invariants.ehr;

import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.composition.Composition;
import com.nedap.archie.rm.composition.ContentItem;
import com.nedap.archie.rm.composition.Observation;
import com.nedap.archie.rm.datastructures.Event;
import com.nedap.archie.rm.datastructures.History;
import com.nedap.archie.rm.datastructures.ItemStructure;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.rm.datastructures.PointEvent;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import com.nedap.archie.rm.generic.PartySelf;
import com.nedap.archie.rm.support.identification.ArchetypeID;
import com.nedap.archie.rmobjectvalidatortest.InvariantTestUtil;
import org.junit.Test;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
