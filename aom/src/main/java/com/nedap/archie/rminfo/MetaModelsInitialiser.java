package com.nedap.archie.rminfo;

import com.nedap.archie.aom.profile.AomProfile;
import com.nedap.archie.aom.profile.AomProfiles;
import org.openehr.bmm.v2.persistence.odin.BmmOdinParser;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Global meta-model access for openEHR RM
 */
public abstract class MetaModelsInitialiser {

    private static final Logger logger = LoggerFactory.getLogger(MetaModelsInitialiser.class);

    /**
     * cache
     */
    private AomProfiles aomProfiles;

    private BmmRepository bmmRepository;

    public BmmRepository getBmmRepository() {
        if(bmmRepository != null) {
            return bmmRepository;
        }

        // The first arg will be something like org.openehr.bmm, which is the package name of the access
        // class within src/main, but also the package path under src/main/resources
        Reflections reflections = new Reflections(getParentPackage() + ".bmm", Scanners.Resources);
        List<String> resources = new ArrayList<>(reflections.getResources (Pattern.compile(".*\\.bmm")));

        bmmRepository = new BmmRepository();
        for(String resourceName:resources) {
            logger.info("parsing " + resourceName);
            try(InputStream stream = this.getClass().getResourceAsStream("/" + resourceName)) { //not sure why the "/" + is needed, but it is
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
     * Returns the AOM Profiles available in the resources of this module
     * @return
     */
    public AomProfiles getAomProfiles() {
        if(aomProfiles != null) {
            return aomProfiles;
        }
        AomProfiles profiles = new AomProfiles();

        //now parse the AOM profiles - they should be in a place like resources/org.openehr.aom_profiles
        Reflections reflections = new Reflections(getParentPackage() + ".aom_profiles", Scanners.Resources);
        List<String> resourceNames = new ArrayList<>(reflections.getResources (Pattern.compile(".*\\.arp")));

        for(String resource:resourceNames) {
            try(InputStream odin = this.getClass().getResourceAsStream("/" + resource)){
                profiles.add(odin);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        aomProfiles = profiles;
        return profiles;
    }

    /**
     * Returns the model info lookups that are built in archie and are available in the classloader. Add the reference
     * models to your dependencies to make this return values
     * @return
     */
    abstract public ReferenceModels getNativeRms();

    /**
     * Returns the MetaModels loaded with all BMM, ModelInfoLookup and AOM profiles that are available.
     * Returns a new MetaModels instance every call!
     * @return
     */
    public MetaModels getMetaModels() {
        MetaModels metaModels = new MetaModels(getNativeRms(), getBmmRepository());
        for(AomProfile profile:getAomProfiles().getProfiles()) {
            metaModels.getAomProfiles().add(profile);
        }
        return metaModels;
    }

    abstract public String getParentPackage();
}