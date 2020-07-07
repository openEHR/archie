package org.openehr.bmm.v2.validation.converters;


import org.openehr.bmm.persistence.validation.BmmMessageIds;
import org.openehr.bmm.v2.persistence.BmmIncludeSpec;
import org.openehr.bmm.v2.persistence.PBmmClass;
import org.openehr.bmm.v2.persistence.PBmmPackage;
import org.openehr.bmm.v2.persistence.PBmmSchema;
import org.openehr.bmm.v2.validation.BmmSchemaConverter;
import org.openehr.bmm.v2.validation.BmmValidationResult;
import org.openehr.bmm.v2.validation.BmmRepository;
import org.openehr.utils.message.MessageLogger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IncludesProcessor {

    public List<String> getIncludeIds(PBmmSchema schema) {

        return schema.getIncludes().values().stream()
                .map( includeSpec -> includeSpec.getId()).collect(Collectors.toCollection(ArrayList::new));
    }

    public void cloneSchemaAndAddIncludes(BmmValidationResult validationResult, BmmRepository repository, MessageLogger logger) {
        //step 1: check that all includes exist
        PBmmSchema schema = validationResult.getOriginalSchema();
        for(BmmIncludeSpec include: schema.getIncludes().values())
            if(!repository.containsPersistentSchema(include.getId()))
                logger.addError(BmmMessageIds.ec_bmm_schema_included_schema_not_found, include.getId());

        if (!logger.hasErrors()) {
            PBmmSchema clone = (PBmmSchema) schema.clone();
            validationResult.setSchemaWithMergedIncludes(clone);
            //step 2: get all BMM Models for all includes and merge into BmmModel
            for (BmmIncludeSpec include : schema.getIncludes().values()) {
                //check if already included. If so, don't include again.
                // This prevents double includes plus a potential infinite loop
                if (!validationResult.getMergedSchemas().contains(include.getId()) || validationResult.getFailedMergedSchemas().contains(include.getId())) {

                    BmmValidationResult included = repository.getModel(include.getId());
                    if (included == null) {
                        PBmmSchema persistentSchema = repository.getPersistentSchema(include.getId());
                        BmmSchemaConverter bmmSchemaConverter = new BmmSchemaConverter(repository);
                        included = bmmSchemaConverter.validateConvertAndAddToRepo(persistentSchema);
                    }
                    if (!included.passes()) {
                        logger.addError(BmmMessageIds.ec_bmm_schema_includes_valiidation_failed, schema.getSchemaId(), included.getLogger().toString());
                        validationResult.addFailedMerge(include.getId());
                    }
                    else
                        mergeIncluded(validationResult, included);
                }
            }
        }
    }

    private void mergeIncluded(BmmValidationResult includingValidationResult, BmmValidationResult includedValidation) {
        PBmmSchema including = includingValidationResult.getSchemaWithMergedIncludes();
        PBmmSchema included = includedValidation.getSchemaWithMergedIncludes();
        //archetype parent class: only merge if nothing already in the higher-level schema
        if (included.getArchetypeParentClass() != null &&  including.getArchetypeParentClass() == null)
            including.setArchetypeParentClass(included.getArchetypeParentClass());

        //archetype data value parent class: only merge if nothing already in the higher-level schema
        if (included.getArchetypeDataValueParentClass() != null && including.getArchetypeDataValueParentClass() == null)
            including.setArchetypeDataValueParentClass(including.getArchetypeDataValueParentClass());

        //archetype closures
        LinkedHashSet<String> newClosurePackages = new LinkedHashSet<>();
        newClosurePackages.addAll(included.getArchetypeRmClosurePackages());
        newClosurePackages.addAll(including.getArchetypeRmClosurePackages());
        included.setArchetypeRmClosurePackages(new ArrayList<>(newClosurePackages));

        for (Map.Entry<String, PBmmPackage> packageEntry:includedValidation.getCanonicalPackages().entrySet()) {
            if (includingValidationResult.getCanonicalPackages().containsKey(packageEntry.getKey())) {
                PBmmPackage persistedBmmPackage = includingValidationResult.getCanonicalPackages().get(packageEntry.getKey());
                merge(persistedBmmPackage, packageEntry.getValue());
            }
            else
                includingValidationResult.getCanonicalPackages().put(packageEntry.getKey(), (PBmmPackage) packageEntry.getValue().clone());
        }

        //If a package already exist, merge its classes, for each child package repeat...
        //Merge class definitions first. If you see a class with the same name, log it (complain) - OpenEHR has no notion of namespaces. Need to fix spec to support them.
        //this automatically includes primitive types
        for (String className:included.getClassDefinitions().keySet()) {
            PBmmClass bmmClass = included.getClassDefinitions().get(className);
            including.getClassDefinitions().put(className, (PBmmClass) bmmClass.clone());
        }
        for (String primitiveTypeName:included.getPrimitiveTypes().keySet()) {
            PBmmClass pBmmClass = included.getPrimitiveTypes().get(primitiveTypeName);
            including.getPrimitiveTypes().put(primitiveTypeName, (PBmmClass) pBmmClass.clone());
        }

        includingValidationResult.addMergedSchema(included.getSchemaId().toUpperCase());
    }

    private void merge(PBmmPackage including, PBmmPackage included) {
        LinkedHashSet<String> newClasses = new LinkedHashSet<>(including.getClasses());
        newClasses.addAll(included.getClasses());
        including.setClasses(new ArrayList<>(newClasses));
        included.getPackages().values().forEach(p -> {
            PBmmPackage sourcePackage = including.getPackages().get(p.getName());
            if (sourcePackage != null)
                merge(sourcePackage, p);
            else
                including.getPackages().put(p.getName().toUpperCase(), (PBmmPackage) p.clone());
        });
    }
}
