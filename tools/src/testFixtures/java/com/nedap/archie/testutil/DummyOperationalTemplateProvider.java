package com.nedap.archie.testutil;

import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.flattener.OperationalTemplateProvider;

/**
 * Generates dummy operational templates when one is requested with the given concept id.
 */
public class DummyOperationalTemplateProvider implements OperationalTemplateProvider {
    private final String conceptId;

    public DummyOperationalTemplateProvider(String conceptId) {
        this.conceptId = conceptId;
    }

    @Override
    public OperationalTemplate getOperationalTemplate(String archetypeId) {
        ArchetypeHRID archetypeHRID = new ArchetypeHRID(archetypeId);
        if(conceptId.equals(archetypeHRID.getConceptId())) {
            return generateDummyOperationalTemplate(archetypeHRID);
        }
        return null;
    }

    private static OperationalTemplate generateDummyOperationalTemplate(ArchetypeHRID archetypeHRID) {
        CComplexObject cComplexObject = new CComplexObject();
        cComplexObject.setNodeId("id1");
        cComplexObject.setRmTypeName(archetypeHRID.getRmClass());

        OperationalTemplate result = new OperationalTemplate();
        result.setArchetypeId(archetypeHRID);
        result.setDefinition(cComplexObject);
        return result;
    }
}
