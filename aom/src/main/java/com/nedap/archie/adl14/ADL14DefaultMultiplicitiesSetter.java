package com.nedap.archie.adl14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModels;

/**
 * Sets the default occurrences with ADL 1.4 rules ({1..1}), if not explicitly set in a given Archetype.
 * Useful for conversion to ADL 2, where the default values are different, and it is good to start
 * with the correct values already present.
 * <p>
 * Cardinality and existence are also specified to have a default value. However, this is not used
 * in practice (source, several openEHR community members). Adding that to the conversion would
 * lead to problems. So these are left out.
 */
public class ADL14DefaultMultiplicitiesSetter {

    private final MetaModels metaModels;

    public ADL14DefaultMultiplicitiesSetter(MetaModels metaModels) {
        this.metaModels = metaModels;
    }

    public void setDefaults(Archetype archetype) {
        metaModels.selectModel(archetype);
        correctItemsMultiplicities(archetype.getDefinition());
    }

    private void correctItemsMultiplicities(CObject cObject) {
        for (CAttribute attribute : cObject.getAttributes()) {
            for (CObject child : attribute.getChildren()) {
                if (child.getOccurrences() == null && metaModels.isMultiple(cObject.getRmTypeName(), attribute.getRmAttributeName())) {
                    child.setOccurrences(new MultiplicityInterval(1, 1));
                }
                correctItemsMultiplicities(child);
            }
        }

    }
}
