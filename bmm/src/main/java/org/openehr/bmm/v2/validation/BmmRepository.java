package org.openehr.bmm.v2.validation;

import org.openehr.bmm.v2.persistence.PBmmSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BmmRepository {

    private Map<String, PBmmSchema> persistentSchemasById = new ConcurrentHashMap<>();

    private Map<String, BmmValidationResult> modelsById = new ConcurrentHashMap<>();

    /** models by the archetype closure, eg for openEHR-EHR-OBSERVATION.test.v1.0.0
     * you use openEHR-EHR_1.0.4 for RM model 1.0.4
     */
    private Map<String, BmmValidationResult> modelsByClosure = new ConcurrentHashMap<>();

    public BmmRepository() {

    }

    public List<PBmmSchema> getPersistentSchemas() {
        return new ArrayList<>(persistentSchemasById.values());
    }

    public PBmmSchema getPersistentSchema(String schemaId) {
        return persistentSchemasById.get(schemaId.toLowerCase());
    }

    public void addPersistentSchema(PBmmSchema schema) {
        this.persistentSchemasById.put(schema.getSchemaId().toLowerCase(), schema);
    }

    public List<BmmValidationResult> getModels() {
        return new ArrayList<>(modelsById.values());
    }

    public List<BmmValidationResult> getValidModels() {
        return modelsById.values().stream().filter(validationResult -> validationResult.passes()).collect(Collectors.toList());
    }

    public List<BmmValidationResult> getInvalidModels() {
        return modelsById.values().stream().filter(validationResult -> !validationResult.passes()).collect(Collectors.toList());
    }

    public BmmValidationResult getModel(String schemaId) {
        return modelsById.get(schemaId.toLowerCase());
    }

    public void addModel(BmmValidationResult model) {
        this.modelsById.put(model.getSchemaId().toLowerCase(),  model);
    }

    public boolean containsPersistentSchema(String schemaId) {
        return persistentSchemasById.containsKey(schemaId.toLowerCase());
    }

    public void addModelByClosure(String closure,  BmmValidationResult model) {
        this.modelsByClosure.put(closure.toLowerCase(), model);
    }

    public BmmValidationResult getModelByClosure(String closure) {
        return modelsByClosure.get(closure.toLowerCase());
    }

    public void merge (BmmRepository otherBmmRepo) {
        persistentSchemasById.putAll(otherBmmRepo.persistentSchemasById);
        modelsById.putAll(otherBmmRepo.modelsById);
        modelsByClosure.putAll(otherBmmRepo.modelsByClosure);
    }
}
