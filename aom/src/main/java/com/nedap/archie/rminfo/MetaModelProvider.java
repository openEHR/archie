package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;

/**
 * A provider for meta models, which can be used to retrieve the meta model for a specific reference model based on an
 * archetype or reference model publisher, package and release version.
 */
public interface MetaModelProvider {
    /**
     * Get a meta model based on an archetype.
     *
     * @param archetype the archetype to find the meta model for
     * @return the meta model for the reference model that the archetype is based on
     * @throws ModelNotFoundException when no BMM and no ModelInfoLookup model has been found matching the archetype
     */
    public default MetaModel getMetaModel(Archetype archetype) throws ModelNotFoundException {
        return getMetaModel(archetype, archetype.getRmRelease());
    }

    /**
     * Get a meta model based on an archetype, but override the RM release version from the archetype with the given RM
     * release version.
     *
     * @param archetype the archetype to find the meta model for
     * @param rmRelease the release version of the reference model. This will override the RM release version in the
     *                  archetype.
     * @return the meta model for the reference model that the archetype is based on
     * @throws ModelNotFoundException when no BMM and no ModelInfoLookup model has been found matching the archetype and
     *                                RM version
     */
    public default MetaModel getMetaModel(Archetype archetype, String rmRelease) throws ModelNotFoundException {
        return getMetaModel(
                archetype.getArchetypeId().getRmPublisher(),
                archetype.getArchetypeId().getRmPackage(),
                rmRelease
        );
    }

    /**
     * Get a model based on a reference model publisher, package and release version.
     *
     * @param rmPublisher the publisher of the reference model
     * @param rmPackage   the package of the reference model
     * @param rmRelease   the release version of the reference model
     * @return the meta model
     * @throws ModelNotFoundException when no BMM and no ModelInfoLookup model has been found matching the publisher,
     *                                package and release version
     */
    public abstract MetaModel getMetaModel(String rmPublisher, String rmPackage, String rmRelease) throws ModelNotFoundException;

    /**
     * @deprecated For backwards compatibility only. Use {@link #getMetaModel(Archetype)} instead.
     */
    @Deprecated
    public default MetaModel selectAndGetMetaModel(Archetype archetype) throws ModelNotFoundException {
        return getMetaModel(archetype);
    }

    /**
     * @deprecated For backwards compatibility only. Use {@link #getMetaModel(Archetype, String)} instead.
     */
    @Deprecated
    public default MetaModel selectAndGetMetaModel(Archetype archetype, String rmVersion) throws ModelNotFoundException {
        return getMetaModel(archetype, rmVersion);
    }

    /**
     * @deprecated For backwards compatibility only. Use {@link #getMetaModel(String, String, String)} instead.
     */
    @Deprecated
    public default MetaModel selectAndGetMetaModel(String rmPublisher, String rmPackage, String rmRelease) throws ModelNotFoundException {
        return getMetaModel(rmPublisher, rmPackage, rmRelease);
    }
}
