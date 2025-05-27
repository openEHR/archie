package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A MetaModelProvider that allows to override the RM release version for a specific RM publisher and package.
 * <p>
 * When the RM release version is overridden for a specific RM publisher and package, the overridden version will be
 * used by the {@link #getMetaModel(Archetype)} method to retrieve the meta model.
 */
public class OverridingMetaModelProvider implements MetaModelProvider {
    private final MetaModelProvider delegate;

    /**
     * Allows to set a specific RM release version for a specific RM model, so that one is used instead of the one in the archetype
     */
    private final Map<String, String> overriddenMetaModelVersions = new ConcurrentHashMap<>();

    /**
     * Create an OverridingMetaModelProvider that delegates to the given MetaModelProvider.
     *
     * @param delegate the MetaModelProvider to delegate to
     */
    public OverridingMetaModelProvider(MetaModelProvider delegate) {
        this.delegate = delegate;
    }

    /**
     * Indicate that the model version for the given package by the given publisher should be fixed
     * to a specific version. Useful for validating archetypes against new RM versions, for example OpenEHR
     * RM 1.0.2 archetypes against 1.0.4
     *
     * @param rmPublisher         the publisher of the RM
     * @param rmPackage           the package of the RM to override the version for
     * @param overridingRmRelease the version that should be chosen
     */
    public void overrideModelVersion(String rmPublisher, String rmPackage, String overridingRmRelease) {
        this.overriddenMetaModelVersions.put(
                BmmDefinitions.publisherQualifiedRmClosureName(rmPublisher, rmPackage),
                overridingRmRelease
        );
    }

    /**
     * Remove the overriden model version for the given publisher and package.
     *
     * @param rmPublisher the publisher of the package
     * @param rmPackage   the RM Package to remove the model version for
     */
    public void removeOverridenModelVersion(String rmPublisher, String rmPackage) {
        this.overriddenMetaModelVersions.remove(BmmDefinitions.publisherQualifiedRmClosureName(rmPublisher, rmPackage));
    }

    /**
     * Get the overriden model version for the given package, if any.
     *
     * @param rmPublisher the publisher of the package
     * @param rmPackage   the RM Package to remove the model version for
     * @return the overridden model version, or null if no override has been set
     */
    public String getOverriddenModelVersion(String rmPublisher, String rmPackage) {
        return this.overriddenMetaModelVersions.get(BmmDefinitions.publisherQualifiedRmClosureName(rmPublisher, rmPackage));
    }

    @Override
    public MetaModel getMetaModel(Archetype archetype) throws ModelNotFoundException {
        String overriddenVersion = getOverriddenModelVersion(archetype.getArchetypeId().getRmPublisher(), archetype.getArchetypeId().getRmPackage());
        return getMetaModel(archetype, overriddenVersion == null ? archetype.getRmRelease() : overriddenVersion);
    }

    @Override
    public MetaModel getMetaModel(String rmPublisher, String rmPackage, String rmRelease) throws ModelNotFoundException {
        return delegate.getMetaModel(rmPublisher, rmPackage, rmRelease);
    }
}
