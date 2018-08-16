package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of a validation. Contains:
 * - validation messages, if any,
 * - the unprocessed source archetype
 * - the flattened archetype, if flattening is possible
 */
public class ArchetypeValidationResult {

    private String archetypeId;//in case we do not even have an AOM because of a parse error
    private List<ArchetypeValidationMessage> errors = new ArrayList<>();
    private Archetype sourceArchetype;
    private Archetype flattened;
    private List<ArchetypeValidationResult> overlayValidations;

    public ArchetypeValidationResult(String archetypeId){
        this.archetypeId = archetypeId;
    }

    public ArchetypeValidationResult(Archetype archetype) {
        this.sourceArchetype = archetype;
        this.archetypeId = archetype.getArchetypeId().toString();
        this.overlayValidations = new ArrayList<>();
    }

    /**
     * Get all the validation errors for this archetype
     * @return
     */
    public List<ArchetypeValidationMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<ArchetypeValidationMessage> errors) {
        this.errors = errors;
    }

    /**
     * Get the source archetype of this validation result
     * @return
     */
    public Archetype getSourceArchetype() {
        return sourceArchetype;
    }

    public void setSourceArchetype(Archetype sourceArchetype) {
        this.sourceArchetype = sourceArchetype;
    }

    /**
     * Get the flattened form of the source archetype. Can return null, if the archetype could not be flattened due to
     * validation errors
     *
     * @return
     */
    public Archetype getFlattened() {
        return flattened;
    }

    public void setFlattened(Archetype flattened) {
        this.flattened = flattened;
    }

    public String getArchetypeId() {
        return archetypeId;
    }

    /**
     * Returns true if the archetype has warnings or errors
     * @return
     */
    public boolean hasWarningsOrErrors() {
        return !errors.isEmpty();
    }


    /**
     * Returns true if the archetype has no errors. Warnings are ok.
     * @return
     */
    public boolean passes() {
        for(ArchetypeValidationMessage message:getErrors()) {
            if(!message.isWarning()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add validation results from template overlays defined in a template
     * @param overlayValidations
     */
    public void addOverlayValidations(List<ArchetypeValidationResult> overlayValidations) {
        this.overlayValidations = overlayValidations;
    }

    /**
     * If the source archetype was a Template, it can have template overlays, which are separate archetypes within a template
     * get the validation results for the overlays. If there are errors, there will also be an error saying that
     * there are errors in the overlays in the result of getErrors()
     * @return
     */
    public List<ArchetypeValidationResult> getOverlayValidations() {
        return overlayValidations;
    }



    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("archetype: " + archetypeId);
        result.append("\n");
        result.append("passes: " + passes());
        result.append("\n");
        for(ArchetypeValidationMessage message:errors) {
            result.append(message);
            result.append("\n");

        }

        return result.toString();
    }
}
