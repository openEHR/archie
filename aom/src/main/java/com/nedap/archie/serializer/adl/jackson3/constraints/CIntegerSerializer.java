package com.nedap.archie.serializer.adl.jackson3.constraints;


import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.serializer.adl.jackson3.ADLDefinitionSerializer;

/**
 * @author Marko Pipan
 */
public class CIntegerSerializer extends COrderedSerializer<CInteger> {
    public CIntegerSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }


}
