package org.openehr.bmm.v2.validation.converters;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmDefinedType;
import org.openehr.bmm.core.BmmModel;

import java.util.Map;

public class DescendantsCalculator {

    public void calculateDescendants(BmmModel model) {

        final Map<String, BmmClass> classDefinitions = model.getClassDefinitions();

        for (BmmClass bmmClass:classDefinitions.values()) {
            for (BmmDefinedType ancestor : bmmClass.getAncestors().values()) {
                ancestor.getBaseClass().addImmediateDescendant(bmmClass.getName());
            }
        }
    }
}
