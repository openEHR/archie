package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CComplexObjectProxy;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.flattener.ArchetypeRepository;
import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public abstract class ArchetypeValidatingVisitor extends ArchetypeValidationBase {


    public ArchetypeValidatingVisitor() {
        super();
    }

    @Override
    public void validate() {
        beginValidation();
        ArrayDeque<CObject> workList = new ArrayDeque<>();
        workList.add(archetype.getDefinition());
        while(!workList.isEmpty()) {
            CObject cObject = workList.pop();
            validate(cObject);
            for(CAttribute attribute: cObject.getAttributes()) {
                validate(attribute);
                workList.addAll(attribute.getChildren());
            }
        }
        endValidation();

    }


    /**
     * Override to get a callback when validation begins
     * @param archetype the archetype that is currently being validated
     * @param flatParent
     */
    protected void beginValidation() {

    }

    /**
     * Override to get a callback when validation ends
     * @param archetype the archetype that is currently being validated
     * @return
     */
    protected void endValidation() {

    }

    protected void validate(CObject cObject) {
        if(cObject instanceof  CComplexObject) {
            validate((CComplexObject) cObject);
        } else if (cObject instanceof  CPrimitiveObject) {
            validate((CPrimitiveObject) cObject);
        } else if(cObject instanceof ArchetypeSlot){
            validate((ArchetypeSlot) cObject);
        } else if(cObject instanceof CComplexObjectProxy){
            validate((CComplexObjectProxy) cObject);
        }
    }

    /**
     * Override for validation on archetype slots
     * @param cObject
     * @return
     */
    protected void validate(ArchetypeSlot cObject) {
    }

    /**
     * Override for validation on complex object proxy constraints
     * @param cObject
     * @return
     */
    protected void validate(CComplexObjectProxy cObject) {
    }

    /**
     * Override for validation on complex objects
     * @param cObject
     * @return
     */
    protected void validate(CComplexObject cObject) {
    }

    protected void validate(CPrimitiveObject cObject) {
    }

    protected void validate(CAttribute cAttribute) {
    }

}
