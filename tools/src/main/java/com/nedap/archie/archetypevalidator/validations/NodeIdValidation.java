package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ArchetypeValidatingVisitor;

import java.util.HashMap;

/**
 * Validated uniqueness of node ids (VCOSU) and presence of node ids (VCOID)
 *
 * Created by pieter.bos on 31/03/2017.
 */
public class NodeIdValidation extends ArchetypeValidatingVisitor {

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
        if(cObject.getNodeId() == null) {
            //every object must have a node id
            addMessageWithPath(ErrorType.VCOID, cObject.getPath());
        }
        else if(!CPrimitiveObject.PRIMITIVE_NODE_ID_VALUE.equals(cObject.getNodeId()) && nodeIds.containsKey(cObject.getNodeId())) {
            //every node id in a single archetype must be unique or a primitive object
            addMessageWithPath(ErrorType.VCOSU, cObject.getPath(), "node ID " + cObject.getNodeId() + " already used in " + nodeIds.get(cObject.getNodeId()));
        }
        nodeIds.put(cObject.getNodeId(), cObject.getPath());
    }


}
