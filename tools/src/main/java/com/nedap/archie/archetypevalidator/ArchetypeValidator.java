package com.nedap.archie.archetypevalidator;

import com.google.common.base.Joiner;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.archetypevalidator.validations.*;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.flattener.OverridingInMemFullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;
import org.openehr.utils.message.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public class ArchetypeValidator {
    private static final Logger logger = LoggerFactory.getLogger(ArchetypeValidator.class);

    private MetaModels combinedModels;
    private FlattenerConfiguration flattenerConfiguration = FlattenerConfiguration.forFlattened();

    //see comment on why there is a phase 0
    private List<ArchetypeValidation> validationsPhase0;

    private List<ArchetypeValidation> validationsPhase1;

    private List<ArchetypeValidation> validationsPhase2;

    private List<ArchetypeValidation> validationsPhase3;

    public ArchetypeValidator(ReferenceModels models) {
        this(new MetaModels(models, null));
    }

    public ArchetypeValidator(MetaModels models) {
        this.combinedModels = models;
        validationsPhase0 = new ArrayList<>();
        //defined in spec, but not in three phase validator and not in grammar
        //eiffel checks these in the parser
        //but there's no reason this cannot be parsed, so check them here
        validationsPhase0.add(new AttributeUniquenessValidation());
        validationsPhase0.add(new NodeIdValidation());
        validationsPhase0.add(new BasicDefinitionObjectValidation());

        validationsPhase0.add(new AttributeTupleValidation());

        validationsPhase1 = new ArrayList<>();
        //conforms to spec
        validationsPhase1.add(new BasicChecks());
        //MultiplicitiesValidation is a phase 0 (parser) validation in the archetype editor. However, that would just prevent too many checks, including one of the example checks
        //so it has been moved to phase 1
        validationsPhase1.add(new MultiplicitiesValidation());
        validationsPhase1.add(new AuthoredArchetypeMetadataChecks());
        validationsPhase1.add(new DefinitionStructureValidation());
        validationsPhase1.add(new BasicTerminologyValidation());
        validationsPhase1.add(new VariousStructureValidation());
        validationsPhase1.add(new CodeValidation());
        //TODO: validation annotations

        validationsPhase2 = new ArrayList<>();

        validationsPhase2.add(new ValidateAgainstReferenceModel());
        validationsPhase2.add(new SpecializedDefinitionValidation());

        validationsPhase3 = new ArrayList<>();
        validationsPhase3.add(new SpecializedOccurrencesValidation());
        validationsPhase3.add(new AnnotationsValidation());
        validationsPhase3.add(new RmOverlayValidation());
        validationsPhase3.add(new FlatFormValidation());
    }

    public void setRemoveZeroOccurrencesConstraintsComingFromParents(boolean value) {
        flattenerConfiguration.setRemoveZeroOccurrencesInParents(value);
    }


    public ValidationResult validate(Archetype archetype) {
        return validate(archetype, null);
    }


    /**
     * Validate an archetype, plus a repository of other archetypes.
     *
     * If the archetype is specialized, it MUST be in its differential form.
     *
     * Returns a validationResult that contains errors, warnings, the source archetype and the flattened form of the archetype.
     * If the parent of a specialized archetype did not validate, returns the validationResult
     * of the parent - because that is the error. Using classes must check if the actual archetype given in the argument
     * or the parent archetype caused the error by inspecting ValidationResult.getArchetypeId().
     *
     * All results will be stored in the given FullArchetypeRepository
     *
     * @param archetype
     * @param repository
     * @return
     */
    public ValidationResult validate(Archetype archetype, FullArchetypeRepository repository) {
        ArchetypeValidationSettings settings = repository == null ? null : repository.getArchetypeValidationSettings();
        if(settings == null) {
            settings = new ArchetypeValidationSettings();
        }
        OverridingInMemFullArchetypeRepository extraRepository;
        if(repository == null) {
            extraRepository = new OverridingInMemFullArchetypeRepository();
        } else {
            extraRepository = new OverridingInMemFullArchetypeRepository(repository);
        }
        repository = extraRepository;

        combinedModels.selectModel(archetype);

        if(combinedModels.getSelectedModelInfoLookup() == null && combinedModels.getSelectedBmmModel() == null) {
            throw new UnsupportedOperationException("reference model unknown for archetype " + archetype.getArchetypeId());
        }
        //we assume we always want a new validation to be run, for example because the archetype
        //has been updated. Therefore, do not retrieve the old result from the repository
        archetype = cloneAndPreprocess(combinedModels, archetype);//this clones the actual archetype so the source does not get changed
        Archetype flatParent = null;
        if(archetype.isSpecialized()) {
            ValidationResult infiniteLoopResult = checkForInfiniteLoopInSpecialisation(repository, archetype);
            if (!infiniteLoopResult.passes()) {
                return infiniteLoopResult;
            }
            ValidationResult parentValidationResult = repository.compileAndRetrieveValidationResult(archetype.getParentArchetypeId(), this);
            combinedModels.selectModel(archetype);
            if(parentValidationResult != null) {
                if(parentValidationResult.passes()) {
                    flatParent = parentValidationResult.getFlattened();
                    if(flatParent instanceof Template) {
                        Template parentTemplate = (Template) parentValidationResult.getSourceArchetype();
                        // Add the template overlays from the parent to the extraRepository,
                        // so template overlays specializing other template overlays can be validated.
                        parentTemplate.getTemplateOverlays().forEach(
                                extraRepository::addExtraArchetype
                        );
                    }
                } else {
                    ValidationResult result = new ValidationResult(archetype);
                    List<ValidationMessage> messages = new ArrayList<>();
                    ValidationMessage message = new ValidationMessage(ErrorType.PARENT_VALIDATION_FAILED, "", I18n.t("Error in parent archetype. Fix those errors before continuing with this archetype: {0}", parentValidationResult.getErrors().toString()));
                    messages.add(message);
                    result.setErrors(messages);
                    result.setSourceArchetype(archetype);
                    repository.setValidationResult(result);
                    return result;
                }
            }
        }

        if(archetype instanceof Template) {
            //in the case of a template store the overlays in the extraRepository separate from the rest of the archetypes
            //later they can be retrieved and handled as extra archetypes, that are not top level archetypes usable in other
            //archetypes that are not templates

            for(TemplateOverlay overlay:((Template) archetype).getTemplateOverlays()) {
                extraRepository.addExtraArchetype(overlay);
            }
            for(TemplateOverlay overlay:((Template) archetype).getTemplateOverlays()) {
                //validate the overlays first, but make sure to do that only once (so don't call this same method!)
                extraRepository.compileAndRetrieveValidationResult(overlay.getArchetypeId().toString(), this);
                combinedModels.selectModel(archetype);
            }
        }

        List<ValidationMessage> messages = runValidations(archetype, repository, settings, flatParent, validationsPhase0);
        ValidationResult result = new ValidationResult(archetype);
        result.setErrors(messages);
        if(result.passes()) {
            //continue running only if the basic phase 0 validation run, otherwise we get annoying exceptions
            messages.addAll(runValidations(archetype, repository, settings, flatParent, validationsPhase1));

            //the separate validations will check if the archtype is specialized and if they need this in phase 2
            //because the RM validations are technically phase 2 and required to run
            //also the separate validations are implemented so that they can run with errors in phase 1 without exceptions
            //plus exceptions will nicely be logged as an OTHER error type - we can safely run it and you will get
            //more errors in one go - could be useful
            messages.addAll(runValidations(archetype, repository, settings, flatParent, validationsPhase2));
        }
        result.setErrors(messages);

        if(archetype instanceof Template) {
            FullArchetypeRepository extraArchetypeRepository = extraRepository.getExtraArchetypeRepository();
            result.addOverlayValidations(extraArchetypeRepository.getAllValidationResults());
            for(ValidationResult subResult:extraArchetypeRepository.getAllValidationResults()) {
                if(!subResult.passes()) {
                    result.getErrors().add(new ValidationMessage(ErrorType.OVERLAY_VALIDATION_FAILED, null, I18n.t("Template overlay {0} had validation errors", subResult.getArchetypeId())));
                }
            }
        }

        if(result.passes() || settings.isAlwaysTryToFlatten()) {
            try {
                Archetype flattened = new Flattener(repository, combinedModels, flattenerConfiguration).flatten(archetype, 0);

                try {
                    OperationalTemplate operationalTemplate = (OperationalTemplate) new Flattener(repository, combinedModels).createOperationalTemplate(true).flatten(archetype, 0);
                    extraRepository.addExtraOperationalTemplate(operationalTemplate);
                } catch (Exception e) {
                    //this is probably an error in an included archetype, so ignore it here
                    //the other archetype will not validate
                    ValidationMessage message = new ValidationMessage(ErrorType.OTHER, null, "Error during Operational template creation. This does not necessarily mean the current archetype has a problem, but perhaps one that is included with use_archetype: " + e);
                    message.setWarning(true);
                    messages.add(message);
                }
                result.setFlattened(flattened);
                if(result.passes()) {
                    messages.addAll(runValidations(flattened, repository, settings, flatParent, validationsPhase3));
                }
            } catch (Exception e) {
                messages.add(new ValidationMessage(ErrorType.OTHER, null, "flattening failed with exception " + e));
                logger.error("error during validation", e);
            }
        }

        repository.setValidationResult(result);
        return result;
    }

    private Archetype cloneAndPreprocess(MetaModels models, Archetype archetype) {
        Archetype preprocessed = archetype.clone();
        new ReflectionConstraintImposer(models).setSingleOrMultiple(preprocessed.getDefinition());
        return preprocessed;
    }

    private List<ValidationMessage> runValidations(Archetype archetype, FullArchetypeRepository repository, ArchetypeValidationSettings settings, Archetype flatParent, List<ArchetypeValidation> validations) {

        List<ValidationMessage> messages = new ArrayList<>();
        for(ArchetypeValidation validation: validations) {
            try {
                messages.addAll(validation.validate(combinedModels, archetype, flatParent, repository, settings));
            } catch (Exception e) {
                logger.error("error running validation processor", e);
                e.printStackTrace();
                messages.add(new ValidationMessage(ErrorType.OTHER, null, "error running validator : " + e.getClass().getSimpleName() +
                        Joiner.on("\n").join(e.getStackTrace())));
            }
        }
        return messages;
    }

    private ValidationResult checkForInfiniteLoopInSpecialisation(FullArchetypeRepository repository, Archetype archetype) {
        Set<String> archetypesInSpecialisationTree = new HashSet<>();
        archetypesInSpecialisationTree.add(archetype.getArchetypeId().getFullId());
        Archetype childArchetype;
        Archetype specialisationCheck = archetype;
        while (specialisationCheck.getParentArchetypeId() != null) {
            childArchetype = specialisationCheck;
            specialisationCheck = repository.getArchetype(specialisationCheck.getParentArchetypeId());
            if (specialisationCheck == null) {
                // Parent cannot be found, will fail later in the validation
                break;
            }
            if (archetypesInSpecialisationTree.contains(specialisationCheck.getArchetypeId().getFullId())) {
                ValidationResult validationResult = new ValidationResult(archetype);
                List<ValidationMessage> messages = new ArrayList<>();
                ValidationMessage validationMessage = new ValidationMessage(ErrorType.OTHER, null, "Infinite loop caused by specialising: " + specialisationCheck.getArchetypeId().getFullId() + " in " + childArchetype.getArchetypeId().getFullId());
                messages.add(validationMessage);
                validationResult.setErrors(messages);
                validationResult.setSourceArchetype(archetype);
                return validationResult;
            }
            archetypesInSpecialisationTree.add(specialisationCheck.getArchetypeId().getFullId());
        }
        return new ValidationResult(archetype);
    }

}
