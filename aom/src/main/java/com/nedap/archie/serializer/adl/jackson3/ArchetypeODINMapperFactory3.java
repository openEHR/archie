package com.nedap.archie.serializer.adl.jackson3;

import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.serializer.adl.jackson.ArchetypeTerminologyMixin;
import org.openehr.odin.jackson3.ODINMapper3;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;

public class ArchetypeODINMapperFactory3 {

    public ObjectMapper createMapper() {
        SimpleModule module = new SimpleModule("archetype-odin-module");
        module.addSerializer(ArchetypeTerm.class, new ArchetypeTermOdinSerializer());
        module.setMixInAnnotation(ArchetypeTerminology.class, ArchetypeTerminologyMixin.class);

        return ODINMapper3.builder()
                .deactivateDefaultTyping()
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .addModule(module)
                .build();
    }
}
