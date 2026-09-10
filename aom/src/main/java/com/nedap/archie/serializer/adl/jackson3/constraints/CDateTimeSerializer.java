package com.nedap.archie.serializer.adl.jackson3.constraints;

import com.nedap.archie.aom.primitives.CDateTime;
import com.nedap.archie.serializer.adl.jackson3.ADLDefinitionSerializer;

/**
 * @author Marko Pipan
 */
public class CDateTimeSerializer extends CTemporalSerializer<CDateTime> {
    public CDateTimeSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }
}
