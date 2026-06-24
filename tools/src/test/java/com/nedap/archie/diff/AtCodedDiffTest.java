package com.nedap.archie.diff;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModelProvider;
import com.nedap.archie.rminfo.SimpleMetaModelProvider;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import com.nedap.archie.testutil.TestUtil;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gap #1 probe (end-to-end): differential diff of an at-coded ADL 2.4 archetype.
 *
 * <p>Flattens the specialized child against its parent, runs the {@link Differentiator}, and asserts the result
 * serializes back to the original differential source (the standard diff round-trip used by the rest of the diff
 * tests). The differ relies on {@code AOMUtils.codeExistsAtLevel} / {@code getSpecialisationStatusFromCode}, which
 * misclassify the zero-padded at-coded root ({@code at0000}). If this test fails, at-coded diff support is
 * actually broken and the {@code codeExistsAtLevel} fix is necessary; if it passes, the analysis' "Medium" concern
 * for gap #1 can be downgraded.
 *
 * <p>Parsing is done directly here (tolerating grammar ambiguity warnings) rather than via {@code DiffTestUtil},
 * whose shared parse helper rejects the FULL AMBIGUITY warnings these CKM-derived at-coded fixtures emit.
 */
public class AtCodedDiffTest {

    private static final String LOCATION = "/com/nedap/archie/adl14/";

    private final MetaModelProvider metaModelProvider =
            new SimpleMetaModelProvider(BuiltinReferenceModels.getAvailableModelInfoLookups(), null);

    @Test
    public void atCodedSpecializationRoundTrips() throws Exception {
        Archetype parent = parse("openEHR-EHR-CLUSTER.exam_adl2_at.v2.1.3.adls");
        Archetype child = parse("openEHR-EHR-CLUSTER.exam-abdomen_adl2_at.v0.0.1-alpha.adls");

        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parent);

        Archetype flattened = new Flattener(repository, metaModelProvider).flatten(child);
        assertEquals(child.getParentArchetypeId(), flattened.getParentArchetypeId());

        Archetype diffed = new Differentiator(BuiltinReferenceModels.getMetaModelProvider()).differentiate(flattened, parent);
        child.setGenerated(true); // the diff tool sets this; align before comparing

        assertEquals(ADLArchetypeSerializer.serialize(child), ADLArchetypeSerializer.serialize(diffed));
        TestUtil.assertCObjectEquals(child.getDefinition(), diffed.getDefinition());
    }

    private Archetype parse(String fileName) throws Exception {
        ADLParser parser = new ADLParser(BuiltinReferenceModels.getMetaModelProvider());
        try (InputStream stream = AtCodedDiffTest.class.getResourceAsStream(LOCATION + fileName)) {
            Archetype archetype = parser.parse(stream);
            assertTrue(parser.getErrors().hasNoErrors(), parser.getErrors().toString());
            return archetype;
        }
    }
}
