package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.archetypevalidator.ArchetypeValidationBase;
import com.nedap.archie.archetypevalidator.ErrorType;
import org.openehr.utils.message.I18n;

import java.util.Objects;

public class BasicChecks extends ArchetypeValidationBase {

    public BasicChecks() {
        super();
    }

    @Override
    public void validate() {
        checkRmRootType();
        checkIdCodeSpecialisationLevel();
        checkMissingTerminology();
        checkSpecializationDepth();
    }

    private void checkSpecializationDepth() {
        if(archetype.getParentArchetypeId() != null) {
            //parent does NOT need to be flat
            Archetype parent = repository.getArchetype(archetype.getParentArchetypeId());
            if(parent != null) {
                int parentNodeIdSpecialisationDepth = parent.getDefinition().specialisationDepth();
                int nodeIdSpecialisationDepth = archetype.getDefinition().specialisationDepth();
                if(parentNodeIdSpecialisationDepth != nodeIdSpecialisationDepth -1) {
                    addMessage(ErrorType.VACSD, I18n.t("The specialisation depth of the archetype, {0}, must be one greater than the specialisation depth of the parent archetype, {1}", nodeIdSpecialisationDepth, parentNodeIdSpecialisationDepth));
                }
            }
        }
    }

    private void checkMissingTerminology() {
        //missing mandatory parts are checked in grammar, but check here as well
        if(archetype.getTerminology() == null) {
            addMessage(ErrorType.STCNT, I18n.t("Archetype terminology not defined"));
        } else if(archetype.getTerminology().getTermDefinitions() == null || archetype.getTerminology().getTermDefinitions().isEmpty()) {
            addMessage(ErrorType.STCNT,I18n.t("Archetype terminology contains no term definitions"));
        }
    }

    private void checkIdCodeSpecialisationLevel() {
        int depth = ValidationUtils.getSpecializationDepth(archetype, repository);
        if(depth != archetype.getDefinition().specialisationDepth()) {

            addMessageWithPath(ErrorType.VARCN,
                    I18n.t("Incorrect root node id {0}: it must match the specialization depth of the archetype, which is {1}", archetype.getDefinition().getNodeId(), depth),
                    "/"
            );
        }
        if(!archetype.getDefinition().getNodeId().matches("id1(.1)*")) {
            addMessageWithPath(ErrorType.VARCN,
                    I18n.t("The node id is not in the form id1.1....1: {0}", archetype.getDefinition().getNodeId()),
                    "/");
        }
    }



    private void checkRmRootType() {
        if(!Objects.equals(archetype.getArchetypeId().getRmClass(), archetype.getDefinition().getRmTypeName())) {
            addMessage(ErrorType.VARDT, String.format("RM type in id %s does not match RM type in definition %s",
                            archetype.getArchetypeId().getConceptId(),
                            archetype.getDefinition().getRmTypeName()));
        }
    }
}
