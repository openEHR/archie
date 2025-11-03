package com.nedap.archie.serializer.adl.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import org.openehr.odin.jackson.ODINMapper;

public class ArchetypeODINMapperFactory {

    public ODINMapper createMapper() {
        ODINMapper result = new ODINMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ArchetypeTerm.class, new ArchetypeTermOdinSerializer());
        module.setMixInAnnotation(ArchetypeTerminology.class, ArchetypeTerminologyMixin.class);
        result.disableDefaultTyping();//no typing info for archetype ODIN needed
        result.registerModule(module);
        return result;
    }
}
