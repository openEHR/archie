package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.archetypevalidator.ArchetypeValidationSettings;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;
import com.github.zafarkhaja.semver.Version;

import java.util.List;
import java.util.Objects;

public interface FullArchetypeRepository extends ArchetypeRepository, OperationalTemplateProvider {

    Archetype getFlattenedArchetype(String archetypeId);

    ValidationResult getValidationResult(String archetypeId);

    OperationalTemplate getOperationalTemplate(String archetypeId);

    void setValidationResult(ValidationResult result);

    void setFlattenedArchetype(Archetype archetype);

    void setOperationalTemplate(OperationalTemplate template);

    /**
     * Removes the validation result and the operational template of the given archetype id. Keeps the archetype
     *
     * @param archetypeId
     */
    void removeValidationResult(String archetypeId);

    List<ValidationResult> getAllValidationResults();

    ArchetypeValidationSettings getArchetypeValidationSettings();

    default void compile(ReferenceModels models) {
        ArchetypeValidator validator = new ArchetypeValidator(models);
        compile(validator);
    }

    default void compile(MetaModels models) {
        ArchetypeValidator validator = new ArchetypeValidator(models);
        compile(validator);
    }

    /**
     * validate the validation result if necessary, and return either the newly validated one or
     * the existing validation result
     * @param models
     * @return
     */
    default ValidationResult compileAndRetrieveValidationResult(String archetypeId, MetaModels models) {
        ValidationResult validationResult = getValidationResult(archetypeId);
        Archetype archetype = getArchetype(archetypeId);
        if(validationResult != null && !archetypeIsNewerVersion(archetype, validationResult)) {
            //only return if the ValidationResult is the newest version of the archetype, otherwise compile it.
            return validationResult;
        }

        if(archetype == null) {
            return null;
        }
        ArchetypeValidator validator = new ArchetypeValidator(models);
        return validator.validate(archetype, this);
    }

    static boolean archetypeIsNewerVersion(Archetype archetype, ValidationResult validationResult) {
        if(archetype == null || archetype.getArchetypeId() == null) {
            return false;
        }
        String archetypeVersion = archetype.getArchetypeId().getVersionId();
        String validationResultVersion;
        if(validationResult.getSourceArchetype() == null || validationResult.getSourceArchetype().getArchetypeId() == null) {
            //TODO: how to handle? use the string based archetypeID?
            validationResultVersion = new ArchetypeHRID(validationResult.getArchetypeId()).getVersionId();
        } else {
            validationResultVersion = validationResult.getSourceArchetype().getArchetypeId().getVersionId();
        }

        return new CustomVersionComparator().compare(Version.valueOf(archetypeVersion), Version.valueOf(validationResultVersion)) > 0;
    }


    /**
     * validate the validation result if necessary, and return either the newly validated one or
     * the existing validation result
     * @param models
     * @return
     */
    default ValidationResult compileAndRetrieveValidationResult(String archetypeId, ArchetypeValidator validator) {
        ValidationResult validationResult = getValidationResult(archetypeId);
        Archetype archetype = getArchetype(archetypeId);
        if(validationResult != null && !archetypeIsNewerVersion(archetype, validationResult)) {
            //only return if the ValidationResult is the newest version of the archetype, otherwise compile it.
            return validationResult;
        }
        if(archetype == null) {
            return null;
        }
        return validator.validate(archetype, this);
    }

    default void compile(ArchetypeValidator validator) {
        for(Archetype archetype:getAllArchetypes()) {
            if(getValidationResult(archetype.getArchetypeId().toString()) == null) {
                validator.validate(archetype, this);
            }
        }
    }
}
