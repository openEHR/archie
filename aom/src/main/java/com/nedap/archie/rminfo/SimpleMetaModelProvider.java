package com.nedap.archie.rminfo;

import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomProfiles;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidationResult;

/**
 * A simple implementation of the MetaModelProvider interface that retrieves meta models from either a ReferenceModels
 * instance or a BmmRepository. It also supports AOM profiles.
 */
public class SimpleMetaModelProvider implements MetaModelProvider {
    private final ReferenceModels referenceModels;
    private final BmmRepository bmmRepository;
    private final AomProfiles aomProfiles;

    /**
     * Create a SimpleMetaModelProvider that retrieves meta models from the given ReferenceModels and BmmRepository.
     * If both are null, an IllegalArgumentException will be thrown.
     *
     * @param referenceModels the ReferenceModels to use for retrieving meta models
     * @param bmmRepository   the BmmRepository to use for retrieving BMM models
     */
    public SimpleMetaModelProvider(ReferenceModels referenceModels, BmmRepository bmmRepository) {
        this(referenceModels, bmmRepository, new AomProfiles());
    }

    /**
     * Create a SimpleMetaModelProvider that retrieves meta models from the given ReferenceModels, BmmRepository, and
     * AomProfiles. If both referenceModels and bmmRepository are null, an IllegalArgumentException will be thrown.
     *
     * @param referenceModels the ReferenceModels to use for retrieving meta models
     * @param bmmRepository   the BmmRepository to use for retrieving BMM models
     * @param aomProfiles     the AomProfiles to use for retrieving AOM profiles
     */
    public SimpleMetaModelProvider(ReferenceModels referenceModels, BmmRepository bmmRepository, AomProfiles aomProfiles) {
        if (referenceModels == null && bmmRepository == null) {
            throw new IllegalArgumentException("Either referenceModels or bmmRepository must be provided");
        }
        this.referenceModels = referenceModels;
        this.bmmRepository = bmmRepository;
        this.aomProfiles = aomProfiles;
    }

    @Override
    public MetaModel getMetaModel(String rmPublisher, String rmPackage, String rmRelease) throws ModelNotFoundException {
        ModelInfoLookup modelInfoLookup = null;
        BmmModel bmmModel = null;
        RMObjectMapperProvider objectMapperProvider = null;
        if (referenceModels != null) {
            modelInfoLookup = referenceModels.getModel(rmPublisher, rmPackage);
            objectMapperProvider = referenceModels.getRmObjectMapperProvider(rmPublisher, rmPackage);
        }
        if (bmmRepository != null) {
            BmmValidationResult validationResult = bmmRepository.getModelByClosure(BmmDefinitions.publisherQualifiedRmClosureName(rmPublisher, rmPackage) + "_" + rmRelease);
            bmmModel = validationResult == null ? null : validationResult.getModel();
        }

        AomProfile aomProfile = getAomProfileWithSchemaId(bmmModel);
        if (aomProfile == null) {
            aomProfile = getAomProfileOnPublisher(rmPublisher);
        }

        if (modelInfoLookup == null && bmmModel == null) {
            throw new ModelNotFoundException(String.format("model for %s.%s version %s not found", rmPublisher, rmPackage, rmRelease));
        }
        return new MetaModel(modelInfoLookup, bmmModel, aomProfile, objectMapperProvider);
    }

    private AomProfile getAomProfileWithSchemaId(BmmModel bmmModel) {
        if (bmmModel != null) {
            for (AomProfile profile : aomProfiles.getProfiles()) {
                if (profile.getRmSchemaPattern().stream().anyMatch(pat -> bmmModel.getSchemaId().matches(pat))) {
                    return profile;
                }
            }
        }
        return null;
    }

    private AomProfile getAomProfileOnPublisher(String rmPublisher) {
        for (AomProfile profile : aomProfiles.getProfiles()) {
            if (profile.getProfileName().equalsIgnoreCase(rmPublisher)) {
                return profile;
            }
        }
        return null;
    }
}
