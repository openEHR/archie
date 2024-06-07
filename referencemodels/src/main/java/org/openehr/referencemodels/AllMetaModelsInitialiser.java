package org.openehr.referencemodels;

import com.nedap.archie.aom.profile.AomProfiles;
import com.nedap.archie.rminfo.*;
import org.openehr.bmm.v2.validation.BmmRepository;
import com.nedap.archie.openehr.rminfo.OpenEhrRmMetaModelsInitialiser;
import com.nedap.archie.openehr.rminfo.OpenEhrTestRmMetaModelsInitialiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that loads all available meta-model classes that are available in Archie
 *
 * uses reflection to only load the ModelInfoLookup classes that are available
 */
public class AllMetaModelsInitialiser {

    private static final Logger logger = LoggerFactory.getLogger(AllMetaModelsInitialiser.class);

    /**
     * Static cache
     */
    private static AomProfiles aomProfiles;

    private static BmmRepository bmmRepository;

    private static List<MetaModelsInitialiser> mmInitializers;

    private static List<MetaModelsInitialiser> getMmInitializers() {
        if(mmInitializers != null) {
            return mmInitializers;
        }
        mmInitializers = new ArrayList<>();
        mmInitializers.add(new OpenEhrRmMetaModelsInitialiser());
        mmInitializers.add(new OpenEhrTestRmMetaModelsInitialiser());
        return mmInitializers;
    }

    public static BmmRepository getBmmRepository() {
        if(bmmRepository != null) {
            return bmmRepository;
        }

        bmmRepository = new BmmRepository();

        for (MetaModelsInitialiser mmInitializer: getMmInitializers()) {
            bmmRepository.merge (mmInitializer.getBmmRepository());
        }

        return bmmRepository;
    }

    /**
     * Returns the built in AOM Profiles
     * @return
     */
    public static AomProfiles getAomProfiles() {
        if(aomProfiles != null) {
            return aomProfiles;
        }

        AomProfiles profiles = new AomProfiles();

        for (MetaModelsInitialiser mmInitializer: getMmInitializers()) {
            profiles.merge (mmInitializer.getAomProfiles());
        }

        AllMetaModelsInitialiser.aomProfiles = profiles;
        return profiles;
    }

    public static ReferenceModels getNativeRms() {
        ReferenceModels result = new ReferenceModels();
        for (MetaModelsInitialiser mmInitializer: getMmInitializers()) {
            result.merge (mmInitializer.getNativeRms());
        }
        return result;
    }

    /**
     * Returns the MetaModels loaded with all BMM, ModelInfoLookup and AOM profiles that are available.
     * Returns a new MetaModels instance every call!
     * @return
     */
    public static MetaModels getMetaModels() {
        return new MetaModels(getNativeRms(), getBmmRepository(), getAomProfiles());
    }
}