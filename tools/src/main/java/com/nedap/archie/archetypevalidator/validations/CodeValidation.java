package com.nedap.archie.archetypevalidator.validations;

import com.google.common.base.Joiner;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import org.openehr.utils.message.I18n;

import java.util.HashMap;

import static com.nedap.archie.aom.utils.AOMUtils.parentIsMultiple;

public class CodeValidation extends ValidatingVisitor {

    public CodeValidation() {
        super();
    }

    private HashMap<String, String> nodeIdsWithoutPrefix = new HashMap<>();

    @Override
    protected void beginValidation() {
        nodeIdsWithoutPrefix.clear();
    }

    @Override
    public void validate(CObject cObject) {
        if(cObject instanceof CTerminologyCode) {
            validate((CTerminologyCode) cObject);
        }
        String nodeId = cObject.getNodeId();
        int codeSpecializationDepth = AOMUtils.getSpecializationDepthFromCode(nodeId);
        int archetypeSpecializationDepth = archetype.specializationDepth();
        if(codeSpecializationDepth > archetypeSpecializationDepth) {
            addMessageWithPath(ErrorType.VTSD, cObject.path(),
                    I18n.t("The code specialization depth of code {0} is {1}, which is greater than archetype specialization depth {2}",
                            nodeId, codeSpecializationDepth, archetypeSpecializationDepth));
        } else if (cObject.isRoot() || parentIsMultiple(cObject, flatParent, combinedModels)) {
            if ((codeSpecializationDepth < archetypeSpecializationDepth && flatParent != null && !flatParent.getTerminology().hasIdCode(nodeId)) ||
                    (codeSpecializationDepth == archetypeSpecializationDepth && !archetype.getTerminology().hasIdCode(nodeId))) {
                addMessageWithPath(ErrorType.VATID, cObject.path(),
                        I18n.t("Node id {0} is used in the archetype, but missing in the terminology", nodeId));
            }
        }

        if(!CPrimitiveObject.PRIMITIVE_NODE_ID_VALUE.equals(cObject.getNodeId())) {
            nodeIdsWithoutPrefix.put(AOMUtils.stripPrefix(cObject.getNodeId()), cObject.getPath());
        }
    }

    public void validate(CTerminologyCode cTerminologyCode) {
        //validate CTerminology codes
        int archetypeSpecializationDepth = archetype.specializationDepth();

        for(String constraint:cTerminologyCode.getConstraint()) {
            int codeSpecializationDepth = AOMUtils.getSpecializationDepthFromCode(constraint);

            if (!CPrimitiveObject.PRIMITIVE_NODE_ID_VALUE.equals(constraint) &&
                    archetypeSpecializationDepth == codeSpecializationDepth &&
                    nodeIdsWithoutPrefix.containsKey(AOMUtils.stripPrefix(constraint))) {
                addWarningWithPath(ErrorType.ADL14_INCOMPATIBLE_NODE_IDS, cTerminologyCode.getPath(),
                        I18n.t("Node id {0} already used in path {1} with a different at, id or ac prefix. Will not be convertible to ADL 1.4",
                                constraint, nodeIdsWithoutPrefix.get(AOMUtils.stripPrefix(constraint))));
            }

            if(AOMUtils.isValueSetCode(constraint)) {
                if(codeSpecializationDepth > archetypeSpecializationDepth) {
                    addMessageWithPath(ErrorType.VATCD, cTerminologyCode.path(), I18n.t("Code {0} from the C_TERMINOLOGY_CODE constraint has specialization depth {1}, but this must be no greater than {2}",
                            constraint, codeSpecializationDepth, archetypeSpecializationDepth));
                } else if((codeSpecializationDepth < archetypeSpecializationDepth && !flatParent.getTerminology().hasValueSetCode(constraint)) ||
                        (codeSpecializationDepth== archetypeSpecializationDepth && !archetype.getTerminology().hasValueSetCode(constraint))) {
                    addMessageWithPath(ErrorType.VACDF, cTerminologyCode.path(),
                            I18n.t("Code {0} from the C_TERMINOLOGY_CODE constraint is not defined in the terminology", constraint));
                } else if(cTerminologyCode.getAssumedValue() != null) {
                    ValueSet valueSet = archetype.getTerminology().getValueSets().get(constraint);
                    if(valueSet != null) {
                        if(!valueSet.getMembers().contains(cTerminologyCode.getAssumedValue().getCodeString())) {
                            addMessageWithPath(ErrorType.VATDA, cTerminologyCode.path(),
                                    I18n.t("Assumed value {0} from the C_TERMINOLOGY_CODE is not part of value set {1}. Expected one of {2}",
                                            cTerminologyCode.getAssumedValue(),
                                            valueSet.getId(),
                                            Joiner.on(", ").join(valueSet.getMembers())));
                        }
                    }
                }


            } else if (AOMUtils.isValueCode(constraint)) {
                if(codeSpecializationDepth > archetypeSpecializationDepth) {
                    addMessageWithPath(ErrorType.VATCD, cTerminologyCode.path(), I18n.t("Code {0} from the C_TERMINOLOGY_CODE constraint has specialization depth {1}, but this must be no greater than {2}",
                            constraint, codeSpecializationDepth, archetypeSpecializationDepth));
                } else if((codeSpecializationDepth < archetypeSpecializationDepth && !flatParent.getTerminology().hasValueCode(constraint)) ||
                        (codeSpecializationDepth== archetypeSpecializationDepth && !archetype.getTerminology().hasValueCode(constraint))) {
                    addMessageWithPath(ErrorType.VATDF, cTerminologyCode.path(), I18n.t("Code {0} from C_TERMINOLOGY_CODE constraint is not defined in the terminology", constraint));
                }
            }
        }

    }

}
