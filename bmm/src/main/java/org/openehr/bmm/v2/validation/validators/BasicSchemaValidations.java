package org.openehr.bmm.v2.validation.validators;


import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.bmm.v2.validation.BmmValidation;
import org.openehr.bmm.v2.validation.BmmValidationResult;
import org.openehr.utils.message.MessageLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicSchemaValidations implements BmmValidation {

    @Override
    public void validate(BmmValidationResult validationResult, BmmRepository repository, MessageLogger logger, PBmmSchema schema) {
        //Check that RM shema release is valid
        if (!BmmDefinitions.isValidStandardVersion(schema.getRmRelease())) {
            logger.addError(BmmMessageIds.EC_RM_RELEASE_INVALID, schema.getSchemaId(), schema.getRmRelease());
        }

        Map<String, String> packageClassList = new HashMap<>();

        //1. check that no duplicate class names are found in packages
        validationResult.getCanonicalPackages().forEach((packageName, canonicalPackage) -> canonicalPackage.doRecursiveClasses((persistedBmmPackage, className) -> {
            String classNameStr = className.toLowerCase();
            if(packageClassList.containsKey(classNameStr)) {
                logger.addError(BmmMessageIds.EC_DUPLICATE_CLASS_IN_PACKAGES, schema.getSchemaId(), className, persistedBmmPackage.getName(), packageClassList.get(classNameStr));
            } else {
                packageClassList.put(classNameStr, persistedBmmPackage.getName());
            }
        }));

        List<String> classNameList = new ArrayList<>();
        //2. check that every class is in a package
        schema.doAllClasses( persistedBmmClass -> {
            String className = persistedBmmClass.getName().toLowerCase();
            if (!packageClassList.containsKey(className)) {
                logger.addError(BmmMessageIds.EC_CLASS_NOT_DECLARED_IN_PACKAGES, schema.getSchemaId(), persistedBmmClass.getName());
            } else if (classNameList.contains(className)) {
                logger.addError(BmmMessageIds.EC_DUPLICATE_CLASS_DEFINITION, schema.getSchemaId(), persistedBmmClass.getName());
            } else {
                classNameList.add(className);
            }
        });
    }
}
