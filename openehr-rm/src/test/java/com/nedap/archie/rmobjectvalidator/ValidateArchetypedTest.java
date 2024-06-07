package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.archetyped.FeederAuditDetails;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.Item;
import org.openehr.rm.datastructures.ItemTree;
import org.openehr.rm.datavalues.quantity.DvProportion;
import org.openehr.rm.support.identification.ArchetypeID;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ValidateArchetypedTest {

    private TestUtil testUtil;
    private InMemoryFullArchetypeRepository repo;
    private RMObjectValidator validator;

    private Archetype elementArchetype;
    private OperationalTemplate elementOpt;
    private Archetype itemTreeArchetype;

    @Before
    public void setup() throws Exception {
        testUtil = new TestUtil(OpenEhrRmInfoLookup.getInstance());

        repo = new InMemoryFullArchetypeRepository();
        validator = new RMObjectValidator(OpenEhrRmInfoLookup.getInstance(), repo);

        elementArchetype = parse("/adl2-tests/rmobjectvalidity/openEHR-EHR-ELEMENT.element_with_required_attributes.v1.0.0.adls");
        elementOpt = createOpt(elementArchetype);

        repo.addArchetype(elementArchetype);
        repo.setOperationalTemplate(elementOpt);

        itemTreeArchetype = parse("/adl2-tests/rmobjectvalidity/openEHR-EHR-ITEM_TREE.cardinality_testing.v1.0.0.adls");
        OperationalTemplate itemTreeOpt = createOpt(itemTreeArchetype);

        repo.addArchetype(itemTreeArchetype);
        repo.setOperationalTemplate(itemTreeOpt);
    }

    @Test
    public void testArchetypedOtherDetails() {
        Element element = (Element) testUtil.constructEmptyRMObject(elementArchetype.getDefinition());
        DvProportion dvProportion = (DvProportion) element.getValue();
        dvProportion.setNumerator(3D);
        dvProportion.setDenominator(4D);
        dvProportion.setType(3L);

        ItemTree itemTree = (ItemTree) testUtil.constructEmptyRMObject(itemTreeArchetype.getDefinition());
        List<Item> items = itemTree.getItems();
        items.clear();

        Archetyped details = new Archetyped();
        details.setArchetypeId(new ArchetypeID(itemTreeArchetype.getArchetypeId().toString()));
        details.setRmVersion(OpenEhrRmInfoLookup.RM_VERSION);
        itemTree.setArchetypeDetails(details);

        FeederAudit feederAudit = new FeederAudit();
        FeederAuditDetails feederAuditDetails = new FeederAuditDetails("archie");
        feederAuditDetails.setOtherDetails(itemTree);
        feederAudit.setOriginatingSystemAudit(feederAuditDetails);
        element.setFeederAudit(feederAudit);

        validator.setRunInvariantChecks(false);
        List<RMObjectValidationMessage> validationMessages = validator.validate(elementOpt, element);
        assertEquals("There should be 1 error", 1, validationMessages.size());
        assertEquals("Attribute does not match cardinality 1..2", validationMessages.get(0).getMessage());
        assertEquals("The path should be correct", "/feeder_audit/originating_system_audit/other_details[id1]/items", validationMessages.get(0).getPath());
        assertEquals(RMObjectValidationMessageType.CARDINALITY_MISMATCH, validationMessages.get(0).getType());
    }

    private OperationalTemplate createOpt(Archetype archetype) {
        return (OperationalTemplate) new Flattener(repo, AllMetaModelsInitialiser.getMetaModels(), FlattenerConfiguration.forOperationalTemplate()).flatten(archetype);
    }

    private Archetype parse(String filename) throws IOException, ADLParseException {
        return TestUtil.parseFailOnErrors(this.getClass(), filename);
    }
}
