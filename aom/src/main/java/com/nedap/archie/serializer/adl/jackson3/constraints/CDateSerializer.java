package com.nedap.archie.serializer.adl.jackson3.constraints;


import com.nedap.archie.aom.primitives.CDate;
import com.nedap.archie.serializer.adl.jackson3.ADLDefinitionSerializer;

/**
 * @author Marko Pipan
 */
public class CDateSerializer extends CTemporalSerializer<CDate> {
    public CDateSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

}
