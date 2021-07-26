package com.nedap.archie.flattener;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rules.Assertion;

import java.util.List;

public class FlattenerUtil {



    public static List<Assertion> getPossiblyOverridenListValue(List<Assertion> parent, List<Assertion> child) {
        if(child != null && !child.isEmpty()) {
            return child;
        }
        return parent;
    }

    public static <T> T getPossiblyOverridenValue(T parent, T specialized) {
        if(specialized != null) {
            return specialized;
        }
        return parent;
    }

    public static boolean shouldReplaceSpecializedParent(CObject parent, List<CObject> differentialNodes, MetaModels metaModels) {

        MultiplicityInterval occurrences = parent.effectiveOccurrences(metaModels::referenceModelPropMultiplicity);
        //isSingle/isMultiple is tricky and not doable just in the parser. Don't use those
        if(isSingle(parent.getParent(), metaModels)) {
            return true;
        } else if(occurrences != null && occurrences.upperIsOne()) {
            //REFINE the parent node case 1, the parent has occurrences upper == 1
            return true;
        } else if (differentialNodes.size() == 1) {
            MultiplicityInterval effectiveOccurrences;
            //the differentialNode can have a differential path instead of an attribute name. In that case, we need to replace the rm type name
            //of the parent with the actual typename in the parent archetype. Otherwise, it may fall back to the default type in the RM,
            //and that can be an abstract type that does not have the attribute that we are trying to constrain. For example:
            //diff archetype:
            // /events[id6]/data/items matches {
            //in the rm, data maps to an ITEM_STRUCTURE that does not have the attribute items.
            //in the parent archetype, that is then an ITEM_TREE. We need to use ITEM_TREE here, which is what this code accomplishes.
            if(parent.getParent() == null || parent.getParent().getParent() == null) {
                effectiveOccurrences = differentialNodes.get(0).effectiveOccurrences(metaModels::referenceModelPropMultiplicity);
            } else {
                effectiveOccurrences = differentialNodes.get(0).effectiveOccurrences((s, s2) -> metaModels.referenceModelPropMultiplicity(
                        parent.getParent().getParent().getRmTypeName(), parent.getParent().getRmAttributeName()));
            }
            if(effectiveOccurrences != null && effectiveOccurrences.upperIsOne()) {
                //REFINE the parent node case 2, only one child with occurrences upper == 1
                return true;
            }
        }
        return false;
    }

    public static boolean isSingle(CAttribute attribute, MetaModels metaModels) {
        if(attribute != null && attribute.getParent() != null && attribute.getDifferentialPath() == null) {
            return !metaModels.isMultiple(attribute.getParent().getRmTypeName(), attribute.getRmAttributeName());
        }
        return false;
    }

}
