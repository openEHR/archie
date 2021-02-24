package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.xml.types.XmlArchetype;
import com.nedap.archie.xml.types.XmlOperationalTemplate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OPTAdapter extends XmlAdapter<XmlOperationalTemplate, OperationalTemplate> {
    @Override
    public OperationalTemplate unmarshal(XmlOperationalTemplate archetype) throws Exception {
        OperationalTemplate result = new OperationalTemplate();
        IncludedTerminologyAdapter includedTerminologyAdapter = new IncludedTerminologyAdapter();
        ArchetypeAdapter.fromXml(archetype, result);
        result.setComponentTerminologies(includedTerminologyAdapter.unmarshal(archetype.getComponentTerminologies()));
        result.setComponentTerminologies(includedTerminologyAdapter.unmarshal(archetype.getComponentTerminologies()));
        return result;
    }

    @Override
    public XmlOperationalTemplate marshal(OperationalTemplate v) throws Exception {
        return new XmlOperationalTemplate(v);
    }
}
