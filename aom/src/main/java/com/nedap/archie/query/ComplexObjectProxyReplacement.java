package com.nedap.archie.query;

import com.nedap.archie.aom.*;

import java.util.List;

/**
 * little class used for a CompelxObjectProxyReplacement because we cannot replace in a collection
 * that we iterate at the same time
 */
public class ComplexObjectProxyReplacement {
    private final CComplexObject replacement;
    private final CComplexObjectProxy proxy;

    public ComplexObjectProxyReplacement(CComplexObjectProxy proxy, CComplexObject replacement) {
        this.proxy = proxy;
        this.replacement = replacement;
    }

    public void replace() {
        proxy.getParent().replaceChild(proxy.getNodeId(), replacement);
    }

    public static ComplexObjectProxyReplacement getComplexObjectProxyReplacement(CComplexObjectProxy proxy) {
        List<CObject> newObjects = new AOMPathQuery(proxy.getTargetPath()).findList(getNearestArchetypeRoot(proxy), true);
        if (newObjects == null || newObjects.isEmpty()) {
            return null;
        } else {
            CObject found = newObjects.get(0);
            CComplexObject clone = (CComplexObject) found.clone();
            clone.setNodeId(proxy.getNodeId());
            if (proxy.getOccurrences() != null) {
                clone.setOccurrences(proxy.getOccurrences());
            }
            if (proxy.getSiblingOrder() != null) {
                clone.setSiblingOrder(proxy.getSiblingOrder());
            }
            return new ComplexObjectProxyReplacement(proxy, clone);
        }
    }

    /**
     * Get the archetype root that is the most near parent. Either returns a C_ARCHETYPE_ROOT or the complex_object at archetype.getDefinition()
     * @return
     */
    public static CComplexObject getNearestArchetypeRoot(CObject object) {
        //find a C_ARCHETYPE_ROOT
        CAttribute parentAttribute = object.getParent();
        while(parentAttribute != null) {
            CObject parentObject = parentAttribute.getParent();
            if(parentObject == null) {
                break;
            }
            if(parentObject instanceof CArchetypeRoot) {
                return (CArchetypeRoot) parentObject;
            }
            parentAttribute = parentObject.getParent();
        }
        //C_ARCHETYPE_ROOT not found, return the archetype definition
        return object.getArchetype().getDefinition();

    }


    public CComplexObject getReplacement() {
        return replacement;
    }
}
