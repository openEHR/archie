package com.nedap.archie.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import org.threeten.extra.PeriodDuration;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;

/**
 * A Jackson module that overrides the standard JavaTimeModule serializers and deserializers for ISO 8601 Durations.
 * It supports the negative format as used in OpenEHR, and it can parse PeriodDurations if required
 */
public class ArchieDurationModule extends SimpleModule {

    public ArchieDurationModule() {
        super("archie-duration-module");

        addDeserializer(TemporalAmount.class, new com.nedap.archie.json.DurationDeserializer());

        addSerializer(Duration.class, new DurationSerializer());
        addSerializer(TemporalAmount.class, new DurationSerializer());
        addSerializer(Period.class, new DurationSerializer());
        addSerializer(PeriodDuration.class, new DurationSerializer());
    }
}
