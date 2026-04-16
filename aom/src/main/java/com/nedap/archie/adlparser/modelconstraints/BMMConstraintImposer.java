package com.nedap.archie.adlparser.modelconstraints;

import com.google.common.collect.Sets;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.BmmContainerProperty;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmProperty;

import java.util.Set;

public class BMMConstraintImposer implements ModelConstraintImposer {

    private final BmmModel model;

    private Set<String> nonOrderedContainerTypes = Sets.newHashSet("set", "hash", "bag");
    private Set<String> uniqueContainerTypes = Sets.newHashSet("set", "hash");

    public BMMConstraintImposer(BmmModel model) {
        this.model = model;
    }

    @Override
    public CAttribute getDefaultAttribute(String typeId, String attribute) {
        BmmProperty<?> property = model.propertyAtPath (typeId, attribute);
        if(property == null) {
            return null;
        }
        CAttribute result = new CAttribute();
        MultiplicityInterval existence = property.getExistence();
        result.setExistence(existence);

        if(property instanceof BmmContainerProperty) {
            BmmContainerProperty containerProperty = (BmmContainerProperty) property;
            Cardinality cardinality = new Cardinality();
            cardinality.setInterval(containerProperty.getCardinality());
            String name = containerProperty.getType().getContainerType().getName();
            cardinality.setOrdered(!nonOrderedContainerTypes.contains(name.toLowerCase()));
            cardinality.setUnique(uniqueContainerTypes.contains(name.toLowerCase()));

            result.setCardinality(cardinality);
            result.setMultiple(true);

        } else {
            result.setMultiple(false);
        }



        return result;
    }
}
