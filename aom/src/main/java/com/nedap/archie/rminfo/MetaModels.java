package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomProfiles;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.v2.validation.BmmRepository;

/**
 * MetaModel class that provides some opertaions for archetype validation and flattener that is either based on
 * an implementation-derived model (ModelInfoLookup) or BMM
 *
 * To use, select a model first using the selectModel() method. Then you can use any of the methods from MetaModelInterface
 * or obtain the underlying models directly. Trying to use the MetaModelInterface methods without selecting a model will
 * result in a NoModelSelectedException being thrown.
 *
 * By default the MetaModels uses the RM version from the archetype. It is possible to override this version either for
 * the entire MetaModels, or for a specific call, with the overrideModelVersion() method, and the two-parameter
 * selectModel() method.
 *
 * Note that this class is NOT thread-safe and is to be used by a single thread only.
 *
 * @deprecated Use {@link SimpleMetaModelProvider}, {@link OverridingMetaModelProvider} or {@link MetaModel} instead.
 */
@Deprecated
public class MetaModels implements MetaModelInterface, MetaModelProvider {
    private final ReferenceModels models;
    private final BmmRepository bmmRepository;
    private final AomProfiles aomProfiles;
    private final OverridingMetaModelProvider overridingMetaModelProvider;

    private MetaModel selectedModel;

    public MetaModels(ReferenceModels models, BmmRepository repository) {
        this(models, repository, new AomProfiles());
    }

    public MetaModels(ReferenceModels models, BmmRepository repository, AomProfiles profiles) {
        this.models = models;
        this.bmmRepository = repository;
        aomProfiles = profiles;
        this.overridingMetaModelProvider = new OverridingMetaModelProvider(
                new SimpleMetaModelProvider(models, repository, profiles)
        );
    }

    /**
     * Indicate that the model version for the given package by the given publisher should be fixed
     * to a specific version. Useful for validating archetypes against new RM versions, for example OpenEHR
     * RM 1.0.2 archetypes against 1.0.4
     * @param rmPublisher the publisher of the RM
     * @param rmPackage the package of the RM to override the version for
     * @param version the version that should be chosen
     */
    public void overrideModelVersion(String rmPublisher, String rmPackage, String version) {
        overridingMetaModelProvider.overrideModelVersion(rmPublisher, rmPackage, version);
    }

    /**
     * Remove the overriden model version for the given package
     * @param rmPublisher The publisher of the package
     * @param rmPackage the RM Package to remove the model version for
     */
    public void removeOverridenModelVersion(String rmPublisher, String rmPackage) {
        overridingMetaModelProvider.removeOverridenModelVersion(rmPublisher, rmPackage);
    }

    public String getOverriddenModelVersion(String rmPublisher, String rmPackage) {
        return overridingMetaModelProvider.getOverriddenModelVersion(rmPublisher, rmPackage);
    }

    @Override
    public MetaModel getMetaModel(Archetype archetype) throws ModelNotFoundException {
        return overridingMetaModelProvider.getMetaModel(archetype);
    }

    @Override
    public MetaModel getMetaModel(Archetype archetype, String rmVersion) throws ModelNotFoundException {
        return overridingMetaModelProvider.getMetaModel(archetype, rmVersion);
    }

    @Override
    public MetaModel getMetaModel(String rmPublisher, String rmPackage, String rmRelease) throws ModelNotFoundException {
        return overridingMetaModelProvider.getMetaModel(rmPublisher, rmPackage, rmRelease);
    }

    @Override
    public MetaModel selectAndGetMetaModel(Archetype archetype) throws ModelNotFoundException {
        MetaModel result = getMetaModel(archetype);
        this.selectedModel = result;
        return result;
    }

    @Override
    public MetaModel selectAndGetMetaModel(Archetype archetype, String rmVersion) throws ModelNotFoundException {
        MetaModel result = getMetaModel(archetype, rmVersion);
        this.selectedModel = result;
        return result;
    }

    @Override
    public MetaModel selectAndGetMetaModel(String rmPublisher, String rmPackage, String rmRelease) throws ModelNotFoundException {
        MetaModel result = getMetaModel(rmPublisher, rmPackage, rmRelease);
        this.selectedModel = result;
        return result;
    }

