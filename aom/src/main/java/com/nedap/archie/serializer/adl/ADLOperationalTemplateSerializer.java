package com.nedap.archie.serializer.adl;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.rminfo.RMObjectMapperProvider;

import java.util.function.Function;

/**
 * @author markopi
 */
class ADLOperationalTemplateSerializer extends ADLAuthoredArchetypeSerializer<OperationalTemplate> {

    // Default constructor — uses the Jackson 2 backed ADLStringBuilder.
    public ADLOperationalTemplateSerializer(OperationalTemplate archetype, Function<String, Archetype> flatArchetypeProvider, RMObjectMapperProvider rmObjectMapperProvider) {
        super(archetype, flatArchetypeProvider, rmObjectMapperProvider);
    }

    // Use this constructor to supply a custom ADLStringBuilder, e.g. new ADLStringBuilder3() for Jackson 3.
    public ADLOperationalTemplateSerializer(OperationalTemplate archetype, Function<String, Archetype> flatArchetypeProvider, RMObjectMapperProvider rmObjectMapperProvider, ADLStringBuilder builder) {
        super(archetype, flatArchetypeProvider, rmObjectMapperProvider, builder);
    }

    @Override
    protected String serialize() {
        super.serialize();
        builder.newline().append("component_terminologies").newIndentedLine()
                .append("component_terminologies = < ") //todo: this should perhaps be in the ODIN serializer?
                .indent()
                .odin(archetype.getComponentTerminologies())
                .unindent()
                .append("> ")
                .unindent();
        return builder.toString();
    }

    @Override
    protected String headTag() {
        return "operational_template";
    }
}
