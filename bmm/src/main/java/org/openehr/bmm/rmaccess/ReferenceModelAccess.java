package org.openehr.bmm.rmaccess;

        /*
         * #%L
         * OpenEHR - Java Model Stack
         * %%
         * Copyright (C) 2016 - 2017  Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
         * %%
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         *
         *      http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         * #L%
         * Author: Claude Nanjo
         */

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.persistence.BmmIncludeSpecification;
import org.openehr.bmm.persistence.PersistedBmmSchema;
import org.openehr.bmm.persistence.PersistedBmmSchemaState;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.utils.message.MessageLogger;
import org.openehr.utils.validation.BasicValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ReferenceModelAccess {

    private static final Logger log = LoggerFactory.getLogger(ReferenceModelAccess.class);
    private static int DEFAULT_NUMBER_OF_TRIES = 10;

    /**
     * Maximum number of levels of schema inclusion
     */
    public static final int MAXIMUM_INCLUSION_DEPTH = 10;

    /**
     * BMM File Filter
     */
    public static final IOFileFilter bmmFileFilter = new IOFileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getAbsolutePath().endsWith(BmmDefinitions.BMM_SCHEMA_FILE_EXTENSION);
        }

        @Override
        public boolean accept(File file, String s) {
            return s.endsWith(BmmDefinitions.BMM_SCHEMA_FILE_EXTENSION);
        }
    };

    /**
     * Validator to accumulate any errors encountered during the loading of schemas.
     */
    private BasicValidator validator;

    /**
     * List of directories where all the schemas loaded here are found.
     */
    private List<String> schemaDirectories;
    /**
     * All schemas found and loaded from `schema_directory'. Keyed by schema_id, i.e.
     * <p>
     * model_publisher '' schema_name '' model_release
     * e.g. openehr_rm_1.0.3, openehr_test_1.0.1, iso_13606-1_2008.
     * This is the same key as BMM_SCHEMA.schema_id. Does not
     * include schemas that failed to parse (i.e. SCHEMA_ACCESS.passed = False)
     */
    private Map<String, SchemaDescriptor> allSchemas;

    /**
     * Includes only fully validated schemas
     */
    private Map<String, SchemaDescriptor> candidateSchemas;

    /**
     * Top-level (root) schemas in use. Table is keyed by logical schema_name,
     * i.e. model_publisher '_' model_name, e.g. "openehr_rm". Schemas containing
     * different variants of same model (i.e. model_publisher + model_name) are
     * considered duplicates.
     */
    private Map<String, BmmModel> validModels;

    /**
     * All top-level schemas keyed by issuer
     */
    private Map<String, List<SchemaDescriptor>> topLevelSchemasByPublisher;

    /**
     * initial load list for this session, set during initialisation. This may initially be empty
     * or contain invalid entries; it will be modified to correctly list the actual schemas found
     */
    private List<String> schemaLoadList;

    /**
     * load counter so other parts of the application can see if anything has changed
     */
    private int loadCount;

    /**
     * set to True if any processing failed
     */
    private boolean exceptionEncountered;

    /**
     * map of inclusions among schemas found in the directory; structure:
     * {key = schema_id; {list of schemas that 'include' key}}
     * Schemas not included by other schemas have NO ENTRY in this list
     * this is detected and used to populate `top_level_schemas'
     */
    private Map<String, List<String>> schemaInclusionMap;

    /**
     * schemas keyed by lower-case qualified package name, i.e. model_publisher '-' package_name, e.g. "openehr-ehr";
     * this matches the qualified package name part of an ARCHETYPE_ID
     */
    private Map<String, BmmModel> modelsByClosure;

    private int numberOfTries = DEFAULT_NUMBER_OF_TRIES;

    /**
     * Cache of loaded persisted schemas.
     */
