package com.nedap.archie.rmobjectvalidator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.primitives.ConstraintStatus;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.flattener.specexamples.FlattenerTestUtil;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rules.evaluation.DummyRulesPrimitiveObjectParent;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.net.URI;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 23/02/16.
 */
public class TerminologyCodeConstraintsTest {

    private Archetype archetype;
    private final ValidationConfiguration validationConfiguration = new ValidationConfiguration.Builder().build();
    private final PrimitiveObjectConstraintHelper primitiveObjectConstraintHelper = new PrimitiveObjectConstraintHelper(validationConfiguration);
    private final ValidationHelper validationHelper = new ValidationHelper(ArchieRMInfoLookup.getInstance(), validationConfiguration);

    @Before
    public void setupArchetype() {
        archetype = new AuthoredArchetype();

        ArchetypeTerminology terminology = new ArchetypeTerminology();
        terminology.setTermBindings(new HashMap<String, Map<String, URI>>() {{
            put("openehr", new HashMap<String, URI>() {{
                put("at9000", URI.create("http://openehr.org/id/532"));
            }});

            put("IANA_media-types", new HashMap<String, URI>() {{
                put("at9001", URI.create("https://www.w3.org/ns/iana/media-types/text/plain#Resource"));
            }});
        }});
        archetype.setTerminology(terminology);

        Map<String, ValueSet> valueSets = new LinkedHashMap<>();
        terminology.setValueSets(valueSets);

        ValueSet ac12 = new ValueSet();
        ac12.setId("ac12");
        ac12.setMembers(Sets.newHashSet("at23", "at24"));
        ValueSet ac13 = new ValueSet();
        ac13.setId("ac13");
        ac13.setMembers(Sets.newHashSet("at24"));
        valueSets.put("ac12", ac12 );
        valueSets.put("ac13", ac13 );
    }

