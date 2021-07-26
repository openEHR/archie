package com.nedap.archie.opt14;

import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.opt14.schema.CARCHETYPEROOT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Performs an in-place conversion of the OPT2 to a source form template. Destorys the OPT2 in the process!
 *
 * Does not perform any diffing, just creates template overlays where required
 */
public class OptToTemplateConverter {
    private Template template;
    private OperationalTemplate opt2;

    public void convert(OperationalTemplate opt2, Template template) {
        this.template = template;
        this.opt2 = opt2;
        convert(opt2.getDefinition());
        template.setDefinition(opt2.getDefinition());
    }

    public void convert(CAttribute cAttribute) {
        LinkedHashMap<Integer, CObject> replacements = new LinkedHashMap<>();
        List<CObject> children = cAttribute.getChildren();
        int i = 0;
        for(CObject child: children) {
            CObject replacement = convert(child);
            if(replacement != null) {
                replacements.put(i, replacement);
            }
            i++;
        }

        for(Map.Entry<Integer, CObject> replacement: replacements.entrySet()) {
            int index = replacement.getKey();
            CObject constraint = replacement.getValue();
            children.set(index, constraint);
            constraint.setParent(cAttribute);
        }
    }

    public CObject convert(CObject cObject) {
        CObject replacement = null;
        if(cObject instanceof CArchetypeRoot) {
            replacement = convertRoot((CArchetypeRoot) cObject);
        }
        //but do continue processing, even if there is a replacement!
        for(CAttribute attribute:cObject.getAttributes()) {
            convert(attribute);
        }

        return replacement;
    }

    private CObject convertRoot(CArchetypeRoot cRoot14) {

        CComplexObject templateRoot = new CComplexObject();
        templateRoot.setNodeId("at0000");
        templateRoot.setRmTypeName(cRoot14.getRmTypeName());
        templateRoot.setAttributes(cRoot14.getAttributes());

        TemplateOverlay overlay = new TemplateOverlay();
        overlay.setArchetypeId(new ArchetypeHRID(cRoot14.getArchetypeRef()));
        overlay.getArchetypeId().setConceptId(overlay.getArchetypeId().getConceptId() + "ovl-1");
        overlay.setParentArchetypeId(cRoot14.getArchetypeRef());
        overlay.setDefinition(templateRoot);
        overlay.setTerminology(opt2.getComponentTerminologies().get(cRoot14.getArchetypeRef()));
        template.addTemplateOverlay(overlay);

        CArchetypeRoot root = new CArchetypeRoot();
        root.setArchetypeRef(overlay.getArchetypeId().getFullId());
        if(cRoot14.getNodeId() != null && !cRoot14.getNodeId().isEmpty() && !cRoot14.getNodeId().startsWith("at0000")) {
            root.setNodeId(cRoot14.getNodeId());
        }
        root.setRmTypeName(cRoot14.getRmTypeName());
        root.setOccurrences(cRoot14.getOccurrences());
        return root;
    }
}
