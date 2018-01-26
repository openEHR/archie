package com.nedap.archie.query;

import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CComplexObjectProxy;
import com.nedap.archie.aom.CObject;

/**
 * little class used for a CompelxObjectProxyReplacement because we cannot replace in a collection
 * that we iterate at the same time
 */
public class ComplexObjectProxyReplacement {
    private final CComplexObject replacement;
    private final CComplexObjectProxy proxy;

    public ComplexObjectProxyReplacement(CComplexObjectProxy proxy, CComplexObject object) {
        this.proxy = proxy;
        this.replacement = object;
    }

    public void replace() {
        proxy.getParent().replaceChild(proxy.getNodeId(), replacement);
    }

    public static ComplexObjectProxyReplacement getComplexObjectProxyReplacement(CComplexObjectProxy proxy) {
        CObject newObject = new AOMPathQuery(proxy.getTargetPath()).find(getNearestArchetypeRoot(proxy));
        if (newObject == null) {
            return null;
        } else {
            CComplexObject clone = (CComplexObject) newObject.clone();
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
    private static CComplexObject getNearestArchetypeRoot(CObject object) {
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
