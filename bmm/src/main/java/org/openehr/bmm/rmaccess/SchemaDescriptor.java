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

import org.openehr.bmm.persistence.PersistedBmmSchema;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.persistence.deserializer.BmmSchemaDeserializer;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.persistence.validation.BmmSchemaValidator;
import org.openehr.odin.CompositeOdinObject;
import org.openehr.odin.antlr.OdinVisitorImpl;
import org.openehr.odin.loader.OdinLoaderImpl;
import org.openehr.odin.utils.OdinSerializationUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Descriptor for a BMM schema. Contains a meta-data table of attributes obtained from a mini-ODIN parse of the schema file.
 * Be sure to call initialize method after invoking the constructor.
 */
public class SchemaDescriptor {

    /**
     * Persistent form of model.
     */
    private PersistedBmmSchema persistentSchema;
    /**
     * Computable form of model.
     */
    private BmmModel schema;
    /**
     * Schema id, formed from:
     * <p>
     * meta_data(Metadata_model_publisher) '-' meta_data(metadata_schema_name) '-' meta_data(Metadata_model_release)
     * e.g. openehr_rm_1.0.3, openehr_test_1.0.1, iso_13606-1_2008.
     */
    private String schemaId;
    /**
     * Path to the schema
     */
    private String schemaPath;
    /**
     * Table of {key, value} pairs of schema meta-data, keys as follows:
     * <p>
     * "bmm_version",
     * "model_publisher",
     * "schema_name",
     * "model_release",
     * "schema_revision",
     * "schema_lifecycle_state",
     * "schema_description",
     * "schema_path"
     */
    private Map<String, String> metadata;
    /**
     * Schema_ids of schemas included by this schema.
     */
    private List<String> includes;
    /**
     * The version of the BMM schema
     */
    private String bmmVersion;
    /**
     * True if this is a top-level schema, i.e. not included by some other schema
     */
    private boolean isTopLevel;
    /**
     * True if the BMM version found in the schema (or assumed, if none) is compatible with that in this software
     */
    private boolean isBmmCompatible;
    /**
     * Schema file (to ODIN file). If this is null, inputStream must be set
     */
    private File schemaFile;
    /**
     * InputStream to BMM file;
     */
    private InputStream inputStream;
    /**
     * The BMM Validator for this schema descriptor
     */
    private BmmSchemaValidator schemaValidator;

    private boolean schemaAlreadyLoaded;

    public SchemaDescriptor() {
        this.schemaValidator = new BmmSchemaValidator();
        this.includes = new ArrayList<String>();
    }

    /**
     * Metadata constructor
     *
     * @param aMetadata
     */
    public SchemaDescriptor(Map<String, String> aMetadata) {
        this();
        this.metadata = aMetadata;
    }

    /**
     * File constructor
     *
     * @param bmmSchemaFile
     */
    public SchemaDescriptor(File bmmSchemaFile) {
        this();
        schemaFile = bmmSchemaFile;
    }

    /**
     * Constructor with an inputStream and a filename
     *
     * @param stream the input stream. must be closed by the caller!
     * @param fileName the file name of the BMM file. Used for easier error messages
     */
    public SchemaDescriptor(InputStream stream, String fileName) {
        this();
        this.inputStream = stream;
        //not a path, but a filename. Shouldn't matter at all
        this.schemaPath = fileName;
    }


    public void initialize() {
        if(schemaFile != null) {
            initializeWithSchemaFile();
        } else if(metadata != null) {
            initializeWithMetadata();
        } else if(inputStream != null) {
            initializeWithInputStream();
        } else {
            throw new IllegalStateException("Invalid schema descriptor state");
        }
    }

    public void initializeWithSchemaFile() {
        schemaPath = schemaFile.getAbsolutePath();
        load();
        schemaAlreadyLoaded = true;
        initializeFromLoadedSchema();
    }

    public void initializeWithInputStream() {
        load();
        schemaAlreadyLoaded = true;
        initializeFromLoadedSchema();
    }

    private void initializeFromLoadedSchema() {
        this.metadata = new HashMap<>();
        metadata.put(BmmDefinitions.METADATA_BMM_VERSION, persistentSchema.getBmmVersion());
        metadata.put(BmmDefinitions.METADATA_RM_PUBLISHER, persistentSchema.getRmPublisher());
        metadata.put(BmmDefinitions.METADATA_SCHEMA_NAME, persistentSchema.getSchemaName());
        metadata.put(BmmDefinitions.METADATA_RM_RELEASE, persistentSchema.getRmRelease());
        metadata.put(BmmDefinitions.METADATA_SCHEMA_LIFECYCLE_STATE, persistentSchema.getSchemaLifecycleState());
        metadata.put(BmmDefinitions.METADATA_SCHEMA_DESCRIPTION, persistentSchema.getSchemaDescription());
        metadata.put(BmmDefinitions.METADATA_SCHEMA_PATH, schemaPath);
        schemaId = BmmDefinitions.createSchemaId(persistentSchema.getRmPublisher(), persistentSchema.getSchemaName(), persistentSchema.getRmRelease());
        bmmVersion = persistentSchema.getBmmVersion();
        if(bmmVersion == null) {
            bmmVersion = BmmDefinitions.ASSUMED_BMM_VERSION;
        }
        validateBmmVersionCompatibility();
    }

