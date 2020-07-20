package org.openehr.bmm.v2.validation;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.persistence.PBmmPackage;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.validation.converters.CanonicalPackagesGenerator;
import org.openehr.bmm.v2.validation.converters.BmmModelCreator;
import org.openehr.bmm.v2.validation.converters.DescendantsCalculator;
import org.openehr.bmm.v2.validation.converters.IncludesProcessor;
import org.openehr.bmm.v2.validation.converters.PreprocessPersistedSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converts a Persisted BMM Schema into a computable BMM Model. Validates the schema along the way. Returns a
 * BmmValidationResult, which contains the source PBmmSchema, the converted BmmModel, plus any validation info, warning
 * and error messages from the validation and conversion process.
 * This does not change the PBmmSchema objects, unlike the Eiffel implementation and the P_BMM specifications,
 * all state information is stored in the BmmValidationResult instead.
 *
 * One BmmSchemaConverter can be reused for conversion of multiple schemas. Not thread-safe.
 */
public class BmmSchemaConverter {

    private static final Logger logger = LoggerFactory.getLogger(BmmSchemaConverter.class);

    private final BmmRepository repository;
    /**
     * package structure in which all top-level qualified package names like xx.yy.zz have been
     * expanded out to a hierarchy of BMM_PACKAGE objects
     */
    private BmmSchemaValidator schemaValidator;

    /**
     * Create the BmmSchemaConverter for the given BmmRepository. The given BmmRepository will be used as the source
     * for any included schemas
     * @param repository The BmmRepository for use as a source of included Bmm schemas
     */
    public BmmSchemaConverter(BmmRepository repository) {
        this.repository = repository;
        schemaValidator = new BmmSchemaValidator(repository);
    }

    /**
     * validate the P_BMM schema. If validation succeeds, convert the schema to a BMM Model.
     * Side effects: will add the BmmValidationResult to the repository. Will also add the BmmValidationResult of all
     * included BmmValidationResult to the repository, recursively
     * @param schema the P_BMM schema to convert.
     * @return the BmmValidationResult that contains the origina PBmmSchema, any validation results, and if validation
     *          succeeded, the converted BmmModel.
     */
    public BmmValidationResult validateConvertAndAddToRepo(PBmmSchema schema) {
        //clear error messages
        schemaValidator = new BmmSchemaValidator(repository);

        logger.info("loading " + schema.getSchemaId());

        BmmValidationResult result = new BmmValidationResult();
        result.setLogger(schemaValidator.getLogger());
        result.setSchemaId(schema.getSchemaId());
        result.setOriginalSchema(schema);
        try {
            //Step 1: run validations that can be run on the persisted file format
            //validate some basics of the schema: check that everything defined in tje packages part appears in classes
            //and the other way around
            schemaValidator.validateCreated(result, schema);
            schemaValidator.checkNoExceptions();

            //Check that this P_BMM file has a version compatible with this library
            schemaValidator.validateBmmVersion(result, schema);
            schemaValidator.checkNoExceptions();
            //validate that includes exist
            schemaValidator.validateIncludes(result, schema);
            schemaValidator.checkNoExceptions();

            //convert some maps into case insensitive ones, to make some lookups case insensitive
            //this keeps the original casing, just the lookup is different
            new PreprocessPersistedSchema().preprocess(schema);

            //step 2: create canonical packages (org.openehr.ehr becomes org -> openehr -> ehr)
            Map<String, PBmmPackage> canonicalPackages = new CanonicalPackagesGenerator().generateCanonicalPackages(schema);
            result.setCanonicalPackages(canonicalPackages);

            //and process included schemas, storing a clone of the original schema in the BmmValidationResult
            //this step also validates and converts any included schemas, storing the result in the repository
            new IncludesProcessor().cloneSchemaAndAddIncludes(result, repository, schemaValidator.getLogger());
            schemaValidator.checkNoExceptions();

            //step 3: validate the merged schema, including the includes
            schemaValidator.validateSchemaAfterMergeOfIncludes(result);
            schemaValidator.checkNoExceptions();

            //step 4: create model
            BmmModel bmmModel = new BmmModelCreator().create(result);

            schemaValidator.checkNoExceptions();

            // set the descendants and ancestors properties
            new DescendantsCalculator().calculateDescendants(bmmModel);
            result.setModel(bmmModel);

            //add the model and closure to the repository, but only if no errors
            createModelsByClosureAndVersion(result);

            return result;
        }
        catch (BmmSchemaValidationException ex) {
            //cannot continue on validation error
            return result;
        } catch (Exception e) {
            result.getLogger().addError(BmmMessageIds.ec_bmm_schema_conv_fail_err, schema.getSchemaName(), e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
            return result;
        }
        finally {
            //add the result to the repo, even in case of errors
            repository.addModel(result);
        }
    }

    /**
     * Convert and validate all schemas in the repository. Stores the results in the repository
     */
    public void validateAndConvertRepository() {
        for (PBmmSchema schema:repository.getPersistentSchemas())
            if (repository.getModel(schema.getSchemaId()) == null) {
                BmmValidationResult bmmValidationResult = validateConvertAndAddToRepo(schema);
            }
    }

    private void createModelsByClosureAndVersion(BmmValidationResult validationResult) {
        BmmModel model = validationResult.getModel();
        List<String> rmClosures = new ArrayList<>();
        String schemaId = model.getSchemaId();
        String modelPublisher = model.getRmPublisher();
        String modelName = model.getModelName();
        if (modelName != null)
            addClosure(schemaId, validationResult, modelPublisher, modelName);
        else
            //possibly old style BMM, test
            for (String closureName:model.getArchetypeRmClosurePackages())
                addClosure(schemaId, validationResult, modelPublisher, closureName);
    }

    private void addClosure(String schemaId, BmmValidationResult validationResult, String modelPublisher, String modelName) {
        String qualifiedRmClosureName = BmmDefinitions.publisherQualifiedRmClosureName(modelPublisher, modelName) + "_" + validationResult.getModel().getRmRelease();
        BmmValidationResult existingSchema = repository.getModelByClosure(qualifiedRmClosureName);
        if (existingSchema != null)
            schemaValidator.getLogger().addInfo(BmmMessageIds.ec_bmm_schema_duplicate_found,
                    qualifiedRmClosureName,
                    existingSchema.getSchemaId(),
                    schemaId);
        else
            repository.addModelByClosure(qualifiedRmClosureName, validationResult);
    }


}
