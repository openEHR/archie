package com.nedap.archie.adlparser.modelconstraints;

import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.rminfo.MetaModel;
import com.nedap.archie.rminfo.MetaModelInterface;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;

/**
 * ModelConstraintImposer that checks the constraint with java-reflection. javax.annotation.NonNull is implemented
 * as being NonNull. Other attributes are assumed to be non-null. Collection attributes are assumed to be 0..*
 *
 * Fully thread-safe, inexpensive to create as long as you cached your ModelInfoLookup
 *
 * Created by pieter.bos on 04/11/15.
 */
public class ReflectionConstraintImposer implements ModelConstraintImposer {

    /** Contains complex object structure of the specified model. Attributes NEVER will have children. Sorry bout that :)*/

    private MetaModelInterface lookup;

    public ReflectionConstraintImposer(ModelInfoLookup classLookup) {
        this.lookup = new MetaModel(classLookup, null);
    }

    public ReflectionConstraintImposer(MetaModelInterface metaModel) {
        this.lookup = metaModel;
    }

    private CAttribute createCAttribute(String typeId, String attributeName) {
        CAttribute attribute = new CAttribute();

        attribute.setMultiple(false);
        attribute.setRmAttributeName(attributeName);

        boolean nullable = lookup.isNullable(typeId, attributeName);
        if(nullable) {
            attribute.setExistence(new MultiplicityInterval(0, 1));
        } else {
            attribute.setExistence(new MultiplicityInterval(1, 1));
        }

        if(lookup.isMultiple(typeId, attributeName)) {
            if(nullable) {
                attribute.setCardinality(Cardinality.unbounded());
            } else {
                attribute.setCardinality(Cardinality.mandatoryAndUnbounded());
            }

            attribute.setMultiple(true);
        } else {
            //only for container attributes (list, set, etc)
            attribute.setCardinality(null);
            attribute.setMultiple(false);
        }
        return attribute;
    }

    @Override
    public CAttribute getDefaultAttribute(String typeId, String attribute) {
        if(!lookup.attributeExists(typeId, attribute)) {
            return null;
        }
        return createCAttribute(typeId, attribute);
    }


}
