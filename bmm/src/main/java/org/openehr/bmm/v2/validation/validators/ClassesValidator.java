package org.openehr.bmm.v2.validation.validators;

import org.apache.commons.lang3.StringUtils;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.persistence.PBmmGenericParameter;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.persistence.PBmmClass;
import org.openehr.bmm.v2.persistence.PBmmProperty;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidation;
import org.openehr.bmm.v2.validation.BmmValidationResult;
import org.openehr.utils.message.MessageLogger;

public class ClassesValidator extends ValidatorBase implements BmmValidation {

    private BmmValidationResult validationResult;
    private BmmRepository repository;
    private PBmmSchema schema;
    private PropertyValidator propertyValidator;

    @Override
    public void validate (BmmValidationResult validationResult, BmmRepository repository, MessageLogger logger, PBmmSchema schema) {

        setLogger(logger);
        this.validationResult = validationResult;
        this.repository = repository;
        this.schema = schema;
        propertyValidator = new PropertyValidator(logger, schema);

        schema.doAllClasses( pBmmClass -> {
            validateClass(pBmmClass);
        });
    }

    public void validateClass(PBmmClass pBmmClass) {
        //check that all ancestors exist
        pBmmClass.getAncestorTypeNames().forEach (ancestorClassName -> {
            if (StringUtils.isEmpty (ancestorClassName)) {
                addValidityError(schema, pBmmClass.getSourceSchemaId(), BmmMessageIds.EC_ANCESTOR_NAME_EMPTY, pBmmClass.getSourceSchemaId(), pBmmClass.getName());
            } else if (!ancestorClassName.equalsIgnoreCase(BmmDefinitions.ANY_TYPE) && schema.getClassDefinition(BmmDefinitions.typeNameToClassKey(ancestorClassName)) == null) {
                addValidityError(schema, pBmmClass.getSourceSchemaId(), BmmMessageIds.EC_ANCESTOR_DOES_NOT_EXIST, pBmmClass.getSourceSchemaId(), pBmmClass.getName(), ancestorClassName);
            }
        });

        if (!logger.hasErrors()) {
            validateGenericParameters(pBmmClass);
        }

        // validate the properties
        for (PBmmProperty<?> property:pBmmClass.getProperties().values()) {
            propertyValidator.validateProperty(pBmmClass, property);
        }
    }

    private void validateGenericParameters(PBmmClass pBmmClass) {
        //check that all generic parameter.conforms_to_type exist exists
        if (pBmmClass.isGeneric()) {
            for (PBmmGenericParameter pBmmGenericParameter: pBmmClass.getGenericParameterDefs().values()) {
                String conformsToType = pBmmGenericParameter.getConformsToType();
                if (conformsToType != null && !schema.hasClassOrPrimitiveDefinition(conformsToType)) {
                    addValidityError(schema, pBmmClass.getSourceSchemaId(), BmmMessageIds.EC_GENERIC_PARAMETER_CONSTRAINT_DOES_NOT_EXIST,
                            pBmmClass.getSourceSchemaId(),
                            pBmmClass.getName(),
                            pBmmGenericParameter.getName(),
                            conformsToType);
                }
            }

        }

    }

}
