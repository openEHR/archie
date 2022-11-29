package org.openehr.referencemodels;

import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomProfiles;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMObjectMapperProvider;
import com.nedap.archie.rminfo.ReferenceModels;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;
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
public class BuiltinReferenceModels {

    private static final Logger logger = LoggerFactory.getLogger(BuiltinReferenceModels.class);

    /**
     * Static cache
     */
    private static AomProfiles aomProfiles;

    private static BmmRepository bmmRepository;

    public static BmmRepository getBmmRepository() {
        if(bmmRepository != null) {
            return bmmRepository;
        }

        String[] resources = { // "bmm/CIMI/Release-0.0.3/BMM/CIMI_RM_CORE.v.0.0.3.bmm",
                "bmm/openEHR/components/RM/Release-1.0.4/openehr_rm_demographic_104.bmm",
                "bmm/openEHR/original/Release-1.0.2/openehr_structures_102.bmm",
                "bmm/openEHR/components/RM/Release-1.0.3/openehr_ehr_103.bmm",
                "bmm/openEHR/original/Release-1.0.2/openehr_primitive_types_102.bmm",
                "bmm/openEHR/components/RM/Release-1.0.4/openehr_rm_ehr_extract_104.bmm",
                "bmm/openEHR/components/RM/Release-1.0.3/openehr_rm_103.bmm",
                "bmm/openEHR/components/BASE/Release-1.1.0/openehr_base_110.bmm",
                "bmm/openEHR/components/RM/rejected/openehr_ehr_extract_999.bmm",
                // "bmm/CIMI/Release-0.0.3/BMM/CIMI_RM_CLINICAL.v.0.0.3.bmm",
                "bmm/openEHR/original/Release-1.0.2/openehr_demographic_102.bmm",
                "bmm/openEHR/original/Release-1.0.2/openehr_ehr_102.bmm",
                "bmm/openEHR/components/LANG/Release-1.0.0/openehr_lang_100.bmm",
                "bmm/openEHR/components/RM/Release-1.0.4/openehr_rm_ehr_104.bmm",
                "bmm/openEHR/components/PROC/Release-1.5.0/openehr_proc_task_planning_150.bmm",
                "bmm/openEHR/components/PROC/Release-1.6.0/openehr_proc_task_planning_160.bmm",
                "bmm/openEHR/components/PROC/latest/openehr_proc_task_planning_110.bmm",
                "bmm/openEHR/original/Release-1.0.2/openehr_rm_102.bmm",
                "bmm/openEHR/components/RM/Release-1.0.4/openehr_rm_structures_104.bmm",
                "bmm/FHIR/DSTU/BMM/hl7_fhir_resourceses_dstu.bmm",
                "bmm/openEHR/components/RM/Release-1.0.3/openehr_demographic_103.bmm",
                "bmm/openEHR/components/RM/Release-1.0.4/openehr_rm_104.bmm",
                // "bmm/ISO_13606/2008/BMM/cen_ts14796_0.90.bmm",
                "bmm/openEHR/components/BASE/Release-1.0.0/openehr_base_100.bmm",
                // "bmm/CDISC/Core/BMM/CDISC-Core-0.5.0.bmm",
                "bmm/openEHR/adl_test/Release-1.0.0/BMM/openehr_adltest_100.bmm",
                // "bmm/ISO_21090/2011/BMM/iso_21090_0.9.0.bmm",
                "bmm/openEHR/components/PROC/Release-1.0.0/openehr_proc_task_planning_100.bmm",
                "bmm/openEHR/components/RM/Release-1.0.3/openehr_structures_103.bmm",
                "bmm/openEHR/components/RM/Release-1.0.3/openehr_ehr_extract_103.bmm",
                "bmm/openEHR/original/Release-1.0.2/openehr_basic_types_102.bmm",
                "bmm/openEHR/components/RM/Release-1.0.3/openehr_primitive_types_103.bmm",
                "bmm/openEHR/components/RM/Release-1.0.3/openehr_basic_types_103.bmm",
                // "bmm/ISO_13606/2008/BMM/cen_EN13606_0.95.bmm",
                "bmm/openEHR/components/RM/Release-1.0.4/openehr_rm_data_types_104.bmm",
                "bmm/openEHR/components/RM/Release-1.1.0/openehr_rm_110.bmm",
                "bmm/openEHR/components/RM/Release-1.1.0/openehr_rm_data_types_110.bmm",
                "bmm/openEHR/components/RM/Release-1.1.0/openehr_rm_demographic_110.bmm",
                "bmm/openEHR/components/RM/Release-1.1.0/openehr_rm_ehr_110.bmm",
                "bmm/openEHR/components/RM/Release-1.1.0/openehr_rm_ehr_extract_110.bmm",
                "bmm/openEHR/components/RM/Release-1.1.0/openehr_rm_structures_110.bmm"

                // "bmm/CIMI/Release-0.0.3/BMM/CIMI_RM_FOUNDATION.v.0.0.3.bmm"
        };
        bmmRepository = new BmmRepository();
        for(String resourceName:resources) {
            logger.info("parsing " + resourceName);
            try(InputStream stream = BuiltinReferenceModels.class.getResourceAsStream("/" + resourceName)) { //not sure why the "/" + is needed, but it is
                bmmRepository.addPersistentSchema(BmmOdinParser.convert(stream));
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
        BuiltinReferenceModels.aomProfiles = profiles;
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
     * Returns the MetaModels loaded with all BMM, ModelInfoLookup and AOM profiles that are available.
     * Returns a new MetaModels instance every call!
     * @return
     */
    public static MetaModels getMetaModels() {
        MetaModels metaModels = new MetaModels(getAvailableModelInfoLookups(), getBmmRepository());
        for(AomProfile profile:getAomProfiles().getProfiles()) {
            metaModels.getAomProfiles().add(profile);
        }
        return metaModels;
    }
}