    @Test
    public void noConstraint() {
        CTerminologyCode code = new CTerminologyCode();
        assertTrue(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[ac12::at23]")));
    }

    @Test
    public void terminologyIdConstraint() {
        CTerminologyCode code = new CTerminologyCode();
        code.setParent(new DummyRulesPrimitiveObjectParent(archetype));
        code.addConstraint("ac12");
        assertTrue(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[ac12::at23]")));
        assertTrue(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[ac12::at24]")));
        assertFalse(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[ac12::at25]")));
        assertTrue(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[local::at23]")));
    }

    @Test
    public void externalTerminology() {
        CTerminologyCode code = new CTerminologyCode();
        code.setParent(new DummyRulesPrimitiveObjectParent(archetype));
        code.addConstraint("ac12");

        assertTrue(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[snomedct::72489423]")));
        assertTrue(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[anything::atall]")));

        PrimitiveObjectConstraintHelper failingHelper = new PrimitiveObjectConstraintHelper(new ValidationConfiguration.Builder().failOnUnknownTerminologyId(true).build());
        assertFalse(failingHelper.isValidValue(code, TerminologyCode.createFromString("[snomedct::72489423]")));
        assertFalse(failingHelper.isValidValue(code, TerminologyCode.createFromString("[anything::atall]")));
    }

    @Test
    public void openEHRTerminology() {
        CTerminologyCode code = new CTerminologyCode();
        code.setParent(new DummyRulesPrimitiveObjectParent(archetype));
        code.addConstraint("at9000");
        code.setConstraintStatus(ConstraintStatus.REQUIRED);

        DvCodedText text = new DvCodedText();
        text.setValue("does not matter for this validation");
        text.setDefiningCode(new CodePhrase("[openehr::532]"));
        assertTrue(validationHelper.isValidValue(code, text));
        text.setDefiningCode(new CodePhrase("[openehr::999]"));
        assertFalse(validationHelper.isValidValue(code, text));
        text.setDefiningCode(new CodePhrase());
        assertFalse(validationHelper.isValidValue(code, text));
        text.setDefiningCode(null);
        assertFalse(validationHelper.isValidValue(code, text));
        assertFalse(validationHelper.isValidValue(code, null));
    }

    @Test
    public void IANATerminology() {
        CTerminologyCode code = new CTerminologyCode();
        code.setParent(new DummyRulesPrimitiveObjectParent(archetype));
        code.addConstraint("at9001");
        code.setConstraintStatus(ConstraintStatus.REQUIRED);

        DvCodedText text = new DvCodedText();
        text.setValue("does not matter for this validation");
        text.setDefiningCode(new CodePhrase("[IANA_media-types::text/plain]"));
        assertTrue(validationHelper.isValidValue(code, text));
        text.setDefiningCode(new CodePhrase("[IANA_media-types::abc/def]"));
        assertFalse(validationHelper.isValidValue(code, text));
        text.setDefiningCode(new CodePhrase());
        assertFalse(validationHelper.isValidValue(code, text));
        text.setDefiningCode(null);
        assertFalse(validationHelper.isValidValue(code, text));
        assertFalse(validationHelper.isValidValue(code, null));
    }

    @Test
    public void terminologyCodeConstraint() {
        CTerminologyCode code = new CTerminologyCode();
        code.setParent(new DummyRulesPrimitiveObjectParent(archetype));
        code.addConstraint("at23");
        assertTrue(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[ac12::at23]")));
        assertTrue(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[ac13::at23]")));
        assertFalse(primitiveObjectConstraintHelper.isValidValue(code, TerminologyCode.createFromString("[ac13::at24]")));
    }

    @Test
    public void dvCodedText() {
        //DV_CODED_TEXT can be constrained by a C_TERMINOLOGY_CONSTRAINT, according to lots of DV_ORDINAL usage in the CKM
        CTerminologyCode code = new CTerminologyCode();
        code.setParent(new DummyRulesPrimitiveObjectParent(archetype));
        code.addConstraint("at23");
        termCodeAssertions(code);
    }

    @Test
    public void requiredBindingStrength() {
        //DV_CODED_TEXT can be constrained by a C_TERMINOLOGY_CONSTRAINT, according to lots of DV_ORDINAL usage in the CKM
        CTerminologyCode code = new CTerminologyCode();
        code.setParent(new DummyRulesPrimitiveObjectParent(archetype));
        code.addConstraint("at23");
        code.setConstraintStatus(ConstraintStatus.REQUIRED);
        termCodeAssertions(code);
    }

    @Test
    public void otherBindingStrength() {
        //DV_CODED_TEXT can be constrained by a C_TERMINOLOGY_CONSTRAINT, according to lots of DV_ORDINAL usage in the CKM
        CTerminologyCode code = new CTerminologyCode();
        code.setParent(new DummyRulesPrimitiveObjectParent(archetype));
        code.addConstraint("at23");
        Set<ConstraintStatus> nonRequiredBindings = EnumSet.of(ConstraintStatus.EXTENSIBLE, ConstraintStatus.EXAMPLE, ConstraintStatus.PREFERRED);
        for(ConstraintStatus status:nonRequiredBindings) {
            code.setConstraintStatus(status);
            DvCodedText text = new DvCodedText();
            text.setValue("does not matter for this validation");
            text.setDefiningCode(new CodePhrase("[local::at23]"));
            assertTrue(validationHelper.isValidValue(code, text));
            text.setDefiningCode(new CodePhrase("[local::at24]"));
            assertTrue(validationHelper.isValidValue(code, text));
            text.setDefiningCode(new CodePhrase("[local:at0.3.25]"));
            assertTrue(validationHelper.isValidValue(code, text));
            text.setDefiningCode(new CodePhrase("[snomed:123657]"));
            assertTrue(validationHelper.isValidValue(code, text));
        }
    }

    private void termCodeAssertions(CTerminologyCode code) {
        DvCodedText text = new DvCodedText();
        text.setValue("does not matter for this validation");
        text.setDefiningCode(new CodePhrase("[local::at23]"));
        assertTrue(validationHelper.isValidValue(code, text));
        text.setDefiningCode(new CodePhrase("[local::at24]"));
        assertFalse(validationHelper.isValidValue(code, text));
        text.setDefiningCode(new CodePhrase());
        assertFalse(validationHelper.isValidValue(code, text));
        text.setDefiningCode(null);
        assertFalse(validationHelper.isValidValue(code, text));
        assertFalse(validationHelper.isValidValue(code, null));
    }

    @Test
    public void validationInOptTest() throws Exception {
        //getting the term for a use archetype is a bit of a tricky situation - it's not for the 'id1' code,
        //it's for the code in the parent. So do some specific test here
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(FlattenerTestUtil.parse("/com/nedap/archie/aom/openEHR-EHR-COMPOSITION.parent.v1.0.0.adls"));
        repository.addArchetype(FlattenerTestUtil.parse("/com/nedap/archie/aom/openEHR-EHR-GENERIC_ENTRY.included.v1.0.0.adls"));

        //create operational template
        Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
        OperationalTemplate opt = (OperationalTemplate) flattener.flatten(repository.getArchetype("openEHR-EHR-COMPOSITION.parent.v1.0.0"));
        CTerminologyCode code = opt.itemAtPath("/content/data/items/value/defining_code[1]");
        assertEquals(Lists.newArrayList("at4", "at5", "at6"), code.getValueSetExpanded());
    }
}
