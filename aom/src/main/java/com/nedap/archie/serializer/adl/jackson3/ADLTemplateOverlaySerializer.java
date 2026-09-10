package com.nedap.archie.serializer.adl.jackson3;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.rminfo.RMObjectMapperProvider3;

import java.util.function.Function;

/**
 * @author markopi
 */
class ADLTemplateOverlaySerializer extends ADLArchetypeSerializer<TemplateOverlay> {


    public ADLTemplateOverlaySerializer(TemplateOverlay archetype, Function<String, Archetype> flatArchetypeProvider, RMObjectMapperProvider3 rmObjectMapperProvider) {
        super(archetype, flatArchetypeProvider, rmObjectMapperProvider);
    }

    @Override
    protected String headTag() {
        return "template_overlay";
    }

    @Override
    protected void appendHeaderAttributes() {
    }

    @Override
    protected void appendLanguage() {
    }

    @Override
    protected void appendDescription() {
    }

    @Override
    protected void appendRules() {

    }
}
