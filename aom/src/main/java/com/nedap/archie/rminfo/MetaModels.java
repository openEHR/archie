package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomProfiles;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidationResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 */
public class MetaModels implements MetaModelInterface {

    private final ReferenceModels models;
    private final BmmRepository bmmRepository;
    private AomProfiles aomProfiles;

    private MetaModel selectedModel;
    private AomProfile selectedAomProfile;

    /**
     * Allows to set a specific RM version for a specific RM model, so that one is used instead of the one in the archetype
     */
    private Map<String, String> overriddenMetaModelVersions = new ConcurrentHashMap<>();


    public MetaModels(ReferenceModels models, BmmRepository repository) {
        this.models = models;
        this.bmmRepository = repository;
        aomProfiles = new AomProfiles();
    }

    public MetaModels(ReferenceModels models, BmmRepository repository, AomProfiles profiles) {
        this.models = models;
        this.bmmRepository = repository;
        aomProfiles = profiles;
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
        this.overriddenMetaModelVersions.put(
                BmmDefinitions.publisherQualifiedRmClosureName(rmPublisher, rmPackage),
                version
        );
    }

    /**
     * Remove the overriden model version for the given package
     * @param rmPublisher The publisher of the package
     * @param rmPackage the RM Package to remove the model version for
     */
    public void removeOverridenModelVersion(String rmPublisher, String rmPackage) {
        this.overriddenMetaModelVersions.remove(BmmDefinitions.publisherQualifiedRmClosureName(rmPublisher, rmPackage));
    }


    public String getOverriddenModelVersion(String rmPublisher, String rmPackage) {
        return this.overriddenMetaModelVersions.get(BmmDefinitions.publisherQualifiedRmClosureName(rmPublisher, rmPackage));
    }

    /**
     * Select a meta model based on an archetype
     * @param archetype the archetype to find the model for
     * @throws ModelNotFoundException when no BMM and no ModelInfoLookup model has been found matching the archetype
     */
    public void selectModel(Archetype archetype) throws ModelNotFoundException { ;
        String overriddenVersion = getOverriddenModelVersion(archetype.getArchetypeId().getRmPublisher(), archetype.getArchetypeId().getRmPackage());
        selectModel(archetype, overriddenVersion == null ? archetype.getRmRelease(): overriddenVersion);
    }

    /**
     * Select a model based on an archetype, but override the RM Release with the given rm release version
     * @param archetype the archetype to find the model for
     * @param rmVersion the version of the reference model you want to check with.
     * @throws ModelNotFoundException
     */
    public void selectModel(Archetype archetype, String rmVersion) throws ModelNotFoundException { ;
        selectModel(archetype.getArchetypeId().getRmPublisher(), archetype.getArchetypeId().getRmPackage(), rmVersion);
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
        ModelInfoLookup selectedModel = null;
        BmmModel selectedBmmModel = null;
        RMObjectMapperProvider objectMapperProvider = null;
        if(models != null) {
             selectedModel = models.getModel(rmPublisher, rmPackage);
             objectMapperProvider = models.getRmObjectMapperProvider(rmPublisher, rmPackage);
        }
        if(bmmRepository != null) {
            BmmValidationResult validationResult = bmmRepository.getModelByClosure(BmmDefinitions.publisherQualifiedRmClosureName(rmPublisher, rmPackage) + "_" +  rmRelease);
            selectedBmmModel = validationResult == null ? null : validationResult.getModel();
        }

        this.selectedAomProfile = getAomProfileWithSchemaId(selectedBmmModel);
        if(this.selectedAomProfile == null) {
            this.selectedAomProfile = getAomProfileOnPublisher(rmPublisher);
        }

        if(selectedModel == null && selectedBmmModel == null) {
            throw new ModelNotFoundException(String.format("model for %s.%s version %s not found", rmPublisher, rmPackage, rmRelease));
        }
        this.selectedModel = new MetaModel(selectedModel, selectedBmmModel, selectedAomProfile, objectMapperProvider);

    }

    private AomProfile getAomProfileWithSchemaId(BmmModel selectedBmmModel) {
        if(selectedBmmModel != null) {
            for (AomProfile profile : aomProfiles.getProfiles()) {
                if (profile.getRmSchemaPattern().stream().anyMatch(pat -> selectedBmmModel.getSchemaId().matches(pat))) {
                    return profile;
                }
            }
        }
        return null;
    }

    private AomProfile getAomProfileOnPublisher(String rmPublisher) {
        for(AomProfile profile:aomProfiles.getProfiles()) {
           if(profile.getProfileName().equalsIgnoreCase(rmPublisher)) {
                return profile;
            }
        }
        return null;
    }

    public ModelInfoLookup getSelectedModelInfoLookup() {
        return selectedModel == null ? null : selectedModel.getSelectedModel();
    }

    public BmmModel getSelectedBmmModel() {
        return selectedModel == null ? null : selectedModel.getSelectedBmmModel();
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

    public boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject cObject) {
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
        return selectedAomProfile;
    }


}
