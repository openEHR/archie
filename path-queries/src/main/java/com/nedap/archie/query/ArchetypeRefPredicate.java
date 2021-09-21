package com.nedap.archie.query;

import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.Objects;
import java.util.function.Predicate;

class ArchetypeRefPredicate implements Predicate<Object> {

    private final ModelInfoLookup lookup;
    private final ArchetypeHRID archetypeRef;

    public ArchetypeRefPredicate(ModelInfoLookup lookup, String archetypeRef) {
        this.lookup = lookup;
        this.archetypeRef = new ArchetypeHRID(archetypeRef);
    }

    @Override
    public boolean test(Object input) {
        if(input == null) {
            return false;
        }
        String nodeIdFromRMObject = lookup.getArchetypeNodeIdFromRMObject(input);
        String archetypeIdFromRMObject = lookup.getArchetypeIdFromArchetypedRmObject(input);

        return versionMatches(archetypeRef, archetypeIdFromRMObject) || (nodeIdFromRMObject != null && AOMUtils.isArchetypeRef(nodeIdFromRMObject) && versionMatches(archetypeRef, nodeIdFromRMObject));

    }

    private static boolean versionMatches(ArchetypeHRID ref, String id) {
        if(id == null) {
            return false;
        }
        ArchetypeHRID toTest = new ArchetypeHRID(id);
        return  toTest.getIdUpToConcept().equals(ref.getIdUpToConcept()) &&
                (ref.getMajorVersion() == null || toTest.getMajorVersion().equals(ref.getMajorVersion())) &&
                (ref.getMinorVersion() == null || toTest.getMinorVersion().equals(ref.getMinorVersion())) &&
                (ref.getPatchVersion() == null || toTest.getPatchVersion().equals(ref.getPatchVersion()));
    }
}

