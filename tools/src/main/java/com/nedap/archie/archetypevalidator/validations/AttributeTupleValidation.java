package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CPrimitiveTuple;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import org.openehr.utils.message.I18n;

import java.util.List;

public class AttributeTupleValidation extends ValidatingVisitor {

    /**
     * Override for validation on complex objects
     * @param cObject the cobject to validate
     */
    protected void validate(CComplexObject cObject) {
        if(cObject.getAttributeTuples() != null) {
            for(CAttributeTuple tuple : cObject.getAttributeTuples()) {
                List<CAttribute> members = tuple.getMembers();

                if(members == null) {
                    addMessageWithPath(ErrorType.OTHER, cObject.getPath(), "An attribute tuple must have members");
                } else {
                    for(CAttribute cAttribute:tuple.getMembers()) {
                        if (!combinedModels.attributeExists(cObject.getRmTypeName(), cAttribute.getRmAttributeName())) {
                            addMessageWithPath(ErrorType.VCARM, cObject.getPath(),
                                    I18n.t("Tuple member attribute {0} is not an attribute of type {1}", cAttribute.getRmAttributeName(), cObject.getRmTypeName()));
                        }
                    }
                    for(CPrimitiveTuple primitiveTuple:tuple.getTuples()) {
                        if(primitiveTuple.getMembers().size() != members.size()) {
                            addMessageWithPath(ErrorType.OTHER, cObject.getPath(),
                                    I18n.t("In the attribute tuple {0} members were specified, but the primitive tuple has {1} members instead", members.size(), primitiveTuple.getMembers().size()));
                        }
                    }
                }
            }
        }
    }
}
