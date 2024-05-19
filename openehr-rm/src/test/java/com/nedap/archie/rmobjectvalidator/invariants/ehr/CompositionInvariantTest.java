package com.nedap.archie.rmobjectvalidator.invariants.ehr;

import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.Observation;
import org.openehr.rm.datastructures.History;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datastructures.PointEvent;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.quantity.datetime.DvDateTime;
import org.openehr.rm.generic.PartySelf;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.time.LocalDateTime;

public class CompositionInvariantTest {

    @Test
    public void validComposition() {
        InvariantTestUtil.assertValid(createValidComposition());
    }

    @Test
    public void invalidLanguage() {
        Composition composition = createValidComposition();
        composition.setLanguage("ISO_639-1::gibberish");
        InvariantTestUtil.assertInvariantInvalid(composition, "Language_valid", "/");
    }

    @Test
    public void invalidTerritory() {
        Composition composition = createValidComposition();
        composition.setTerritory("ISO_3166-1::atlantis");
        InvariantTestUtil.assertInvariantInvalid(composition, "Territory_valid", "/");
    }

    @Test
    public void invalidCategory() {
        Composition composition = createValidComposition();
        composition.setCategory("Uncategorized", "openehr::1");
        InvariantTestUtil.assertInvariantInvalid(composition, "Category_validity", "/");
    }

    @Test
    public void noArchetypeRoot() {
        Composition composition = createValidComposition();
        composition.setArchetypeDetails(null);
        InvariantTestUtil.assertInvariantInvalid(composition, "Is_archetype_root", "/");
    }

    @Test
    public void rmVersionInvalid() {
        Composition composition = createValidComposition();
        composition.getArchetypeDetails().setRmVersion("");
        InvariantTestUtil.assertInvariantInvalid(composition, "Rm_version_valid", "ARCHETYPED", "/archetype_details");
    }

    private Composition createValidComposition() {
        Composition composition = new Composition();
        InvariantTestUtil.setArchetypeRootBasics(composition);
        composition.setCategory(new DvCodedText("persistent", "openehr::431"));
        composition.setComposer(new PartySelf());//patient composed data :)
        composition.setLanguage("ISO_639-1::en");
        composition.setTerritory("ISO_3166-1::NL");

        return composition;
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

    @Test
    public void compositionWithInvalidData() {
        Composition composition = new Composition();
        InvariantTestUtil.setArchetypeRootBasics(composition);
        composition.setCategory(new DvCodedText("persistent", "openehr::431"));
        composition.setComposer(new PartySelf());//patient composed data :)
        composition.setLanguage("ISO_639-1::en");
        composition.setTerritory("ISO_3166-1::NL");
        Observation observation = createValidObservation();
        observation.getData().setEvents(null);
        observation.getData().setSummary(null);
        composition.addContent(observation);
        InvariantTestUtil.assertInvariantInvalid(composition, "Events_valid", "HISTORY", "/content[id1, 1]/data[id1]");

    }
}