    /**
     * Initializes this object and validates the metadata associated with this reference model.
     */
    public void initializeWithMetadata() {
        //To support compatibility with Eiffel code but not used in java-model-stack
        schemaValidator.reset();
        if (BmmDefinitions.isValidMetadata(metadata)) {
            schemaId = BmmDefinitions.createSchemaId(metadata.get(BmmDefinitions.METADATA_RM_PUBLISHER), metadata.get(BmmDefinitions.METADATA_SCHEMA_NAME), metadata.get(BmmDefinitions.METADATA_RM_RELEASE));
            if (metadata.containsKey(BmmDefinitions.METADATA_BMM_VERSION) && metadata.get(BmmDefinitions.METADATA_BMM_VERSION) != null) {
                bmmVersion = metadata.get(BmmDefinitions.METADATA_BMM_VERSION);
            } else {
                bmmVersion = BmmDefinitions.ASSUMED_BMM_VERSION;
            }
        } else {
            throw new IllegalStateException("Invalid metadata file associated with this SchemaDescriptor instance");
        }
        validateBmmVersionCompatibility();
        schemaPath = metadata.get(BmmDefinitions.METADATA_SCHEMA_PATH);
        schemaFile = new File(schemaPath);//Note that this is different to the Eiffel implementation
    }

    private void validateBmmVersionCompatibility() {
        isBmmCompatible = BmmDefinitions.isBmmVersionCompatible(bmmVersion);
        if (!isBmmCompatible) {
            schemaValidator.addError(BmmMessageIds.ec_BMM_VER,
                schemaId,
                bmmVersion,
                BmmDefinitions.BMM_INTERNAL_VERSION);
        }
    }

    /**
     * Returns persistent form of the model.
     *
     * @return
     */
    public PersistedBmmSchema getPersistentSchema() {
        return persistentSchema;
    }

    /**
     * Sets the persistent form of the model.
     *
     * @param persistentSchema
     */
    public void setPersistentSchema(PersistedBmmSchema persistentSchema) {
        this.persistentSchema = persistentSchema;
    }

    /**
     * Returns computable form of the model.
     *
     * @return
     */
    public BmmModel getSchema() {
        return schema;
    }

    /**
     * Sets the computable form of the model.
     *
     * @param schema
     */
    public void setSchema(BmmModel schema) {
        this.schema = schema;
    }

    /**
     * Returns the schema id.
     *
     * @return
     */
    public String getSchemaId() {
        return schemaId;
    }

    /**
     * Sets the schema id.
     *
     * @param schemaId
     */
    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    /**
     * Returns the path where this schema file can be found.
     *
     * @return
     */
    public String getSchemaPath() {
        return schemaPath;
    }

    /**
     * Sets the schema file path for this schema.
     *
     * @param schemaPath
     */
    public void setSchemaPath(String schemaPath) {
        this.schemaPath = schemaPath;
    }

    /**
     * Returns the file pointer to this schema in the file system.
     *
     * @return
     */
    public File getSchemaFile() {
        return schemaFile;
    }

    /**
     * Sets the schema file pointer for this schema in the file system.
     *
     * @param schemaFile
     */
    public void setSchemaFile(File schemaFile) {
        this.schemaFile = schemaFile;
    }

    /**
     * Returns the schema metadata map.
     *
     * @return
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Sets this schema metadata map.
     *
     * @param metadata
     */
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns Schema_ids of schemas included by this schema.
     *
     * @return
     */
    public List<String> getIncludes() {
        return includes;
    }

    /**
     * Sets Schema_ids of schemas included by this schema.
     *
     * @param includes
     */
    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    /**
     * Returns the BMM version used by this persisted schema. If not specified,
     * the default BMM version will be returned. It is recommended that the BMM version
     * always be specified.
     *
     *
     * @return
     */
    public String getBmmVersion() {
        return bmmVersion;
    }

    /**
     * Sets the BMM version used by the persisted schema.
     *
     * @param bmmVersion
     */
    public void setBmmVersion(String bmmVersion) {
        this.bmmVersion = bmmVersion;
    }

    /**
     * True if this is a top-level schema, i.e. not included by some other schema.
     *
     * @return
     */
    public boolean isTopLevel() {
        return isTopLevel;
    }

    /**
     * Set to indicate that this schema is a top-level schema (not included by any other schema)
     *
     * @param topLevel
     */
    public void setTopLevel(boolean topLevel) {
        isTopLevel = topLevel;
    }

