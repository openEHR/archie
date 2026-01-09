package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.support.identification.ArchetypeID;

import java.util.Map;

public class OpenEhrRmObjectProcessor implements RmObjectProcessor {
    @Override
    public void processCreatedObject(Object createdObject, CObject constraint) {
        if (createdObject instanceof Locatable) { //and most often, it will be
            Locatable locatable = (Locatable) createdObject;
            locatable.setArchetypeNodeId(constraint.getNodeId());
            locatable.setNameAsString(constraint.getMeaning());
            if (constraint instanceof CArchetypeRoot) {
                CArchetypeRoot root = (CArchetypeRoot) constraint;
                if (root.getArchetypeRef() != null) {
                    Archetyped details = new Archetyped();
                    details.setArchetypeId(new ArchetypeID(root.getArchetypeRef()));
                    details.setRmVersion(ArchieRMInfoLookup.RM_VERSION);
                    locatable.setArchetypeDetails(details);
                }
            }
        }
    }

    /**
     * Notification that a value at a given path has been updated in the given archetype. Perform tasks here to make
     * sure every other paths are updated as well.
     */
    @Override
    public Map<String, Object> processUpdatedObject(
            Object rootRmObject,
            Archetype archetype,
            String path,
            Object updatedObject
    ) {
        return UpdatedValueHandler.pathHasBeenUpdated(rootRmObject, archetype, path, updatedObject);
    }
}
