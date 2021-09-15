package com.nedap.archie.opt14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ResourceAnnotations;
import com.nedap.archie.aom.rmoverlay.RmAttributeVisibility;
import com.nedap.archie.aom.rmoverlay.RmOverlay;
import com.nedap.archie.aom.rmoverlay.VisibilityType;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.opt14.schema.ANNOTATION;
import com.nedap.archie.opt14.schema.OPERATIONALTEMPLATE;
import com.nedap.archie.opt14.schema.StringDictionaryItem;
import com.nedap.archie.opt14.schema.TVIEW;

import java.util.LinkedHashMap;
import java.util.Map;

public class TViewConverter {

    public static void apply(OPERATIONALTEMPLATE opt14, Archetype adl2Archetype) {
        TVIEW view = opt14.getView();
        if (view != null) {
            if (view.getConstraints() != null) {
                RmOverlay overlay = adl2Archetype.getRmOverlay();
                if (overlay == null) {
                    adl2Archetype.setRmOverlay(new RmOverlay());
                    overlay = adl2Archetype.getRmOverlay();
                }
                Map<String, RmAttributeVisibility> rmVisibility = overlay.getRmVisibility();
                if (rmVisibility == null) {
                    overlay.setRmVisibility(new LinkedHashMap<>());
                    rmVisibility = overlay.getRmVisibility();
                }
                for (TVIEW.Constraints constraints : view.getConstraints()) {
                    for (TVIEW.Constraints.Items item : constraints.getItems()) {
                        RmAttributeVisibility attributeVisibility = rmVisibility.get(constraints.getPath());
                        if(attributeVisibility == null) {
                            attributeVisibility = new RmAttributeVisibility();
                            rmVisibility.put(constraints.getPath(), attributeVisibility);
                        }
                        switch (item.getId()) {
                            case "pass_through":
                                if("true".equals(item.getValue()) || new Boolean(true).equals(item.getValue())) {
                                    attributeVisibility.setVisibility(VisibilityType.SHOW);
                                } else {
                                    attributeVisibility.setVisibility(VisibilityType.HIDE);
                                }
                                break;
                            case "VisibleInView":
                                //create at code and link alias to terminology
                                String atCode = adl2Archetype.generateNextValueCode();
                                attributeVisibility.setAlias(TerminologyCode.createFromString("local", null, atCode));
                                adl2Archetype.getTerminology().getTermDefinitions().forEach((language, terms) -> {
                                    terms.put(atCode, new ArchetypeTerm(atCode, (String) item.getValue(), (String) item.getValue()));
                                });
                                break;
                        }

                    }
                }
            }
        }
    }
}
