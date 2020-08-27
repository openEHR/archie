package com.nedap.archie.diff;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.NodeIdUtil;
import com.nedap.archie.base.Interval;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;

import java.util.ArrayList;
import java.util.List;

public class UnconstrainedIntervalRemover {

    public static void removeUnconstrainedIntervals(Archetype archetype) {
        removeUnconstrainedIntervals(archetype.getDefinition());
        if(archetype instanceof Template) {
            Template template = (Template) archetype;
            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                removeUnconstrainedIntervals(overlay.getDefinition());
            }
        }
    }

    public static void removeUnconstrainedIntervals(CComplexObject cObject) {
        for(CAttribute attribute:cObject.getAttributes()) {
            removeUnconstrainedIntervals(attribute);
        }
    }

    public static void removeUnconstrainedIntervals(CAttribute cAttribute) {
        List<CObject> cObjectsToRemove = new ArrayList<>();
        for(CObject cObject:cAttribute.getChildren()) {
            if(cObject instanceof CComplexObject) {
                removeUnconstrainedIntervals((CComplexObject) cObject);
            } else if (cObject instanceof CPrimitiveObject) {
                CPrimitiveObject cPrimitiveObject = (CPrimitiveObject) cObject;
                List constraint = cPrimitiveObject.getConstraint();
                List<Object> toRemove = new ArrayList<>();
                for(Object i:constraint) {
                    if(i instanceof Interval) {
                        Interval interval = (Interval) i;
                        if(interval.isUpperUnbounded() && interval.isLowerUnbounded()) {
                            toRemove.add(i);
                        }
                    }
                }
                constraint.removeAll(toRemove);
                if(constraint.isEmpty()) {
                    cObjectsToRemove.add(cPrimitiveObject);
                }
            }
        }
        for(CObject cObject:cObjectsToRemove) {
            cAttribute.removeChild(cObject);
        }
    }
}
