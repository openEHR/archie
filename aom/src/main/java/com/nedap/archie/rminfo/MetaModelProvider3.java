package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.TemplateOverlay;

/** Provides model metadata and Jackson 3 mappers. */
public interface MetaModelProvider3 {
    /** Uses the archetype RM release, or the owning template release for overlays. */
    public default MetaModel3 getMetaModel(Archetype archetype) throws ModelNotFoundException {
        if (archetype instanceof TemplateOverlay) return getMetaModel(archetype, ((TemplateOverlay) archetype).getOwningTemplate().getRmRelease());
        return getMetaModel(archetype, ((AuthoredArchetype) archetype).getRmRelease());
    }

    /** Uses the supplied RM release. */
    public default MetaModel3 getMetaModel(Archetype archetype, String rmRelease) throws ModelNotFoundException {
        return getMetaModel(
                archetype.getArchetypeId().getRmPublisher(),
                archetype.getArchetypeId().getRmPackage(),
                rmRelease
        );
    }

    /** Finds a model by publisher, package, and release. */
    public abstract MetaModel3 getMetaModel(String rmPublisher, String rmPackage, String rmRelease) throws ModelNotFoundException;

}
