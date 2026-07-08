package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.definitions.AdlCodeDefinitions;
import com.nedap.archie.flattener.ArchetypeRepository;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.ArrayList;
import java.util.List;

public abstract class ArchetypeValidationBase implements ArchetypeValidation {

    protected Archetype archetype;
    protected Archetype flatParent;
    protected FullArchetypeRepository repository;
    protected List<ValidationMessage> messages;
    protected ModelInfoLookup lookup;
    protected MetaModel metaModel;
    protected ArchetypeValidationSettings settings;

    public ArchetypeValidationBase() {
    }

    @Override
    public List<ValidationMessage> validate(MetaModel metaModel, Archetype archetype, Archetype flatParent, FullArchetypeRepository repository, ArchetypeValidationSettings settings) {
        this.archetype = archetype;
        this.flatParent = flatParent;
        this.repository = repository;
        this.lookup = metaModel.getModelInfoLookup();
        this.metaModel = metaModel;
        this.settings = settings;

        messages = new ArrayList<>();
        validate();
        return messages;
    }

    public abstract void validate();

    /**
     * Resolve the node identifier code system this archetype must be validated against, based on the
     * {@link ArchetypeValidationSettings} and, when set to auto-detect, the code system of the archetype root node.
     *
     * @return true if the archetype is expected to be at-coded, false if it is expected to be id-coded.
     */
    protected boolean expectsAtCodedNodeIds() {
        switch (settings.getNodeIdCodeSystemValidation()) {
            case AT_CODED:
                return true;
            case AUTO_DETECT:
                String rootNodeId = archetype.getDefinition() == null ? null : archetype.getDefinition().getNodeId();
                // detect on the prefix only: at-coded ADL 2.4 node ids are zero-padded (e.g. at0000), which the
                // strict ADL 2 code format would reject, so isValidCode cannot be used here.
                return rootNodeId != null && rootNodeId.startsWith(AdlCodeDefinitions.VALUE_CODE_LEADER);
            case ID_CODED:
            default:
                return false;
        }
    }

    /**
     * Code format validity check that respects the archetype code system: id-coded codes must use the strict ADL 2
     * format (no leading zeros), while at-coded ADL 2.4 codes must use the zero-padded style retained from ADL 1.4
     * (e.g. at0000, at0000.1).
     */
    protected boolean isValidCodeForCodeSystem(String code) {
        return expectsAtCodedNodeIds() ? AOMUtils.isValidAtCodedCode(code) : AOMUtils.isValidIdCodedCode(code);
    }

    public void addMessage(ErrorType errorType) {
        messages.add(new ValidationMessage(errorType));
    }

    public void addMessageWithPath(ErrorType errorType, String path) {
        messages.add(new ValidationMessage(errorType, path));
    }

    public void addMessageWithPath(ErrorType errorType, String path, String customMessage) {
        messages.add(new ValidationMessage(errorType, path, customMessage));
    }

    public void addMessage(ErrorType errorType, String customMessage) {
        messages.add(new ValidationMessage(errorType, null, customMessage));
    }

    public void addWarning(ErrorType errorType) {
        ValidationMessage message = new ValidationMessage(errorType);
        message.setWarning(true);
        messages.add(message);

    }

    public void addWarning(ErrorType errorType, String customMessage) {
        ValidationMessage message = new ValidationMessage(errorType, null, customMessage);
        message.setWarning(true);
        messages.add(message);
    }

    public void addWarningWithPath(ErrorType errorType, String location) {
        ValidationMessage message = new ValidationMessage(errorType, location);
        message.setWarning(true);
        messages.add(message);
    }

    public void addWarningWithPath(ErrorType errorType, String location, String customMessage) {
        ValidationMessage message = new ValidationMessage(errorType, location, customMessage);
        message.setWarning(true);
        messages.add(message);
    }

    public boolean hasPassed() {
        return messages.isEmpty();
    }

    public ModelInfoLookup getLookup() {
        return lookup;
    }

    public Archetype getArchetype() {
        return archetype;
    }

    public Archetype getFlatParent() {
        return flatParent;
    }

    public ArchetypeRepository getRepository() {
        return repository;
    }

    public List<ValidationMessage> getMessages() {
        return messages;
    }

    public ArchetypeValidationSettings getSettings() {
        return settings;
    }
}
