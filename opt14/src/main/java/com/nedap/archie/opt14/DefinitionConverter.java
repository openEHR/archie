package com.nedap.archie.opt14;

import com.google.common.base.Strings;
import com.nedap.archie.adl14.ADL14ConversionConfiguration;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;

import static com.nedap.archie.opt14.BaseTypesConverter.convertCardinality;
import static com.nedap.archie.opt14.BaseTypesConverter.convertMultiplicity;
import static com.nedap.archie.opt14.PrimitiveConverter.convertPrimitive;

import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.opt14.schema.*;

public class DefinitionConverter {

    private OPERATIONALTEMPLATE opt14;
    private OperationalTemplate template;
    private ADL14ConversionConfiguration config;

    public void convert(OperationalTemplate template, OPERATIONALTEMPLATE opt14, ADL14ConversionConfiguration config) {
        this.config = config;
        this.opt14 = opt14;
        this.template = template;
        CARCHETYPEROOT definition = opt14.getDefinition();
        template.setTerminology(TerminologyConverter.createTerminology(opt14, definition, config));
        template.setDefinition(convert(definition));
    }

    private CComplexObject convert(CCOMPLEXOBJECT cComplexObject14) {
        CComplexObject cComplexObject = new CComplexObject();
        setObjectBasics(cComplexObject14, cComplexObject);

        for (CATTRIBUTE attribute14 : cComplexObject14.getAttributes()) {
            cComplexObject.addAttribute(convert(attribute14));
        }
        return cComplexObject;
    }

    private void setObjectBasics(COBJECT cComplexObject14, CObject cComplexObject) {
        cComplexObject.setNodeId(Strings.isNullOrEmpty(cComplexObject14.getNodeId()) ? null : cComplexObject14.getNodeId());
        cComplexObject.setRmTypeName(cComplexObject14.getRmTypeName());
        cComplexObject.setOccurrences(BaseTypesConverter.convertMultiplicity(cComplexObject14.getOccurrences()));
    }

    private CAttribute convert(CATTRIBUTE attribute14) {
        CAttribute attribute = new CAttribute();
        attribute.setRmAttributeName(attribute14.getRmAttributeName());
        attribute.setExistence(convertMultiplicity(attribute14.getExistence()));
        if(attribute14 instanceof  CMULTIPLEATTRIBUTE) {
            CMULTIPLEATTRIBUTE attr14Multiple = (CMULTIPLEATTRIBUTE) attribute14;
            attribute.setCardinality(convertCardinality(attr14Multiple.getCardinality()));
            attribute.setMultiple(true);
        } else if (attribute14 instanceof CSINGLEATTRIBUTE) {
            //ok no one cares about this class
            attribute.setMultiple(false);
        }
        for(COBJECT cobject14:attribute14.getChildren()) {
            CObject cObject = convert(cobject14);
            if(cObject != null) {
                attribute.addChild(cObject);
            }
        }
        return attribute;
    }

    private CObject convert(COBJECT cobject14) {
        if(cobject14 instanceof CARCHETYPEROOT) {
            return convertRoot((CARCHETYPEROOT) cobject14);
        } else if (cobject14 instanceof CCOMPLEXOBJECT) {
            return convert((CCOMPLEXOBJECT) cobject14);
        } else if (cobject14 instanceof ARCHETYPESLOT) {
            return convertSlot((ARCHETYPESLOT) cobject14);
        } else if (cobject14 instanceof CPRIMITIVEOBJECT) {
            return convertPrimitive((CPRIMITIVEOBJECT) cobject14);
        } else if (cobject14 instanceof CDOMAINTYPE) {
            return DomainTypeConverter.convertDomainType((CDOMAINTYPE) cobject14);
        }
        throw new IllegalArgumentException("unknown COBJECT subtype: " + cobject14.getClass());
    }



    private CObject convertSlot(ARCHETYPESLOT cobject14) {
        ArchetypeSlot archetypeSlot = new ArchetypeSlot();
        setObjectBasics(cobject14, archetypeSlot);

        //TODO: assertions for include/exclude, should be straightforward
        return archetypeSlot;
    }

    private CObject convertRoot(CARCHETYPEROOT cRoot14) {

        ArchetypeTerminology terminology = TerminologyConverter.createTerminology(opt14, cRoot14, config);
        CArchetypeRoot root = new CArchetypeRoot();
        root.setArchetypeRef(cRoot14.getArchetypeId().getValue());
        if(cRoot14.getNodeId() != null && !cRoot14.getNodeId().isEmpty() && !cRoot14.getNodeId().startsWith("at0000")) {
            root.setNodeId(cRoot14.getNodeId());
        }
        root.setRmTypeName(cRoot14.getRmTypeName());
        root.setOccurrences(BaseTypesConverter.convertMultiplicity(cRoot14.getOccurrences()));

        setObjectBasics(cRoot14, root);

        for (CATTRIBUTE attribute14 : cRoot14.getAttributes()) {
            root.addAttribute(convert(attribute14));
        }

        template.addComponentTerminology(cRoot14.getArchetypeId().getValue(), terminology);
        return root;
    }


}
