package com.nedap.archie.flattener;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.utils.AOMUtils;

class TupleFlattener {


    public TupleFlattener() {

    }

    protected void flattenTuple(CComplexObject newObject, CAttributeTuple tuple) {
        CAttributeTuple matchingTuple = AOMUtils.findMatchingTuple(newObject.getAttributeTuples(), tuple);


        CAttributeTuple tupleClone = (CAttributeTuple) tuple.clone();
        if(matchingTuple == null) {
            //add
            newObject.addAttributeTuple(tupleClone);
        } else {
            //replace
            newObject.getAttributeTuples().remove(matchingTuple);
            newObject.addAttributeTuple(tupleClone);
        }
        for(CAttribute attribute:tupleClone.getMembers()){
            //replace the entire attribute with the attribute from the new tuple
            //this should be all there is to do.
            newObject.replaceAttribute(attribute);
        }
        //update all parent references
        for(CPrimitiveTuple primitiveTuple:tupleClone.getTuples()) {
            int i = 0;
            for(CPrimitiveObject<?, ?> object:primitiveTuple.getMembers()) {
                object.setSocParent(primitiveTuple);
                object.setParent((tupleClone.getMembers().get(i)));
                i++;
            }
        }
    }
}