//    private Map<String, PersistedBmmSchema> persistedModelIndex;

    /**
     * No-arg constructors initializing all maps and lists.
     */
    public ReferenceModelAccess() {
        validator = new BasicValidator();
        schemaDirectories = new ArrayList<>();
        exceptionEncountered = false;
        allSchemas = new HashMap<>();
        candidateSchemas = new LinkedHashMap<>();
        validModels = new HashMap<>();
        topLevelSchemasByPublisher = new HashMap<>();
        schemaInclusionMap = new LinkedHashMap<>();
        schemaLoadList = new ArrayList<>();
        modelsByClosure = new LinkedHashMap<>();
    }

    public BasicValidator getValidator() {
        return validator;
    }

    public Map<String, SchemaDescriptor> getCandidateSchemas() {
        return candidateSchemas;
    }

    public void setCandidateSchemas(Map<String, SchemaDescriptor> candidateSchemas) {
        this.candidateSchemas = candidateSchemas;
    }

    public Map<String, List<SchemaDescriptor>> getTopLevelSchemasByPublisher() {
        return topLevelSchemasByPublisher;
    }

    public void setTopLevelSchemasByPublisher(Map<String, List<SchemaDescriptor>> topLevelSchemasByPublisher) {
        this.topLevelSchemasByPublisher = topLevelSchemasByPublisher;
    }

    public List<String> getSchemaLoadList() {
        return schemaLoadList;
    }

    public void setSchemaLoadList(List<String> schemaLoadList) {
        this.schemaLoadList = schemaLoadList;
    }

    public String getSchemaLoadListAsString() {
        return schemaLoadList.stream().collect(Collectors.joining(",  "));
    }

    public Map<String, List<String>> getSchemaInclusionMap() {
        return schemaInclusionMap;
    }

    public void setSchemaInclusionMap(Map<String, List<String>> schemaInclusionMap) {
        this.schemaInclusionMap = schemaInclusionMap;
    }

    public Map<String, BmmModel> getModelsByClosure() {
        return modelsByClosure;
    }

    public void setModelsByClosure(Map<String, BmmModel> modelsByClosure) {
        this.modelsByClosure = modelsByClosure;
    }

    public void addModelForClosure(String aQualifiedRmClosureName, BmmModel aModel) {
        this.modelsByClosure.put(aQualifiedRmClosureName.toLowerCase(), aModel);
    }

    public boolean hasReferenceModelForClosure(String aQualifiedRmClosureName) {
        return modelsByClosure.containsKey(aQualifiedRmClosureName.toLowerCase());
    }

    /**
     * Return ref model containing the model-class key `a_qualified_rm_closure_name', e.g. "openEHR-EHR"
     *
     * @param aQualifiedRmClosureName
     * @return
     */
    public BmmModel getReferenceModelForClosure(String aQualifiedRmClosureName) {
        return modelsByClosure.get(aQualifiedRmClosureName.toLowerCase());
    }

    /**
     * Method returns list of directories where all the schemas loaded are found.
     *
     * @return
     */
    public List<String> getSchemaDirectories() {
        return schemaDirectories;
    }

    /**
     * Method sets the list of directories where all the schemas to be loaded are found.
     *
     * @param schemaDirectories
     */
    public void setSchemaDirectories(List<String> schemaDirectories) {
        this.schemaDirectories = schemaDirectories;
    }

    /**
     * Method adds a schema directory.
     */
    public void addSchemaDirectory(String directory) {
        this.schemaDirectories.add(directory);
    }

    /**
     * True if there is a valid schema directory set
     */
    public boolean hasSchemaDirectory() {
        return !schemaDirectories.isEmpty();
    }

    /**
     * Method returns all schemas found and loaded from `schema_directory'. Map is keyed by schema_id.
     * <p>
     * A schema_id is formed by concatenating: model_publisher '' schema_name '' model_release
     *
     * @return
     */
    public Map<String, SchemaDescriptor> getAllSchemas() {
        return allSchemas;
    }

    /**
     * Method sets map of schemas keyed by schema_id
     * <p>
     * A schema_id is formed by concatenating: model_publisher '' schema_name '' model_release
     *
     * @param allSchemas
     */
    public void setAllSchemas(Map<String, SchemaDescriptor> allSchemas) {
        this.allSchemas = allSchemas;
    }

    public void addSchema(SchemaDescriptor schemaDescriptor) {
        this.allSchemas.put(schemaDescriptor.getSchemaId(), schemaDescriptor);
    }

    /**
     * Returns schema descriptor with given schema ID
     *
     * @param aSchemaId
     * @return
     */
    public SchemaDescriptor getSchema(String aSchemaId) {
        return this.allSchemas.get(aSchemaId);
    }

    /**
     * Method returns map of BmmModels keyed by logical model id.
     * <p>
     * A logical model id is keyed by: model_publisher '_' model_name
     *
     * @return
     */
    public Map<String, BmmModel> getValidModels() {
        return validModels;
    }

    /**
     * Method sets map of BmmModels keyed by logical model id.
     * <p>
     * A logical model id is keyed by: model_publisher '_' model_name
     *
     * @param validModels
     */
    public void setValidModels(Map<String, BmmModel> validModels) {
        this.validModels = validModels;
    }


    public void addValidModel(BmmModel schema) {
        this.validModels.put(schema.getSchemaId(), schema);
    }

    public int getNumberOfTries() {
        return numberOfTries;
    }

    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }

    /**
     * Initialise with all schemas found in the directories listed in 'aSchemaDirectories'
     *
     * @param aSchemaDirectories
     */
    public void initializeAll(List<String> aSchemaDirectories) {
        initializeWithLoadList(aSchemaDirectories, new ArrayList<String>());
    }

    /**
     * Initialise with a specific schema load list, usually a sub-set of schemas that will be found in the directory `an_absolute_dir'.
     */
    public void initializeWithLoadList(List<String> aSchemaDirectories, List<String> aSchemaLoadList) {
        this.schemaDirectories = aSchemaDirectories;
        this.schemaLoadList.addAll(aSchemaLoadList);
        reloadSchemas();
    }


    /**
     * Method clears model cache and reloads all schemas located in the schema directories (files with .bmm extensions only)
     */
    public void reloadSchemas() {
        resetValidator();
        loadSchemaDescriptors();
        if (validator.hasNoErrors()) {
            //log an error here
        }
        loadSchemas();
    }

    public void resetValidator() {
        validator.reset();
    }

    /**
     * Initialise `rm_schema_metadata_table' by finding all the schema files in the directory tree of `schema_directory'
     * and for each one, doing a fast parse to obtain the descriptive meta-data found in the first few lines
     */
    protected void loadSchemaDescriptors() {
        try {
            if (hasSchemaDirectory() && !exceptionEncountered) {
                allSchemas.clear();
                for(String directory:schemaDirectories) {
                    File directoryFile = new File(directory);
                    if (!directoryFile.exists() || !directoryFile.canRead()) {
                        validator.addError(BmmMessageIds.ec_bmm_schema_dir_not_valid, directory);
                    } else if (!directoryFile.isDirectory()) {
                        //it's a direct path to a file instead of a directory. Just parse it
                        processSchemaFile(directoryFile);
                    } else if (!(directoryFile.listFiles().length > 0)) {
                        validator.addError(BmmMessageIds.ec_bmm_schema_dir_contains_no_schemas, directory);
                    } else {
                        Collection<File> bmmFiles = FileUtils.listFiles(directoryFile, bmmFileFilter, TrueFileFilter.INSTANCE);
                        for (File bmmFile : bmmFiles) {
                            if (!bmmFile.isDirectory()) {
                                try {
                                    processSchemaFile(bmmFile);
                                } catch (Exception e) {
                                    validator.addError(BmmMessageIds.ec_bmm_schema_load_error, e.getMessage());
                                    log.error("error loading schema {}", bmmFile.getName(), e);
                                }
                            }
                            if (allSchemas.isEmpty()) {
                                validator.addError(BmmMessageIds.ec_bmm_schema_dir_contains_no_schemas, directory);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            exceptionEncountered = true;
            validator.addError(BmmMessageIds.ec_bmm_schema_unknown_exception);
            log.error("unknown exception loading schema descriptors", e);
        }
    }

    public void addSchemaInputStream(InputStream inputStream, String fileName) {
        log.info("loading {}", fileName);
        SchemaDescriptor schemaDescriptor = new SchemaDescriptor(inputStream, fileName);
        processSchemaDescriptor(schemaDescriptor);

    }

    protected void processSchemaFile(File bmmFile) {
        log.info("loading {}", bmmFile);
        SchemaDescriptor schemaDescriptor = new SchemaDescriptor(bmmFile);
        processSchemaDescriptor(schemaDescriptor);
    }

    private void processSchemaDescriptor(SchemaDescriptor schemaDescriptor) {
        schemaDescriptor.initialize();
        //check for two schema files purporting to be the exact same schema (including release)
        if (schemaDescriptor.getSchemaValidator().hasErrors()) {
            validator.addError(BmmMessageIds.ec_bmm_schema_load_failure,
                    schemaDescriptor.getSchemaId(),
                    schemaDescriptor.getSchemaValidator().getErrorStrings());
            log.warn("No schema descriptor created for " + schemaDescriptor.getSchemaPath() + ": Error loading schema");
        } else if (allSchemas.containsKey(schemaDescriptor.getSchemaId())) {
            validator.addWarning(BmmMessageIds.ec_bmm_schema_duplicate_schema_found, schemaDescriptor.getSchemaId(), schemaDescriptor.getSchemaPath());
            log.warn("No schema descriptor created for " + schemaDescriptor.getSchemaPath() + ": Duplicate schema");
        } else {
            addSchema(schemaDescriptor);
        }
    }

    /**
     * Creates the load list by order of dependency. Adds a validation error if
     * allSchemas does not contain a required dependency.
     */
    protected void populateLoadListInDependencyOrder() {
        List<String> remaining = new ArrayList<>();
        remaining.addAll(allSchemas.keySet());
        int count = 0;
        while (remaining.size() > 0 && count < numberOfTries) {
            allSchemas.forEach((aSchemaId, aSchemaDescriptor) -> {
                if (remaining.contains(aSchemaId)) {
                    PersistedBmmSchema currentSchema = aSchemaDescriptor.getPersistentSchema();
                    List<String> includes = new ArrayList<>(currentSchema.getIncludes().keySet());
                    if (includes == null || includes.size() == 0) {
                        schemaLoadList.add(aSchemaId);
                        remaining.remove(aSchemaId);
                    } else {
                        boolean allIncludesFound = true;
                        for(int i = 0; i < includes.size(); i++) {
                            String include = includes.get(i);
                            if(!schemaLoadList.contains(include.toLowerCase())) {
                                allIncludesFound = false;
                                break;
                            }
                        }
                        if(allIncludesFound) {
                            schemaLoadList.add(aSchemaId);
                            remaining.remove(aSchemaId);
                        }
                    }
                }
            });
            count++;
        }
    }

    /**
     * Populate the rm_schemas table by reading in schemas either specified in the 'rm_schemas_load_list'
     * config variable, or by reading all schemas found in the schema directory
     */
    public void loadSchemas() {
        try {
            //Populate the RM schema table first
            validModels.clear();
            topLevelSchemasByPublisher.clear();
            schemaInclusionMap.clear();
            candidateSchemas.clear();

            if (!allSchemas.isEmpty()) {
                //Reset all schema error logs
                allSchemas.forEach((schemaIdentifier, schemaDescriptor) -> {
                    schemaDescriptor.getSchemaValidator().reset();
                });
            }

            //set list of schemas to load; used later to determine what to put in `top_level_schemas'
            List<String> itemsToRemove = new ArrayList<String>();
            if (!schemaLoadList.isEmpty()) {
                for (String listItem : schemaLoadList) {
                    if (!allSchemas.containsKey(listItem)) {
                        validator.addWarning(BmmMessageIds.ec_bmm_schema_invalid_load_list, listItem);
                        itemsToRemove.add(listItem);
                    }
                }
                schemaLoadList.removeAll(itemsToRemove);
            } else {
                schemaLoadList = new ArrayList<>();
                populateLoadListInDependencyOrder();
                validator.addWarning(BmmMessageIds.ec_bmm_schemas_no_load_list_found);
            }

            //initial load of all schemas, which populates `schema_inclusion_map';
            schemaLoadList.forEach((aSchemaId) -> {
                SchemaDescriptor aSchemaDescriptor = allSchemas.get(aSchemaId);
                if (aSchemaDescriptor.getSchemaValidator().hasPassed()) {
                    loadSchemaIncludeClosure(aSchemaId);
                    if (aSchemaDescriptor.getSchemaValidator().getMessageLogger().hasWarnings()) {
                        validator.addWarning(BmmMessageIds.ec_bmm_schema_passed_with_warnings, aSchemaDescriptor.getSchemaValidator().getErrorStrings());
                    }

                } else {
                    validator.addError(BmmMessageIds.ec_bmm_schema_basic_validation_failed, aSchemaId, aSchemaDescriptor.getSchemaValidator().getErrorStrings());

                    if (!aSchemaDescriptor.isBmmCompatible()) {
                        validator.addError(BmmMessageIds.ec_bmm_schema_version_incompatible_with_tool,
                                aSchemaId,
                                BmmDefinitions.BMM_INTERNAL_VERSION);
                    }
                }
            });

            //propagate errors found so far
            //Also here: mark the 'top-level' schemas, inferred from the inclusion maps in each schema
            allSchemas.forEach((aSchemaId, aSchemaDescriptor) -> {
                if (!aSchemaDescriptor.getSchemaValidator().hasPassed()) {
                    mergeValidationErrors(aSchemaDescriptor);
                }
                if (!schemaInclusionMap.containsKey(aSchemaDescriptor.getSchemaId())) {
                    aSchemaDescriptor.setTopLevel(true);
                }
            });

            //Copy all SCHEMA_DESCRIPTORs validated to this point to `validated_schemas'
            //we do this in a separate pass, because each iteration of the previous loop can result in a closure
            //of schemas being loaded
            allSchemas.forEach((aSchemaId, aSchemaDescriptor) -> {
                if (aSchemaDescriptor.getSchemaValidator().hasPassed()) {
                    candidateSchemas.put(aSchemaId, aSchemaDescriptor);
                }
            });

            boolean finished = false;
            //Now we process the include relations on the P_BMM top-level schemas, creating fully populated schemas
            for (int index = 0; index < MAXIMUM_INCLUSION_DEPTH && !finished; index++) {
                finished = true;
                for (String key : schemaInclusionMap.keySet()) {
                    List<String> includeList = schemaInclusionMap.get(key);
                    if (candidateSchemas.containsKey(key)) {
                        PersistedBmmSchema includedSchema = candidateSchemas.get(key).getPersistentSchema();
                        //only process current schema if its lower level includes have already been copied into it,
                        //or if it had no includes, since only then is it ready to be itself included in the next one up the chain
                        //If this included schema is in this state, merge its contents into each schema that includes it
                        if (includedSchema.getState() == PersistedBmmSchemaState.STATE_INCLUDES_PROCESSED) {
                            //Iterate over the schemas that include `included_schema' and process the inclusion
                            for (String includeItem : includeList) {
                                if (candidateSchemas.containsKey(includeItem)) {
                                    PersistedBmmSchema includingSchema = candidateSchemas.get(includeItem).getPersistentSchema();
                                    if (includingSchema.getState() == PersistedBmmSchemaState.STATE_INCLUDES_PENDING) {
                                        includingSchema.merge(includedSchema);
                                        includingSchema.caseInsensitiveIncludeRemoval(key);
                                        if(includingSchema.getIncludes().size() == 0) {
                                            includingSchema.setState(PersistedBmmSchemaState.STATE_INCLUDES_PROCESSED);
                                        }
                                        validator.addInfo(BmmMessageIds.ec_bmm_schema_merged_schema,
                                                includedSchema.getSchemaId(),
                                                candidateSchemas.get(includeItem).getSchemaId());
                                        finished = false;
                                    }
                                } else {
                                    validator.addError(BmmMessageIds.ec_bmm_schema_including_schema_not_valid, includeItem);
                                }
                            }
                        }
                    } else {
                        validator.addError(BmmMessageIds.ec_bmm_schema_included_schema_not_found, key);
                    }
                }
            }

            //By this point the P_BMM schemas have been merged, and the top-level P_BMM schemas can be validated
            //This will cause each schema to potentially create errors to do with included schemas as well as itself
            //These errors then need to be integrated with the original schemas, so as to be reported correctly
            BmmModel topLevelSchema = null;
            for (String aKey : candidateSchemas.keySet()) {
                SchemaDescriptor aSchemaDescriptor = candidateSchemas.get(aKey);
                if (aSchemaDescriptor.isTopLevel() && schemaLoadList.contains(aSchemaDescriptor.getSchemaId())) {
                    if (aSchemaDescriptor.getSchemaValidator().hasPassed() && aSchemaDescriptor.getPersistentSchema().getState() == PersistedBmmSchemaState.STATE_INCLUDES_PROCESSED) {
                        try {
                            //validate the schema & if passed, put it into `top_level_schemas'
                            aSchemaDescriptor.validate();
                            mergeValidationErrors(aSchemaDescriptor);
                            if (aSchemaDescriptor.getSchemaValidator().hasPassed()) {
                                //now we create a BMM_SCHEMA from a fully merged P_BMM_SCHEMA
                                aSchemaDescriptor.createSchema();
                                if (aSchemaDescriptor.getSchema() != null) {
                                    topLevelSchema = aSchemaDescriptor.getSchema();
                                }
                                validModels.put(aSchemaDescriptor.getSchemaId(), topLevelSchema);
                                if (aSchemaDescriptor.getSchemaValidator().getMessageLogger().hasWarnings()) {
                                    validator.addWarning(BmmMessageIds.ec_bmm_schema_passed_with_warnings,
                                            aSchemaDescriptor.getSchemaId(),
                                            aSchemaDescriptor.getSchemaValidator().getErrorStrings());
                                }
                            } else {
                                validator.addError(BmmMessageIds.ec_bmm_schema_post_merge_validate_fail,
                                        aSchemaDescriptor.getSchemaId(),
                                        aSchemaDescriptor.getSchemaValidator().getErrorStrings());
                            }
                        } catch (Exception e) {
                            validator.addError(BmmMessageIds.ec_bmm_schema_post_merge_validate_fail, aSchemaDescriptor.getSchemaId(), e.getClass().getSimpleName() + ": " + e.getMessage());
                            log.error("error validating schema", e);
                        }
                    }
                }
            }

            //now populate the `models_by_closure' table
            modelsByClosure.clear();
            List<String> rmClosures = new ArrayList<>();
            String modelPublisher = null;
            for (String aSchemaId : validModels.keySet()) {
                BmmModel model = validModels.get(aSchemaId);
                modelPublisher = model.getRmPublisher();
                //put a ref to schema, keyed by the model_publisher-package_name key (lower-case) for later lookup by compiler
                rmClosures = model.getArchetypeRmClosurePackages();
                for (String rmClosure : rmClosures) {
                    String qualifiedRmClosureName = BmmDefinitions.publisherQualifiedRmClosureName(modelPublisher, rmClosure);
                    if (modelsByClosure.containsKey(qualifiedRmClosureName)) {
                        BmmModel schema = modelsByClosure.get(qualifiedRmClosureName);
                        validator.addInfo(BmmMessageIds.ec_bmm_schema_duplicate_found,
                                qualifiedRmClosureName,
                                schema.getSchemaId(),
                                aSchemaId);
                    } else {
                        addModelForClosure(qualifiedRmClosureName, model);
                    }
                }
            }

            //add entry to top_level_schemas_by_publisher
            List<SchemaDescriptor> publisherSchemas = null;
            for (String aSchemaId : allSchemas.keySet()) {
                SchemaDescriptor aSchemaDescriptor = allSchemas.get(aSchemaId);
                if (aSchemaDescriptor.isTopLevel()) {
                    modelPublisher = aSchemaDescriptor.getMetadata().get(BmmDefinitions.METADATA_RM_PUBLISHER);
                    if (!topLevelSchemasByPublisher.containsKey(modelPublisher)) {
                        publisherSchemas = new ArrayList<>();
                        topLevelSchemasByPublisher.put(modelPublisher, publisherSchemas);
                    } else {
                        publisherSchemas = topLevelSchemasByPublisher.get(modelPublisher);
                    }
                    publisherSchemas.add(aSchemaDescriptor);
                }
            }

            loadCount += 1;
        } catch (Exception e) {
            exceptionEncountered = true;
            validator.addError(BmmMessageIds.ec_bmm_schema_assertion_violation, e.getClass() + ": " + e.getMessage());

            e.printStackTrace();
        }
    }

    /**
     * Process the include directives for a given schema & build the `schema_inclusion_map' reverse reference table
     *
     * @param aSchemaId
     */
    public void loadSchemaIncludeClosure(String aSchemaId) {
        Map<String, BmmIncludeSpecification> includes;
        List<String> includers;
        SchemaDescriptor targetSchema = allSchemas.get(aSchemaId);
        if (targetSchema == null) {
            validator.addError(BmmMessageIds.ec_bmm_schema_load_failure, aSchemaId);
        } else {
            targetSchema.load();
            if (targetSchema.getSchemaValidator().hasPassed()) {
                targetSchema.validateIncludes(new ArrayList(allSchemas.keySet()));
                if (targetSchema.getSchemaValidator().hasPassed() && targetSchema.getPersistentSchema() != null) {
                    PersistedBmmSchema schema = targetSchema.getPersistentSchema();
                    validator.addInfo(BmmMessageIds.ec_bmm_schema_info_loaded,
                            aSchemaId,
                            "" + schema.getPrimitives().size(),
                            "" + schema.getClassDefinitions().size());
                    includes = schema.getIncludes();
                    if (!includes.isEmpty()) {
                        for (String key : includes.keySet()) {
                            if (!schemaInclusionMap.containsKey(includes.get(key).getId())) {
                                includers = new ArrayList<>();
                                schemaInclusionMap.put(includes.get(key).getId(), includers);
                            }
                            schemaInclusionMap.get(includes.get(key).getId()).add(aSchemaId);
                            if (!allSchemas.containsKey(includes.get(key).getId())) {
                                loadSchemaIncludeClosure(includes.get(key).getId());
                            }
                        }
                    }
                } else {
                    validator.addError(BmmMessageIds.ec_bmm_schema_includes_valiidation_failed,
                            aSchemaId,
                            targetSchema.getSchemaValidator().getErrorStrings());
                }
            } else {
                validator.addError(BmmMessageIds.ec_bmm_schema_load_failure,
                        aSchemaId,
                        targetSchema.getSchemaValidator().getErrorStrings());
            }
        }
    }

    /**
     * Merges all errors recorded during validation of `sd' - this includes errors that
     * may be for included schemas, so we use the inclusion map to mark all schemas
     * up the hierarchy with the knock-on effect of these errors.
     *
     * @param aSchemaDescriptor
     */
    public void mergeValidationErrors(SchemaDescriptor aSchemaDescriptor) {
        Map<String, MessageLogger> errorTable = null;
        Boolean errorsToPropagate = true;
        //SchemaDescriptor targetSchemaDescriptor, clientSchemaDescriptor = null;
        if (aSchemaDescriptor.getPersistentSchema() != null) {
            PersistedBmmSchema persistedSchema = aSchemaDescriptor.getPersistentSchema();
            errorTable = persistedSchema.getBmmSchemaValidator().getSchemaErrorTableCache();
            errorTable.forEach((aSchemaId, anErrorAccumulator) -> {
                SchemaDescriptor schemaDescriptor = allSchemas.get(aSchemaId);
                schemaDescriptor.getSchemaValidator().mergeErrors(anErrorAccumulator);
                //iterate through all schemas including err_table.key_for_iteration, except for `sd' since it will already have been marked
                //Note that there will be an entry in err_table for warnings as well as errors, so we have to process these properly
                if (schemaInclusionMap.containsKey(aSchemaId)) {
                    List<String> includes = schemaInclusionMap.get(aSchemaId);
                    //TODO Verify logic works
                    for (String include : includes) {
                        if (anErrorAccumulator.hasErrors()) {
                            schemaDescriptor.getSchemaValidator().addError(BmmMessageIds.ec_BMM_INCERR,
                                    include,
                                    aSchemaId);
                        } else {
                            schemaDescriptor.getSchemaValidator().addWarning(BmmMessageIds.ec_BMM_INCWARN,
                                    include,
                                    aSchemaId);
                        }
                    }
                }
            });
        }

        //propagate a BMM_INCERR or BMM_INCWARN to all schemas in the inclusion hierarchy from source schemas
        while (errorsToPropagate) {
            errorsToPropagate = false;
            for (String includedSchemaId : schemaInclusionMap.keySet()) {
                List<String> includingSchemaIds = schemaInclusionMap.get(includedSchemaId);
                SchemaDescriptor includedSchemaDescriptor = allSchemas.get(includedSchemaId);//TODO Make sure it is okay to make this local to lambda
                if (includedSchemaDescriptor == null) {//Added by cjkn to handle bad includes.
                    //Include does not exist
                    for (String includingSchemaId : includingSchemaIds) {
                        SchemaDescriptor includingSchemaDescriptor = allSchemas.get(includingSchemaId);
                        includingSchemaDescriptor.getSchemaValidator().addError(BmmMessageIds.ec_bmm_schema_included_schema_not_found,
                                includingSchemaId,
                                includedSchemaId);
                    }
                } else if (!includedSchemaDescriptor.getSchemaValidator().hasPassed() || includedSchemaDescriptor.getSchemaValidator().getMessageLogger().hasWarnings()) {
                    for (String include : includingSchemaIds) {
                        SchemaDescriptor clientSchemaDescriptor = allSchemas.get(include);
                        if (!clientSchemaDescriptor.getSchemaValidator().hasPassed() && clientSchemaDescriptor.getSchemaValidator().getMessageLogger().hasWarnings()) {
                            if (!includedSchemaDescriptor.getSchemaValidator().hasPassed()) {
                                clientSchemaDescriptor.getSchemaValidator().addError(BmmMessageIds.ec_BMM_INCERR,
                                        include,
                                        includedSchemaId);
                            } else {
                                clientSchemaDescriptor.getSchemaValidator().addWarning(BmmMessageIds.ec_BMM_INCWARN,
                                        include,
                                        includedSchemaId);
                            }
                            errorsToPropagate = true;
                        }
                    }
                }
            }
        }

    }

    /**
     * Initializes all schema based on schema directory list provided.
     */
    /*
    public void initializeAll() {
        clearCache();
        if (schemaDirectories == null || schemaDirectories.size() <= 0) {
            throw new RuntimeException("Error initializing schemas. No schema directory set.");
        } else {
            Map<String, BmmIncludeSpecification> allIncludes = loadAllPersistedModels();
            Queue<PersistedBmmSchema> queue = new LinkedList<>();
            Map<String, PersistedBmmSchema> schemasToProcess = new LinkedHashMap<>();
            candidateSchemas.forEach((id, persistedBmmSchema) -> {
                queue.add(persistedBmmSchema);
            });
            int tries = 0;
            int attempts = 100;//TODO Figure out better way to break on cycles in directed cyclic graph or when non-resolvable dependencies exist
            while (queue.size() > 0) {
                PersistedBmmSchema persistedBmmSchema = queue.remove();
                Map<String, BmmIncludeSpecification> includeMap = persistedBmmSchema.getIncludes();
                if (includeMap == null || includeMap.size() == 0) {
                    schemasToProcess.put(persistedBmmSchema.generateSchemaIdentifier().toUpperCase(), persistedBmmSchema);
                } else {
                    final DataType<Boolean> allResolved = new DataType(true);
                    for (String includeId : includeMap.keySet()) {
                        if (schemasToProcess.get(includeId.toUpperCase()) == null) {
                            allResolved.setValue(false);
                            queue.add(persistedBmmSchema);//I have an unresolved dependency so add me back to the queue.
                            break;
                        }
                    }
                    if (allResolved.getValue()) {
                        schemasToProcess.put(persistedBmmSchema.generateSchemaIdentifier().toUpperCase(), persistedBmmSchema);
                    }
                    if (tries >= attempts) {
                        //throw new RuntimeException("Unresolvable includes " + queue.toString());
                        break;
                    }
                    tries++;
                }
            }
            schemasToProcess.forEach((id, persistedBmmModel) -> {
                processSchema(persistedBmmModel);
            });
            System.out.println("Processing completed");
        }
    }

    public Map<String, BmmIncludeSpecification> loadAllPersistedModels() {
        Map<String, BmmIncludeSpecification> allIncludes = new HashMap<>();
        clearCache();
        if (schemaDirectories == null || schemaDirectories.size() <= 0) {
            throw new RuntimeException("Error initializing schemas. No schema directory set.");
        } else {
            List<File> bmmFiles = FileAndDirUtils.loadFilesWithExtensionFromDirectories(getSchemaDirectories(), ".bmm", false);
            bmmFiles.forEach(file -> {
                PersistedBmmSchema persistedBmmSchema = loadPersistedModel(file);
                allIncludes.putAll(persistedBmmSchema.getIncludes());
                candidateSchemas.put(persistedBmmSchema.generateSchemaIdentifier().toUpperCase(), persistedBmmSchema);
            });
        }
        return allIncludes;
    }*/

//    public PersistedBmmSchema loadPersistedModel(File file) {
//        OdinLoaderImpl loader = new OdinLoaderImpl();
//        OdinVisitorImpl visitor = loader.loadOdinFile(file.getAbsolutePath());
//        CompositeOdinObject root = visitor.getAstRootNode();
//        BmmSchemaDeserializer deserializer = new BmmSchemaDeserializer();
//        return deserializer.deserialize(root);
//    }

    /*protected void processSchema(PersistedBmmSchema persistedBmmSchema) {
        String schemaId = persistedBmmSchema.generateSchemaIdentifier();
        if (validModels.get(schemaId.toUpperCase()) == null) {
            handleMerge(persistedBmmSchema);
            persistedBmmSchema.createBmmSchema();
            //TODO Add validation here
            persistedBmmSchema.validate();
            BmmModel model = persistedBmmSchema.getBmmModel();
            if (model != null) {
                validModels.put(schemaId.toUpperCase(), model);//TODO Figure out how to handle nested bmm files
            } else {
                throw new RuntimeException("Invalid model. Model cannot be null");
            }
        }
    }

    protected void handleMerge(PersistedBmmSchema mergeTarget) {
        Map<String, Integer> includeReferenceFrequency = new HashMap<>();
        calculateIncludeReferenceFrequencies(includeReferenceFrequency, mergeTarget);
        mergeTarget.getIncludes().forEach((id, include) -> {
            if (includeReferenceFrequency.get(id.toUpperCase()) == 1) {//Only merge models that have not been already merged in other include dependencies
                PersistedBmmSchema toBeMerged = candidateSchemas.get(id.toUpperCase());
                mergeTarget.merge(toBeMerged);
            }
        });
    }

    protected void calculateIncludeReferenceFrequencies(Map<String, Integer> includeReferenceFrequency, PersistedBmmSchema schema) {
        Map<String, BmmIncludeSpecification> includes = schema.getIncludes();
        includes.keySet().forEach(id -> {
            if (includeReferenceFrequency.get(id) == null) {
                includeReferenceFrequency.put(id, 1);
            } else {
                includeReferenceFrequency.put(id, includeReferenceFrequency.get(id) + 1);
            }
            calculateIncludeReferenceFrequencies(includeReferenceFrequency, candidateSchemas.get(id.toUpperCase()));
        });
    }*/

    /**
     * Method removes all cached schemas.
     */
    /*public void clearCache() {
        if (validModels == null) {
            validModels = new HashMap<>();
        } else {
            validModels.clear();
        }
        if (candidateSchemas == null) {
            candidateSchemas = new LinkedHashMap<>();
        } else {
            candidateSchemas.clear();
        }
    }*/
}