    /**
     * True if the BMM version found in the schema (or assumed, if none) is compatible with that in this software.
     *
     * @return
     */
    public boolean isBmmCompatible() {
        return isBmmCompatible;
    }

    public void setBmmCompatible(boolean bmmCompatible) {
        isBmmCompatible = bmmCompatible;
    }

    /**
     * Returns the schema validator for this schema load and validation.
     *
     * @return
     */
    public BmmSchemaValidator getSchemaValidator() {
        return schemaValidator;
    }

    /**
     * Sets the schema validator for this schema load and validation
     *
     * @param schemaValidator
     */
    public void setSchemaValidator(BmmSchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }

    /**
     * Signals that a load error occurred.
     * TODO Not sure where this is actually used.
     *
     */
    public void signalLoadIncludeError() {
        schemaValidator.addError(BmmMessageIds.ec_bmm_schema_include_failed_to_load, schemaId);
    }

    /**
     * Load schema into in-memory form.
     */
    public void load() {
        if(!schemaAlreadyLoaded) {
            PersistedBmmSchema persistedBmmSchema = loadPersistedSchema();
            if (schemaValidator.hasNoErrors() && persistedBmmSchema != null) {
                schemaValidator.setSchema(persistedBmmSchema);
                persistedBmmSchema.setBmmSchemaValidator(schemaValidator);
                schemaValidator.validateCreated();
                if (schemaValidator.hasPassed()) {
                    persistedBmmSchema.loadFinalize();
                    persistentSchema = persistedBmmSchema;
                }
            }
        } else {
            schemaAlreadyLoaded = false;//reset
        }
    }

    /**
     * Validate entire schema.
     */
    public void validate() {
        persistentSchema.validate();
    }

    /**
     * Validate includes list for this schema, to see if each mentioned schema exists in read schemas.
     */
    public void validateIncludes(List<String> allSchemas) {
        if(persistentSchema.getIncludes() != null && persistentSchema.getIncludes().size() > 0) {
            persistentSchema.getIncludes().keySet().forEach(include -> {
                if(!allSchemas.contains(include.toLowerCase())) {
                    schemaValidator.addError(BmmMessageIds.ec_BMM_INC, schemaId, include);
               } else {
                    includes.add(include.toLowerCase());
                }
            });
        }
    }

    /**
     * Create `schema'.
     */
    public void createSchema() {
        if(schemaValidator.hasPassed()) {
            persistentSchema.createBmmSchema();
            schema = persistentSchema.getBmmModel();
        }
    }

    /**
     * Loads the persisted BMM schema from its ODIN file.
     *
     * @return
     */
    public PersistedBmmSchema loadPersistedSchema() {
        if(schemaFile != null) {
            return loadPersistedSchemaFromFile();
        } else {
            return loadPersistedSchemaFromInputStream();
        }

    }

    private PersistedBmmSchema loadPersistedSchemaFromFile() {
        PersistedBmmSchema retVal = null;
        if (schemaFile == null || !schemaFile.exists()) {
            schemaValidator.addError(BmmMessageIds.ec_object_file_not_valid,
                schemaFile.getName(),
                schemaFile.getAbsolutePath());
        } else {
            try {
                OdinLoaderImpl loader = new OdinLoaderImpl();
                OdinVisitorImpl visitor = loader.loadOdinFile(schemaFile.getAbsolutePath());
                CompositeOdinObject root = visitor.getAstRootNode();
                BmmSchemaDeserializer deserializer = new BmmSchemaDeserializer();
                retVal = deserializer.deserialize(root);
            } catch (Exception e) { //TODO May not be best way to handle this.
                schemaValidator.addError(BmmMessageIds.ec_bmm_schema_load_error,
                    schemaFile.getName(),
                    schemaFile.getAbsolutePath(),
                    e.getMessage());
                e.printStackTrace();
            }
        }
        return retVal;
    }

    private PersistedBmmSchema loadPersistedSchemaFromInputStream() {
        PersistedBmmSchema retVal = null;
        if (inputStream == null) {
            schemaValidator.addError(BmmMessageIds.ec_object_file_not_valid,
                    schemaPath, schemaPath);
        } else {
            try {
                OdinLoaderImpl loader = new OdinLoaderImpl();
                OdinVisitorImpl visitor = loader.loadOdinFile(inputStream);
                CompositeOdinObject root = visitor.getAstRootNode();
                BmmSchemaDeserializer deserializer = new BmmSchemaDeserializer();
                retVal = deserializer.deserialize(root);
            } catch (Exception e) { //TODO May not be best way to handle this.
                schemaValidator.addError(BmmMessageIds.ec_bmm_schema_load_error,
                        schemaFile.getName(),
                        schemaFile.getAbsolutePath(),
                        e.getMessage());
                e.printStackTrace();
            }
        }
        return retVal;
    }

    public String toString() {
        return schemaId;
    }
}
