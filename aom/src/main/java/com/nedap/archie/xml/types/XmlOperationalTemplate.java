package com.nedap.archie.xml.types;

import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.xml.adapters.IncludedTerminologyAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="archetype")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="OPERATIONAL_TEMPLATE")
public class XmlOperationalTemplate extends XmlArchetype {

    /**
     * terminology extracts from subarchetypes, for example snomed codes, multiple choice thingies, etc
     */
    @XmlElement(name="terminology_extracts")
    private List<XmlIncludedTerminology> terminologyExtracts = new ArrayList<>();
    @XmlElement(name="component_terminologies")
    private List<XmlIncludedTerminology> componentTerminologies = new ArrayList<>();

    public XmlOperationalTemplate() {

    }

    public XmlOperationalTemplate(OperationalTemplate t) throws Exception {
        super(t);
        IncludedTerminologyAdapter includedTerminologyAdapter = new IncludedTerminologyAdapter();
        setTerminologyExtracts(includedTerminologyAdapter.marshal(t.getTerminologyExtracts()));
        setComponentTerminologies(includedTerminologyAdapter.marshal(t.getComponentTerminologies()));
    }

    public List<XmlIncludedTerminology> getTerminologyExtracts() {
        return terminologyExtracts;
    }

    public void setTerminologyExtracts(List<XmlIncludedTerminology> terminologyExtracts) {
        this.terminologyExtracts = terminologyExtracts;
    }

    public List<XmlIncludedTerminology> getComponentTerminologies() {
        return componentTerminologies;
    }

    public void setComponentTerminologies(List<XmlIncludedTerminology> componentTerminologies) {
        this.componentTerminologies = componentTerminologies;
    }
}
