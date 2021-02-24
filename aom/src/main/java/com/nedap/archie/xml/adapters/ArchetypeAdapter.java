package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.xml.types.XmlArchetype;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ArchetypeAdapter extends XmlAdapter<XmlArchetype, Archetype> {
    @Override
    public Archetype unmarshal(XmlArchetype archetype) throws Exception {
        Archetype result = new AuthoredArchetype();
        fromXml(archetype, result);
        return result;
    }

    public static void fromXml(XmlArchetype archetype, Archetype result) {
        result.setTerminology(archetype.getTerminology());
        result.setArchetypeId(archetype.getArchetypeId());
        result.setAdlVersion(archetype.getAdlVersion());
        result.setBuildUid(archetype.getBuildUid());
        result.setDefinition(archetype.getDefinition());
        result.setDifferential(archetype.isDifferential());
        result.setGenerated(archetype.getGenerated());
        result.setOtherMetaData(StringDictionaryUtil.convertStringDictionaryListToStringMap(archetype.getOtherMetaData()));
        result.setParentArchetypeId(archetype.getParentArchetypeId());
        result.setRmRelease(archetype.getRmRelease());
        result.setRules( archetype.getRules());
        result.setAnnotations(archetype.getAnnotations());
        result.setAuthoredResourceContent(archetype.getAuthoredResourceContent());
        result.setDescription(archetype.getDescription());
        result.setTranslations(archetype.getTranslations());
        result.setControlled(archetype.getControlled());
        result.setUid(archetype.getUid());
        result.setOriginalLanguage(archetype.getOriginalLanguage());
    }

    @Override
    public XmlArchetype marshal(Archetype v) throws Exception {
        return new XmlArchetype(v);
    }
}
