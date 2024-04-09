package com.nedap.archie.aom;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.xml.adapters.ArchetypeTerminologyAdapter;
import com.nedap.archie.xml.types.XmlArchetypeTerminology;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="OPERATIONAL_TEMPLATE")
@XmlRootElement(name="operational_template")
public class OperationalTemplate extends AuthoredArchetype {

    /**
     * terminology extracts from subarchetypes, for example snomed codes, multiple choice thingies, etc
     */
    @XmlTransient
    private Map<String, ArchetypeTerminology> terminologyExtracts = new ConcurrentHashMap<>();
    @XmlTransient
    private Map<String, ArchetypeTerminology> componentTerminologies = new ConcurrentHashMap<>();

    @XmlElement(name="terminology_extracts")
    @JsonIgnore
    //this field should be marked transient, but JAXB will not allow it.
    private List<XmlArchetypeTerminology> xmlTerminologyExtracts;
    @XmlElement(name="component_terminologies")
    @JsonIgnore
    //this field should be marked transient, but JAXB will not allow it.
    private List<XmlArchetypeTerminology> xmlComponentTerminologies;

    @Override
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        super.afterUnmarshal(unmarshaller, parent);
        if(xmlTerminologyExtracts != null) {
            ArchetypeTerminologyAdapter archetypeTerminologyAdapter = new ArchetypeTerminologyAdapter();
            for(XmlArchetypeTerminology terminology:xmlTerminologyExtracts) {
                try {
                    ArchetypeTerminology converted = archetypeTerminologyAdapter.unmarshal(terminology);
                    terminologyExtracts.put(terminology.getArchetypeId(), converted);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(xmlComponentTerminologies != null) {
            ArchetypeTerminologyAdapter archetypeTerminologyAdapter = new ArchetypeTerminologyAdapter();
            for(XmlArchetypeTerminology terminology:xmlComponentTerminologies) {
                try {
                    ArchetypeTerminology converted = archetypeTerminologyAdapter.unmarshal(terminology);
                    componentTerminologies.put(terminology.getArchetypeId(), converted);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Invoked by Marshaller after it has created an instance of this object.
    @Override
    public boolean beforeMarshal(Marshaller marshaller) {
        super.beforeMarshal(marshaller);
        if(terminologyExtracts != null && !terminologyExtracts.isEmpty()) {
            ArchetypeTerminologyAdapter archetypeTerminologyAdapter = new ArchetypeTerminologyAdapter();
            xmlTerminologyExtracts = new ArrayList<>();
            for(Map.Entry<String, ArchetypeTerminology> terminology:terminologyExtracts.entrySet()) {
                XmlArchetypeTerminology converted = archetypeTerminologyAdapter.marshal(terminology.getValue());
                converted.setArchetypeId(terminology.getKey());
                xmlTerminologyExtracts.add(converted);
            }
        } else {
            xmlTerminologyExtracts = null;
        }
        if(componentTerminologies != null && !componentTerminologies.isEmpty()) {
            ArchetypeTerminologyAdapter archetypeTerminologyAdapter = new ArchetypeTerminologyAdapter();
            xmlComponentTerminologies = new ArrayList<>();
            for(Map.Entry<String, ArchetypeTerminology> terminology:componentTerminologies.entrySet()) {
                XmlArchetypeTerminology converted = archetypeTerminologyAdapter.marshal(terminology.getValue());
                converted.setArchetypeId(terminology.getKey());
                xmlComponentTerminologies.add(converted);
            }
        } else {
            xmlComponentTerminologies = null;
        }
        return true;
    }


    public Map<String, ArchetypeTerminology> getTerminologyExtracts() {
        return terminologyExtracts;
    }

    public void setTerminologyExtracts(Map<String, ArchetypeTerminology> terminologyExtracts) {
        this.terminologyExtracts = terminologyExtracts;
    }

    public void addTerminologyExtract(String nodeId, ArchetypeTerminology terminology) {
        terminologyExtracts.put(nodeId, terminology);
    }

    public Map<String, ArchetypeTerminology> getComponentTerminologies() {
        return componentTerminologies;
    }

    public void setComponentTerminologies(Map<String, ArchetypeTerminology> componentTerminologies) {
        this.componentTerminologies = componentTerminologies;
    }

    public void addComponentTerminology(String nodeId, ArchetypeTerminology terminology) {
        componentTerminologies.put(nodeId, terminology);
    }

    /**
     * Get the last used archetype reference in the path of the given cObject.
     * If stripLastPartOfPath == true, ignore the last pathsegment, usable for finding
     * the id code of an archetype root
     * Returns null if the root archetype is found.
     * @param object
     * @param stripLastPartOfPath
     * @return
     */
    private String getChildArchetypeId(CObject object, boolean stripLastPartOfPath) {
        //optimization possible: walk back the tree until you find a node used in an archetype, instead of
        //getting the entire path
        List<PathSegment> pathSegments = object.getPathSegments();
        Collections.reverse(pathSegments);
        if(stripLastPartOfPath && pathSegments.size() > 1) {
            //the first path segment can point to a archetype root. We do not want to include that
            //but need the path from the parent archetype
            pathSegments = pathSegments.subList(1, pathSegments.size());
        } else if (stripLastPartOfPath) {
            //points to the root node. This should return null
            return null;
        }
        for(PathSegment segment:pathSegments) {
            //TODO: refactor PathSegment parsing so that archetype ref is NEVER in id code and always in archetype ref
            //then remove this if.
            if(segment.hasArchetypeRef()) {
                //this is [archetypeId] instead of [idcode]
                return segment.getNodeId();
            } else if(segment.getArchetypeRef() != null) {
                return segment.getArchetypeRef();
            }
        }
        return null;
    }

    /**
     * Get the ArchetypeTerm for the given CObject. This gets the term from the ComponentTerminologies if required.
     * @param object {@inheritDoc}
     * @param language {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public ArchetypeTerm getTerm(CObject object, String language) {
        return getTerm(object, object.getNodeId(), language);
    }

    /**
     * Get the ArchetypeTerm for the given code in the context of the cObject.
     * This gets the term from the ComponentTerminologies if required.
     * @param object {@inheritDoc}
     * @param language {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public ArchetypeTerm getTerm(CObject object, String code, String language) {
        boolean stripLastPartOfPath = object instanceof CArchetypeRoot && AOMUtils.isIdCode(code);
        ArchetypeTerm term = getTermInternal(object, code, language, stripLastPartOfPath);
        if(stripLastPartOfPath && term == null) {
            term = getTermInternal(object, code, language, false);
        }
        return term;
    }

    private ArchetypeTerm getTermInternal(CObject object, String code, String language, boolean stripLastPartOfPath) {
        String archetypeId = getChildArchetypeId(object, stripLastPartOfPath);

        if(archetypeId == null) {
            return super.getTerm(object, code, language);
        } else {
            ArchetypeTerminology terminology = getComponentTerminologies().get(archetypeId);
            if(terminology != null) {
                ArchetypeTerm term = terminology.getTermDefinition(language, code);
                if(term == null && object instanceof CArchetypeRoot) {
                    //for an archetype root without a term in the parent archetype, for some reason, just return the root node.
                    //this archetype shouldn't validate, but archie has done this since the first versions, so let's keep this behaviour
                    return terminology.getTermDefinition(language, ((CArchetypeRoot) object).getArchetypeRef());
                }
                return term;
            } else {
                //TODO: check if we should do this or just return null
                throw new IllegalStateException("Expected an archetype terminology for archetype id " + archetypeId);
            }
        }
    }


    public ArchetypeTerminology getTerminology(CObject object) {
        String archetypeId = getChildArchetypeId(object, false);
        if(archetypeId == null) {
            return getTerminology();
        } else {
            return getComponentTerminologies().get(archetypeId);
        }
    }
}

