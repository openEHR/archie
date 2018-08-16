package com.nedap.archie.archetypevalidator;

import com.google.common.base.Joiner;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.archetypevalidator.validations.*;
import com.nedap.archie.flattener.ArchetypeRepository;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.flattener.OverridingInMemFullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;
import org.openehr.bmm.rmaccess.ReferenceModelAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public class ArchetypeValidator {
    private static final Logger logger = LoggerFactory.getLogger(ArchetypeValidator.class);

    private MetaModels combinedModels;

    //see comment on why there is a phase 0
    private List<ArchetypeValidation> validationsPhase0;

    private List<ArchetypeValidation> validationsPhase1;

    private List<ArchetypeValidation> validationsPhase2;

    private List<ArchetypeValidation> validationsPhase3;




    public ArchetypeValidator(ReferenceModels models) {
        this(models, null);
    }

    public ArchetypeValidator(ReferenceModels models, ReferenceModelAccess bmmModels) {
        this(new MetaModels(models, bmmModels));
    }

    public ArchetypeValidator(MetaModels models) {
        this.combinedModels = models;
        validationsPhase0 = new ArrayList<>();
        //defined in spec, but not in three phase validator and not in grammar
        //eiffel checks these in the parser
        //but there's no reason this cannot be parsed, so check them here
        validationsPhase0.add(new AttributeUniquenessValidation());
        validationsPhase0.add(new NodeIdValidation());

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
        validationsPhase1.add(new AnnotationsValidation());
        //TODO: validation annotations

        validationsPhase2 = new ArrayList<>();

        validationsPhase2.add(new ValidateAgainstReferenceModel());
        validationsPhase2.add(new SpecializedDefinitionValidation());

        validationsPhase3 = new ArrayList<>();
        validationsPhase3.add(new FlatFormValidation());

    }

    public ArchetypeValidationResult validate(Archetype archetype) {
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
     * or the parent archetype caused the error by inspecting ArchetypeValidationResult.getArchetypeId().
     *
     * All results will be stored in the given FullArchetypeRepository
     *
     * @param archetype
     * @param repository
     * @return
     */
    public ArchetypeValidationResult validate(Archetype archetype, FullArchetypeRepository repository) {
        ArchetypeValidationSettings settings = repository == null ? null : repository.getArchetypeValidationSettings();
        if(settings == null) {
            settings = new ArchetypeValidationSettings();
        }
        if(archetype instanceof Template) {
            //in the case of a template, add a repository that can store the overlays separate from the rest of the archetypes
            //later they can be retrieved and handled as extra archetypes, that are not top level archetypes usable in other
            //archetypes that are not templates
            //TODO: can we specialize templates? In that case we need more work to get any template overlays defined in
            //the parent template in this repo as well
            OverridingInMemFullArchetypeRepository extraRepository = null;
            if(repository == null) {
                extraRepository = new OverridingInMemFullArchetypeRepository();
            } else {
                extraRepository = new OverridingInMemFullArchetypeRepository(repository);
            }
            for(TemplateOverlay overlay:((Template) archetype).getTemplateOverlays()) {
                extraRepository.addExtraArchetype(overlay);
            }
            for(TemplateOverlay overlay:((Template) archetype).getTemplateOverlays()) {
                //validate the overlays first, but make sure to do that only once (so don't call this same method!)
                getValidationResult(overlay.getArchetypeId().toString(), extraRepository);
            }
            repository = extraRepository;
        }


        combinedModels.selectModel(archetype);

        if(combinedModels.getSelectedModelInfoLookup() == null && combinedModels.getSelectedBmmModel() == null) {
            throw new UnsupportedOperationException("reference model unknown for archetype " + archetype.getArchetypeId());
        }
        //we assume we always want a new validation to be run, for example because the archetype
        //has been updated. Therefore, do not retrieve the old result from the repository
        archetype = cloneAndPreprocess(combinedModels, archetype);//this clones the actual archetype so the source does not get changed
        ArchetypeValidationResult parentArchetypeValidationResult = null;
        Archetype flatParent = null;
        if(archetype.isSpecialized()) {
            parentArchetypeValidationResult = getParentValidationResult(archetype, repository);
            if(parentArchetypeValidationResult != null) {
                if(parentArchetypeValidationResult.passes()) {
                    flatParent = parentArchetypeValidationResult.getFlattened();
                } else {
                    return parentArchetypeValidationResult;
                }
            }
        }
        if(repository == null) {
            repository = new InMemoryFullArchetypeRepository();
        }

        List<ArchetypeValidationMessage> messages = runValidations(archetype, repository, settings, flatParent, validationsPhase0);
        ArchetypeValidationResult result = new ArchetypeValidationResult(archetype);
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
        if(result.passes() || settings.isAlwaysTryToFlatten()) {
            try {
                Archetype flattened = new Flattener(repository, combinedModels).flatten(archetype);
                result.setFlattened(flattened);
                if(result.passes()) {
                    messages.addAll(runValidations(flattened, repository, settings, flatParent, validationsPhase3));
                }
            } catch (Exception e) {
                messages.add(new ArchetypeValidationMessage(ErrorType.OTHER, "flattening failed with exception " + e));
                logger.error("error during validation", e);
            }
        }
        if(archetype instanceof Template) {
            OverridingInMemFullArchetypeRepository repositoryWithOverlays =  (OverridingInMemFullArchetypeRepository) repository;
            FullArchetypeRepository extraArchetypeRepository = repositoryWithOverlays.getExtraArchetypeRepository();
            result.addOverlayValidations(extraArchetypeRepository.getAllValidationResults());
            for(ArchetypeValidationResult subResult:extraArchetypeRepository.getAllValidationResults()) {
                if(!subResult.passes()) {
                    result.getErrors().add(new ArchetypeValidationMessage(ErrorType.OTHER, MessageFormat.format("template overlay {0} had validation errors", subResult.getArchetypeId())));
                }
            }
        }

        if(repository != null) {
            repository.setValidationResult(result);
        }
        return result;
    }

    private ArchetypeValidationResult getParentValidationResult(Archetype archetype, FullArchetypeRepository repository) {
        if(!archetype.isSpecialized()) {
            return null; //no parent
        }
        if(repository == null) {
            return null;
        }
        return getValidationResult(archetype.getParentArchetypeId(), repository);
    }

    /**
     * Get the validation rsult for the given archetype id from given repository. Perform validation and add to repository
     * if not already present in the repository.
     * @param archetypeId
     * @param repository
     * @return
     */
    private ArchetypeValidationResult getValidationResult(String archetypeId, FullArchetypeRepository repository) {
        Archetype archetype = repository.getArchetype(archetypeId);
        if(archetype == null) {
            return null; //this situation will trigger the correct message later
        }

        ArchetypeValidationResult archetypeValidationResult = repository.getValidationResult(archetypeId);
        if(archetypeValidationResult == null) {
            //archetype not yet validated. do it now.
            archetypeValidationResult = validate(archetype, repository);
        }
        return archetypeValidationResult;
    }

    private Archetype cloneAndPreprocess(MetaModels models, Archetype archetype) {
        Archetype preprocessed = archetype.clone();
        new ReflectionConstraintImposer(models).setSingleOrMultiple(preprocessed.getDefinition());
        return preprocessed;
    }

    private List<ArchetypeValidationMessage> runValidations(Archetype archetype, ArchetypeRepository repository, ArchetypeValidationSettings settings, Archetype flatParent, List<ArchetypeValidation> validations) {

        List<ArchetypeValidationMessage> messages = new ArrayList<>();
        for(ArchetypeValidation validation: validations) {
            try {
                messages.addAll(validation.validate(combinedModels, archetype, flatParent, repository, settings));
            } catch (Exception e) {
                logger.error("error running validation processor", e);
                e.printStackTrace();
                messages.add(new ArchetypeValidationMessage(ErrorType.OTHER, "unknown path", "error running validator : " + e.getClass().getSimpleName() +
                        Joiner.on("\n").join(e.getStackTrace())));
            }
        }
        return messages;
    }

}
