package com.nedap.archie.flattener.specexamples;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.util.Map;

import static com.nedap.archie.testutil.ParseValidArchetypeTestUtil.*;
import static org.junit.Assert.*;

public class TerminologyFlattenerExamplesFromSpec {

    private static SimpleArchetypeRepository repository;

    @Before
    public void setup() throws Exception {
        repository = new SimpleArchetypeRepository();
    }

    @Test
    public void internalValueSetRedefinition() throws Exception {
        Archetype parent = parse(this.getClass(), "openEHR-EHR-ELEMENT.internal_value_set_parent.v1.0.0.adls");
        repository.addArchetype(parent);

        Archetype specialized = parse(this.getClass(), "openEHR-EHR-ELEMENT.interval_value_set_specialized.v1.0.0.adls");

        Archetype flat = new Flattener(repository, AllMetaModelsInitialiser.getNativeRms()).flatten(specialized);
        Map<String, ValueSet> valueSets = flat.getTerminology().getValueSets();

        CTerminologyCode code = flat.itemAtPath("/name/defining_code[1]");
        assertEquals(5, code.getTerms().size());

        ValueSet newValueSet = valueSets.get("ac1.1");
        assertNotNull(newValueSet);
        assertNotNull(valueSets.get("ac1"));
    }
}
