package com.nedap.archie.openehr.rminfo;

import com.nedap.archie.openehr.serialisation.json.OpenEhrRmObjectMapperProvider;
import com.nedap.archie.rminfo.MetaModelsInitialiser;
import com.nedap.archie.rminfo.RMObjectMapperProvider;
import com.nedap.archie.rminfo.ReferenceModels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Global meta-model access for openEHR RM
 */
public class OpenEhrRmMetaModelsInitialiser extends MetaModelsInitialiser {

    private static final Logger logger = LoggerFactory.getLogger(OpenEhrRmMetaModelsInitialiser.class);

    /**
     * Returns the model info lookups that are built in archie and are available in the classloader. Add the reference
     * models to your dependencies to make this return values
     * @return
     */
    public ReferenceModels getNativeRms() {
        ReferenceModels result = new ReferenceModels();

        RMObjectMapperProvider provider = null;
        try {
            Class<?> objectMapperProvider = OpenEhrRmObjectMapperProvider.class;
            Constructor<?> getProviderInstance = objectMapperProvider.getConstructor();
            provider = (RMObjectMapperProvider) getProviderInstance.newInstance();
        }
        catch (InstantiationException | NoSuchMethodException |  IllegalAccessException | InvocationTargetException ignored) {
        }

        result.registerModel(OpenEhrRmInfoLookup.getInstance(), provider);

        return result;
    }

    public String getParentPackage() {
        return "org.openehr";
    }

}