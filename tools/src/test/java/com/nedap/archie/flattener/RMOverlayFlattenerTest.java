package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.rmoverlay.VisibilityType;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import static org.junit.Assert.*;

public class RMOverlayFlattenerTest {

    @Test
    public void flattenParentChild() throws Exception{
        Archetype parent = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.to_flatten_parent_with_overlay.v1.0.0.adls");
        Archetype child = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.to_flatten_child_with_overlay.v1.0.0.adls");
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parent);
        repository.addArchetype(child);
        repository.compile(new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels()));

        repository.getAllValidationResults().forEach(v -> assertTrue(v.getErrors().toString(), !v.hasWarningsOrErrors()));
        Archetype flattenedChild = repository.getFlattenedArchetype("openEHR-EHR-OBSERVATION.to_flatten_child_with_overlay.v1.0.0");
        assertEquals(VisibilityType.HIDE, parent.getRmOverlay().getRmVisibility().get("/subject").getVisibility());
        assertNull(parent.getRmOverlay().getRmVisibility().get("/state"));
        assertEquals(VisibilityType.HIDE, flattenedChild.getRmOverlay().getRmVisibility().get("/subject").getVisibility());
        assertEquals("at12", flattenedChild.getRmOverlay().getRmVisibility().get("/subject").getAlias().getCodeString());
        assertEquals("local", flattenedChild.getRmOverlay().getRmVisibility().get("/subject").getAlias().getTerminologyIdString());
        assertEquals(VisibilityType.HIDE, flattenedChild.getRmOverlay().getRmVisibility().get("/state").getVisibility());

    }

    @Test
    public void flattenIncludedArchetype() throws Exception {
        Archetype parent = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.to_flatten_parent_with_overlay.v1.0.0.adls");
        Archetype composition = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/flattener/openEHR-COMPOSITION.including_overlay.v1.0.0.adls");
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parent);
        repository.addArchetype(composition);
        repository.compile(new ArchetypeValidator(AllMetaModelsInitialiser.getMetaModels()));

        repository.getAllValidationResults().forEach(v -> assertTrue(v.getErrors().toString(), !v.hasWarningsOrErrors()));
        OperationalTemplate opt = (OperationalTemplate) new Flattener(repository, AllMetaModelsInitialiser.getMetaModels(), FlattenerConfiguration.forOperationalTemplate()).flatten(composition);
        assertEquals(VisibilityType.HIDE, opt.getRmOverlay().getRmVisibility().get("/content[id2]/subject").getVisibility());
        assertEquals("at12", opt.getRmOverlay().getRmVisibility().get("/content[id2]/subject").getAlias().getCodeString());
    }
}
