package com.nedap.archie.rminfo;


import com.nedap.archie.aom.Archetype;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceModels {
    private Map<RMPackageId, ModelInfoLookup> referenceModelsById = new ConcurrentHashMap<>();
    private Map<RMPackageId, RMObjectMapperProvider> objectMapperProvidersById = new ConcurrentHashMap<>();

    public ReferenceModels() {

    }

    public ReferenceModels(ModelInfoLookup... models) {
        for(ModelInfoLookup model:models) {
            registerModel(model);
        }
    }

    public void registerModel(ModelInfoLookup model) {
        registerModel(model, null);
    }

    public void registerModel(ModelInfoLookup model, RMObjectMapperProvider rmObjectMapperProvider) {
        for(RMPackageId id:model.getId()) {
            referenceModelsById.put(id, model);
            if(rmObjectMapperProvider != null) {
                objectMapperProvidersById.put(id, rmObjectMapperProvider);
            }
        }
    }

    public ModelInfoLookup getModel(String publisher, String aPackage) {
        return referenceModelsById.get(new RMPackageId(publisher, aPackage));
    }

    public ModelInfoLookup getModel(Archetype archetype) {
        return getModel(archetype.getArchetypeId().getRmPublisher(), archetype.getArchetypeId().getRmPackage());
    }

    public RMObjectMapperProvider getRmObjectMapperProvider(String publisher, String aPackage) {
        return objectMapperProvidersById.get(new RMPackageId(publisher, aPackage));
    }

    public RMObjectMapperProvider getRmObjectMapperProvider(Archetype archetype) {
        return getRmObjectMapperProvider(archetype.getArchetypeId().getRmPublisher(), archetype.getArchetypeId().getRmPackage());
    }

    public List<ModelInfoLookup> getAllModels() {
        return new ArrayList<>(referenceModelsById.values());
    }

    public void merge (ReferenceModels otherRms) {
        referenceModelsById.putAll(otherRms.referenceModelsById);
        objectMapperProvidersById.putAll(otherRms.objectMapperProvidersById);
    }
}
