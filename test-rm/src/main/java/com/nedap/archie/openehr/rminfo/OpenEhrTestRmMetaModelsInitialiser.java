package com.nedap.archie.openehr.rminfo;

import com.nedap.archie.rminfo.MetaModelsInitialiser;
import com.nedap.archie.rminfo.ReferenceModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global meta-model access for openEHR RM
 */
public class OpenEhrTestRmMetaModelsInitialiser extends MetaModelsInitialiser {

    private static final Logger logger = LoggerFactory.getLogger(OpenEhrTestRmMetaModelsInitialiser.class);

    /**
     * Returns the model info lookups that are built in archie and are available in the classloader. Add the reference
     * models to your dependencies to make this return values
     * @return
     */
    public ReferenceModels getNativeRms() {
        ReferenceModels result = new ReferenceModels();
        result.registerModel(OpenEhrTestRmInfoLookup.getInstance(), null);
        return result;
    }

    public String getParentPackage() {
        return "org.openehr";
    }
}