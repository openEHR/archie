package com.nedap.archie.serializer.adl.constraints;


import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;

import java.time.temporal.TemporalAmount;

/**
 * @author Marko Pipan
 */
public class CDurationSerializer extends CTemporalSerializer<CDuration> {
    public CDurationSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

    @Override
    protected String serializeConstraintValue(Object value) {
        // value is always a TemporalAmount
        return DateTimeSerializerFormatters.serializeDuration((TemporalAmount) value);
    }
}
