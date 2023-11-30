package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import org.openehr.utils.message.I18n;

import java.util.HashMap;

/**
 * Validated uniqueness of node ids (VCOSU) and presence of node ids (VCOID)
 *
 * Created by pieter.bos on 31/03/2017.
 */
public class NodeIdValidation extends ValidatingVisitor {

    //for every id code, it's path
    private HashMap<String, String> nodeIds = new HashMap<>();

    public NodeIdValidation() {
        super();
    }

    @Override
    protected void beginValidation() {
        nodeIds.clear();
    }

    @Override
    public void validate(CObject cObject) {
        int archetypeSpecialisationDepth = archetype.specializationDepth();

        if(cObject.getNodeId() == null) {
            //every object must have a node id
            addMessageWithPath(ErrorType.VCOID, cObject.getPath(),
                    I18n.t("C_OBJECT with RM type {0} must have a node id", cObject.getRmTypeName()));
        }
        else if(!CPrimitiveObject.PRIMITIVE_NODE_ID_VALUE.equals(cObject.getNodeId()) && archetypeSpecialisationDepth == AOMUtils.getSpecializationDepthFromCode(cObject.getNodeId()) && nodeIds.containsKey(cObject.getNodeId())) {
            //every node id in a single archetype must be unique or a primitive object, or a occurrences matches {0} because sometimes that's the only way
            addMessageWithPath(ErrorType.VCOSU, cObject.getPath(), I18n.t("Node id {0} already used in path {1}", cObject.getNodeId(), nodeIds.get(cObject.getNodeId())));
        }
        nodeIds.put(cObject.getNodeId(), cObject.getPath());
    }


}
