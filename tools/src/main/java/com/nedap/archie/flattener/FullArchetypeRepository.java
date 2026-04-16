package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.archetypevalidator.ArchetypeValidationSettings;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.rminfo.MetaModelProvider;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;

import java.util.List;

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

    /**
     * @deprecated Use {@link #compile(MetaModelProvider)} instead.
     */
    @Deprecated
    default void compile(MetaModels models) {
        compile((MetaModelProvider) models);
    }

    default void compile(MetaModelProvider metaModelProvider) {
        ArchetypeValidator validator = new ArchetypeValidator(metaModelProvider);
        compile(validator);
    }

    /**
     * validate the validation result if necessary, and return either the newly validated one or
     * the existing validation result
     * @param metaModelProvider
     * @return
     */
    default ValidationResult compileAndRetrieveValidationResult(String archetypeId, MetaModelProvider metaModelProvider) {
        Archetype archetype = getArchetype(archetypeId);
        if(archetype == null) {
            return null;
        }
        ValidationResult validationResult = getValidationResult(archetype.getArchetypeId().getFullId());

        if(validationResult != null) {
            //only return if the ValidationResult is the newest version of the archetype, otherwise compile it.
            return validationResult;
        }

        ArchetypeValidator validator = new ArchetypeValidator(metaModelProvider);
        return validator.validate(archetype, this);
    }

    /**
     * validate the validation result if necessary, and return either the newly validated one or
     * the existing validation result
     * @param models
     * @return
     */
    @Deprecated
    default ValidationResult compileAndRetrieveValidationResult(String archetypeId, MetaModels models) {
        return compileAndRetrieveValidationResult(archetypeId, (MetaModelProvider) models);
    }

    /**
     * validate the validation result if necessary, and return either the newly validated one or
     * the existing validation result
     * @return
     */
    default ValidationResult compileAndRetrieveValidationResult(String archetypeId, ArchetypeValidator validator) {
        Archetype archetype = getArchetype(archetypeId);
        if(archetype == null) {
            return null;
        }
        ValidationResult validationResult = getValidationResult(archetype.getArchetypeId().getFullId());

        if(validationResult != null) {
            //only return if the ValidationResult is the newest version of the archetype, otherwise compile it.
            return validationResult;
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
