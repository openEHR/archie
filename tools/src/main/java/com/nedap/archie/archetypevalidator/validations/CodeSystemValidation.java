package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import com.nedap.archie.definitions.AdlCodeDefinitions;
import org.openehr.utils.message.I18n;

/**
 * Validates that the archetype uses a single node identifier code system, and that this code system matches the
 * one the validator is configured to accept.
 *
 * ADL 2 archetypes can be id-coded (node ids id1, id1.1, ...) or, since ADL 2.4, at-coded (node ids at0000,
 * at0000.1, ...). Mixing both systems in a single archetype is not allowed. Which system is accepted is configured
 * through {@link com.nedap.archie.archetypevalidator.ArchetypeValidationSettings}; in auto-detect mode the code
 * system of the root node determines what the rest of the archetype is validated against.
 */
public class CodeSystemValidation extends ValidatingVisitor {

    private boolean idCodedNodeIdSeen;
    private boolean atCodedNodeIdSeen;

    public CodeSystemValidation() {
        super();
    }

    @Override
    protected void beginValidation() {
        idCodedNodeIdSeen = false;
        atCodedNodeIdSeen = false;
    }

    @Override
    public void validate(CObject cObject) {
        // Only structural node identifiers are relevant. Value codes (at) and value set codes (ac) live inside
        // primitive constraints and the terminology, never as a C_OBJECT node id, so primitive objects (including
        // C_TERMINOLOGY_CODE and the id9999 primitive node id) are skipped.
        if (cObject instanceof CPrimitiveObject) {
            return;
        }
        String nodeId = cObject.getNodeId();
        // Use the lenient code check so zero-padded at-coded node ids (e.g. at0000) are recognised too; this is a
        // superset that also matches id-coded node ids.
        if (nodeId == null || !AOMUtils.isValidADL14Code(nodeId)) {
            return;
        }
        if (AOMUtils.isIdCode(nodeId)) {
            idCodedNodeIdSeen = true;
        } else if (nodeId.startsWith(AdlCodeDefinitions.VALUE_CODE_LEADER)) {
            atCodedNodeIdSeen = true;
        }
    }

    @Override
    protected void endValidation() {
        if (idCodedNodeIdSeen && atCodedNodeIdSeen) {
            addMessageWithPath(ErrorType.INVALID_NODE_ID_CODE_SYSTEM, "/",
                    I18n.t("An archetype must use a single node identifier code system, but both id-coded and at-coded node ids are used"));
        } else if (expectsAtCodedNodeIds() && idCodedNodeIdSeen) {
            addMessageWithPath(ErrorType.INVALID_NODE_ID_CODE_SYSTEM, "/",
                    I18n.t("The validator is configured to accept at-coded archetypes, but the archetype uses id-coded node ids"));
        } else if (!expectsAtCodedNodeIds() && atCodedNodeIdSeen) {
            addMessageWithPath(ErrorType.INVALID_NODE_ID_CODE_SYSTEM, "/",
                    I18n.t("The validator is configured to accept id-coded archetypes, but the archetype uses at-coded node ids"));
        }
    }
}
