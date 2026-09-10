package com.nedap.archie.serializer.adl.jackson3.constraints;


import com.nedap.archie.aom.primitives.CTime;
import com.nedap.archie.serializer.adl.jackson3.ADLDefinitionSerializer;

/**
 * @author Marko Pipan
 */
public class CTimeSerializer extends CTemporalSerializer<CTime> {
    public CTimeSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }
}
