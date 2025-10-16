package org.openehr.referencemodels;

import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomProfiles;
import com.nedap.archie.rminfo.*;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.jackson.BmmJacksonUtil;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class that loads all available meta-model classes that are available in Archie
 *
 * uses reflection to only load the ModelInfoLookup classes that are available
 */
public class BuiltinReferenceModelsJson {

    private static final Logger logger = LoggerFactory.getLogger(BuiltinReferenceModels.class);

    /**
     * Static cache
     */
    private static AomProfiles aomProfiles;

    private static BmmRepository bmmRepository;

    private static MetaModelProvider metaModelProvider;

    public static BmmRepository getBmmRepository() {
        if(bmmRepository != null) {
            return bmmRepository;
        }

        String[] resources = { // "bmm/CIMI/Release-0.0.3/BMM/CIMI_RM_CORE.v.0.0.3.bmm",
                "bmm/openEHR/components/BASE/Release-1.2.0/openehr_base_1.2.0.bmm.json",
                "bmm/openEHR/components/RM/Release-1.1.0/openehr_rm_1.1.0.bmm.json"
        };
        bmmRepository = new BmmRepository();
        for(String resourceName:resources) {
            logger.info("parsing " + resourceName);
            try(InputStream stream = BuiltinReferenceModels.class.getResourceAsStream("/" + resourceName)) { //not sure why the "/" + is needed, but it is
                bmmRepository.addPersistentSchema(BmmJacksonUtil.getObjectMapper().readValue(stream, PBmmSchema.class));
            } catch (IOException e) {
                throw new RuntimeException("error loading file: " + e);
            } catch (RuntimeException ex) {
                logger.error("error parsing {}", resourceName, ex);
            }
        }
        BmmSchemaConverter converter = new BmmSchemaConverter(bmmRepository);

        converter.validateAndConvertRepository();
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
        //now parse the AOM profiles
        String[] resourceNames = {"/aom_profiles/openehr_aom_profile.arp",
                "/aom_profiles/cdisc_aom_profile.arp",
                "/aom_profiles/cimi_aom_profile.arp",
                "/aom_profiles/fhir_aom_profile.arp",
                "/aom_profiles/iso13606_aom_profile.arp",
        };
        for(String resource:resourceNames) {
            try(InputStream odin = BuiltinReferenceModels.class.getResourceAsStream(resource)){
                profiles.add(odin);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        BuiltinReferenceModelsJson.aomProfiles = profiles;
        return profiles;
    }

    /**
     * Returns the model info lookups that are built in archie and are available in the classloader. Add the reference
     * models to your dependencies to make this return values
     * @return
     */
    public static ReferenceModels getAvailableModelInfoLookups() {
        ReferenceModels result = new ReferenceModels();
        addModelInfoLookupIfExists(result, "com.nedap.archie.rminfo.ArchieRMInfoLookup", "com.nedap.archie.json.ArchieRMObjectMapperProvider");
        addModelInfoLookupIfExists(result, "com.nedap.archie.openehrtestrm.TestRMInfoLookup", null );
        return result;
    }

    private static void addModelInfoLookupIfExists(ReferenceModels result, String className, String objectMapperProviderClassName) {
        try {
            Class<?> openEhrRMLookup = Class.forName(className);
            Method getInstance = openEhrRMLookup.getDeclaredMethod("getInstance");
            ModelInfoLookup modelInfo = (ModelInfoLookup) getInstance.invoke(null);
            RMObjectMapperProvider provider = null;
            if(objectMapperProviderClassName != null) {
                try {
                    Class<?> objectMapperProvider = Class.forName(objectMapperProviderClassName);
                    Constructor<?> getProviderInstance = objectMapperProvider.getConstructor();
                    provider = (RMObjectMapperProvider) getProviderInstance.newInstance();
                } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException |  IllegalAccessException | InvocationTargetException e) {
                    //not present, that's fine. Maybe do a bit of debug logging?
                }
            }
            result.registerModel(modelInfo, provider);
        } catch (ClassNotFoundException | NoSuchMethodException |  IllegalAccessException | InvocationTargetException e) {
            //not present, that's fine. Maybe do a bit of debug logging?
        }
    }

    /**
     * Returns a MetaModelProvider loaded with all BMM models, ModelInfoLookups and AOM profiles that are available.
     */
    public static MetaModelProvider getMetaModelProvider() {
        if (metaModelProvider != null) {
            return metaModelProvider;
        }
        metaModelProvider = new SimpleMetaModelProvider(getAvailableModelInfoLookups(), getBmmRepository(), getAomProfiles());
        return metaModelProvider;
    }

    /**
     * Returns the MetaModels loaded with all BMM, ModelInfoLookup and AOM profiles that are available.
     * Returns a new MetaModels instance every call!
     * @return
     * @deprecated Use {@link #getMetaModelProvider()} instead.
     */
    @Deprecated
    public static MetaModels getMetaModels() {
        MetaModels metaModels = new MetaModels(getAvailableModelInfoLookups(), getBmmRepository());
        for(AomProfile profile:getAomProfiles().getProfiles()) {
            metaModels.getAomProfiles().add(profile);
        }
        return metaModels;
    }
}