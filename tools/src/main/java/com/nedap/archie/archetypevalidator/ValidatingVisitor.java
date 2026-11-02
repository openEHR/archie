package com.nedap.archie.archetypevalidator;

import com.nedap.archie.aom.*;

import java.util.ArrayDeque;

/**
 * Created by pieter.bos on 31/03/2017.
 */
public abstract class ValidatingVisitor extends ArchetypeValidationBase {


    public ValidatingVisitor() {
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
     */
    protected void beginValidation() {

    }

    /**
     * Override to get a callback when validation ends
     */
    protected void endValidation() {

    }

    protected void validate(CObject cObject) {
        if(cObject instanceof  CComplexObject) {
            validate((CComplexObject) cObject);
        } else if (cObject instanceof  CPrimitiveObject) {
            validate((CPrimitiveObject<?, ?>) cObject);
        } else if(cObject instanceof ArchetypeSlot){
            validate((ArchetypeSlot) cObject);
        } else if(cObject instanceof CComplexObjectProxy){
            validate((CComplexObjectProxy) cObject);
        }
    }

    /**
     * Override for validation on archetype slots
     * @param cObject the cobject to validate
     */
    protected void validate(ArchetypeSlot cObject) {
    }

    /**
     * Override for validation on complex object proxy constraints
     * @param cObject the cobject to validate
     */
    protected void validate(CComplexObjectProxy cObject) {
    }

    /**
     * Override for validation on complex objects
     * @param cObject the cobject to validate
     */
    protected void validate(CComplexObject cObject) {
    }

    protected void validate(CPrimitiveObject<?, ?> cObject) {
    }

    protected void validate(CAttribute cAttribute) {
    }

}
