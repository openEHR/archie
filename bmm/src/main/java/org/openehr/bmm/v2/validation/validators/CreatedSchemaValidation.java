package org.openehr.bmm.v2.validation.validators;

import org.apache.commons.lang3.StringUtils;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidation;
import org.openehr.bmm.v2.validation.BmmValidationResult;
import org.openehr.utils.message.MessageLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreatedSchemaValidation implements BmmValidation {
    @Override
    public void validate(BmmValidationResult validationResult, BmmRepository repository, MessageLogger logger, PBmmSchema schema) {
        List<String> packageNames = new ArrayList<>(schema.getPackages().keySet());

        //check top-level names - package names cannot contain each other and be siblings
        schema.getPackages().keySet().forEach(name1 -> {
            List<String> invalidSiblings = packageNames.stream().filter(name2 ->
                    (!name1.equalsIgnoreCase(name2)) && (name1.startsWith(name2 + ".") || name2.startsWith(name1 + "."))
            ).collect(Collectors.toList());
            if (!invalidSiblings.isEmpty()) {
                logger.addError(BmmMessageIds.EC_ILLEGAL_TOP_LEVEL_SIBLING_PACKAGES, schema.getSchemaId());
            }
        });

        //duplicate properties don't have to be checked - Jackson already does this

        //validate package & class structure
        schema.doRecursivePackages(persistedBmmPackage -> {
            //check for lower-down qualified names
            if ((!schema.getPackages().containsKey(persistedBmmPackage.getName())) && persistedBmmPackage.getName().indexOf(BmmDefinitions.PACKAGE_NAME_DELIMITER) >= 0) {
                logger.addError(BmmMessageIds.EC_ILLEGAL_QUALIFIED_PACKAGE_NAME,
                        schema.getSchemaId(),
                        persistedBmmPackage.getName());
            }
            for (String persistedBmmClass: persistedBmmPackage.getClasses()) {
                if (StringUtils.isEmpty(persistedBmmClass)) {
                    logger.addError(BmmMessageIds.ec_BMM_class_name_empty,
                            schema.getSchemaId(),
                            persistedBmmPackage.getName());
                }
                else if(!schema.hasClassOrPrimitiveDefinition(persistedBmmClass)) {
                    logger.addError(BmmMessageIds.ec_BMM_class_not_in_definitions,
                            schema.getSchemaId(),
                            persistedBmmClass,
                            persistedBmmPackage.getName());
                }
            }
        });

        if (!logger.hasErrors()) {
            logger.addInfo(BmmMessageIds.SCHEMA_CREATED,schema.getSchemaId(),
                    ""+schema.getPrimitiveTypes().size(),
                    ""+schema.getClassDefinitions().size());

        }
    }

}
