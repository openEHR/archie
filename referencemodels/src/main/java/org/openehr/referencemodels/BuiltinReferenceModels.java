package org.openehr.referencemodels;

import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomProfiles;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.ReferenceModels;
import org.openehr.bmm.rmaccess.ReferenceModelAccess;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility class that loads all available meta-model classes that are available in Archie
 *
 * uses reflection to only load the ModelInfoLookup classes that are available
 */
public class BuiltinReferenceModels {

    /**
     * Static cache
     */
    private static ReferenceModelAccess access;
    private static AomProfiles aomProfiles;

    /**
     * Returns the built in BMM Reference Models
     * @return
     */
    public static ReferenceModelAccess getBMMReferenceModels() {
        if(access != null) {
            return access;
        }
        Reflections bmm = new Reflections("bmm", new ResourcesScanner());
        Set<String> resources = bmm.getResources(Pattern.compile(".*\\.bmm"));
        ReferenceModelAccess access = new ReferenceModelAccess();
        access.resetValidator();
        for(String resourceName:resources) {
            try(InputStream stream = BuiltinReferenceModels.class.getResourceAsStream("/" + resourceName)) { //not sure why the "/" + is needed, but it is
                access.addSchemaInputStream(stream, resourceName);
            } catch (IOException e) {
                throw new RuntimeException("error loading file: " + e);
            }
        }
        access.loadSchemas();
        BuiltinReferenceModels.access = access;
        return access;
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
        addModelInfoLookupIfExists(result, "com.nedap.archie.rminfo.ArchieRMInfoLookup");
        addModelInfoLookupIfExists(result, "com.nedap.archie.openehrtestrm.TestRMInfoLookup");
        return result;
    }

    private static void addModelInfoLookupIfExists(ReferenceModels result, String className) {
        try {
            Class<?> openEhrRMLookup = Class.forName(className);
            Method getInstance = openEhrRMLookup.getDeclaredMethod("getInstance");
            result.registerModel((ModelInfoLookup) getInstance.invoke(null));
        } catch (ClassNotFoundException | NoSuchMethodException |  IllegalAccessException | InvocationTargetException e) {
            //not present, don't care
        }
    }

    /**
     * Returns the MetaModels loaded with all BMM, ModelInfoLookup and AOM profiles that are available
     * @return
     */
    public static MetaModels getMetaModels() {
        MetaModels metaModels = new MetaModels(getAvailableModelInfoLookups(), getBMMReferenceModels());
        for(AomProfile profile:getAomProfiles().getProfiles()) {
            metaModels.getAomProfiles().add(profile);
        }
        return metaModels;
    }
}