    /**
     * Select a meta model based on an archetype
     * @param archetype the archetype to find the model for
     * @throws ModelNotFoundException when no BMM and no ModelInfoLookup model has been found matching the archetype
     */
    public void selectModel(Archetype archetype) throws ModelNotFoundException { ;
        selectAndGetMetaModel(archetype);
    }

    /**
     * Select a model based on an archetype, but override the RM Release with the given rm release version
     * @param archetype the archetype to find the model for
     * @param rmVersion the version of the reference model you want to check with.
     * @throws ModelNotFoundException
     */
    public void selectModel(Archetype archetype, String rmVersion) throws ModelNotFoundException { ;
        selectAndGetMetaModel(archetype, rmVersion);
    }

    /**
     * Select a model based on an publisher, package and rm release version. Will NOT take into account overriden RM
     * versions, use the other selectModel() methods for that.
     * @param rmPublisher RM Publisher
     * @param rmPackage RM Package
     * @param rmRelease the version of the reference model you want to check with.
     * @throws ModelNotFoundException
     */
    public void selectModel(String rmPublisher, String rmPackage, String rmRelease) throws ModelNotFoundException {
        selectAndGetMetaModel(rmPublisher, rmPackage, rmRelease);
    }

    public ModelInfoLookup getSelectedModelInfoLookup() {
        return selectedModel == null ? null : selectedModel.getModelInfoLookup();
    }

    public BmmModel getSelectedBmmModel() {
        return selectedModel == null ? null : selectedModel.getBmmModel();
    }

    public MetaModel getSelectedModel() {
        return selectedModel;
    }

    public ReferenceModels getReferenceModels() {
        return models;
    }

    public BmmRepository getBmmRepository() {
        return bmmRepository;
    }

    public boolean isMultiple(String typeName, String attributeName) {
        checkThatModelHasBeenSelected();
        return selectedModel.isMultiple(typeName, attributeName);
    }

    public boolean rmTypesConformant(String childTypeName, String parentTypeName) {
        checkThatModelHasBeenSelected();
        return selectedModel.rmTypesConformant(childTypeName, parentTypeName);
    }

    public boolean typeNameExists(String typeName) {
        checkThatModelHasBeenSelected();
        return selectedModel.typeNameExists(typeName);
    }

    public boolean attributeExists(String rmTypeName, String propertyName) {
        checkThatModelHasBeenSelected();
        return selectedModel.attributeExists(rmTypeName, propertyName);
   }

    @Override
    public boolean isNullable(String typeId, String attributeName) {
        checkThatModelHasBeenSelected();
        return selectedModel.isNullable(typeId, attributeName);
    }


    public boolean typeConformant(String rmTypeName, String rmAttributeName, String childConstraintTypeName) {
        checkThatModelHasBeenSelected();
        return selectedModel.typeConformant(rmTypeName, rmAttributeName, childConstraintTypeName);

    }

    public boolean hasReferenceModelPath(String rmTypeName, String path) {
        checkThatModelHasBeenSelected();
        return selectedModel.hasReferenceModelPath(rmTypeName, path);
    }

    public MultiplicityInterval referenceModelPropMultiplicity(String rmTypeName, String rmAttributeName) {
        checkThatModelHasBeenSelected();
        return selectedModel.referenceModelPropMultiplicity(rmTypeName, rmAttributeName);
    }

    public boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject<?, ?> cObject) {
        checkThatModelHasBeenSelected();
        return selectedModel.validatePrimitiveType(rmTypeName, rmAttributeName, cObject);
    }

    private void checkThatModelHasBeenSelected() throws NoModelSelectedException {
        if(selectedModel == null) {
            throw new NoModelSelectedException("Please call the selectModel() method before trying to use MetaModels");
        }

    }

    public boolean isOrdered(String rmTypeName, String rmAttributeName) {
        checkThatModelHasBeenSelected();
        return selectedModel.isOrdered(rmTypeName, rmAttributeName);
    }

    public AomProfiles getAomProfiles() {
        return aomProfiles;
    }

    public AomProfile getSelectedAomProfile() {
        return selectedModel == null ? null : selectedModel.getAomProfile();
    }


}
