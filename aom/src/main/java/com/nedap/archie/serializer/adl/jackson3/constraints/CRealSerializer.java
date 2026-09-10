package com.nedap.archie.serializer.adl.jackson3.constraints;


import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.serializer.adl.jackson3.ADLDefinitionSerializer;

/**
 * @author Marko Pipan
 */
public class CRealSerializer extends COrderedSerializer<CReal> {
    public CRealSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }
}
