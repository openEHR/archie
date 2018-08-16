package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CPrimitiveTuple;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ArchetypeValidatingVisitor;

import java.util.List;

public class AttributeTupleValidation extends ArchetypeValidatingVisitor {

    /**
     * Override for validation on complex objects
     * @param cObject
     * @return
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
                                    "Tuple member attribute " + cAttribute.getRmAttributeName() + " is not a known attribute of " + cObject.getRmTypeName() + " or it is has not been implemented in Archie");
                        }
                    }
                    for(CPrimitiveTuple primitiveTuple:tuple.getTuples()) {
                        if(primitiveTuple.getMembers().size() != members.size()) {
                            addMessageWithPath(ErrorType.OTHER, cObject.getPath(), "There should be " + members.size() + " tuple members, but there were " + primitiveTuple.getMembers().size());
                        }
                    }
                }
            }
        }
    }
}
