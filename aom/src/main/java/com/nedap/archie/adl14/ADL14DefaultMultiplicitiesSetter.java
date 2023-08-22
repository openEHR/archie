package com.nedap.archie.adl14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModels;

/**
 * Sets the default occurrences, cardinality and existence with ADL 1.4 rules, if not explicitly set
 * in a given Archetype. Useful for conversion to ADL 2, where the default values are different, and
 * it is good to start with the correct values already present
 */
public class ADL14DefaultMultiplicitiesSetter {

    private final MetaModels metaModels;

    public ADL14DefaultMultiplicitiesSetter(MetaModels metaModels) {
        this.metaModels = metaModels;
    }

    public void setDefaults(Archetype archetype) {
        metaModels.selectModel(archetype);
        correctItemsCardinality(archetype.getDefinition());
    }

    private void correctItemsCardinality(CObject cObject) {
        for(CAttribute attribute:cObject.getAttributes()) {
            // according to the specification, the following lines must be added.
            // however, in practice this is not followed, and adding it would
            // lead to more problems
          /*
           if(attribute.getCardinality() == null) {
                attribute.setCardinality(new Cardinality(0, 1));
            }
            if(attribute.getExistence() == null) {
                attribute.setExistence(new MultiplicityInterval(1, 1));
            }*/
            for(CObject child:attribute.getChildren()) {
                if(child.getOccurrences() == null &&
                        metaModels.isMultiple(cObject.getRmTypeName(), attribute.getRmAttributeName())) {
                    child.setOccurrences(new MultiplicityInterval(1, 1));
                }
                correctItemsCardinality(child);
            }
        }

    }
}
