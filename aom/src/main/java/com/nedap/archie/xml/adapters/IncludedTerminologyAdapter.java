package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.xml.types.XMLIncludedTerminology;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IncludedTerminologyAdapter extends XmlAdapter<ArrayList<XMLIncludedTerminology>, Map<String, ArchetypeTerminology>> {
    @Override
    public ArrayList<XMLIncludedTerminology> marshal(Map<String, ArchetypeTerminology> v) throws Exception {
        ArrayList<XMLIncludedTerminology> result = new ArrayList<>();
        for(String id:v.keySet()) {
            ArchetypeTerminology terminology = v.get(id);
            if(terminology != null) {
                XMLIncludedTerminology xmlTerm = new XMLIncludedTerminology();
                xmlTerm.setId(id);
                ArchetypeTerminologyAdapter.toXml(terminology, xmlTerm);
                result.add(xmlTerm);
            }

        }
        return result;
    }

    @Override
    public Map<String, ArchetypeTerminology> unmarshal(ArrayList<XMLIncludedTerminology> v) throws Exception {
        Map<String, ArchetypeTerminology> result = new LinkedHashMap<>();
        for(XMLIncludedTerminology t:v) {
            ArchetypeTerminology terminology = ArchetypeTerminologyAdapter.fromXml(t);
            result.put(t.getId(), terminology);
        }
        return result;
    }
}
