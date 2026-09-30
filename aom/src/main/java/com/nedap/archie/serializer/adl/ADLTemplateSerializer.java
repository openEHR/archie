package com.nedap.archie.serializer.adl;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.rminfo.RMObjectMapperProvider;

import java.util.function.Function;

/**
 * @author markopi
 */
class ADLTemplateSerializer extends ADLAuthoredArchetypeSerializer<Template> {

    public ADLTemplateSerializer(Template archetype, Function<String, Archetype> flatArchetypeProvider, RMObjectMapperProvider rmObjectMapperProvider) {
        super(archetype, flatArchetypeProvider, rmObjectMapperProvider);
    }

    @Override
    protected String headTag() {
        return "template";
    }

    @Override
    protected String serialize() {
        super.serialize();
        archetype.getTemplateOverlays().forEach(this::appendTemplateOverlay);
        return builder.toString();
    }

    private void appendTemplateOverlay(TemplateOverlay templateOverlay) {
        builder.newline()
                .append("------------------------------------------------------------------------").newline()
                .append(ADLArchetypeSerializer.serialize(templateOverlay, flatArchetypeProvider, rmObjectMapperProvider));
    }
}
