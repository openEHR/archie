package org.openehr.bmm.v2.validation.converters;

import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.*;
import org.openehr.bmm.v2.persistence.PBmmContainerProperty;
import org.openehr.bmm.v2.persistence.PBmmGenericProperty;
import org.openehr.bmm.v2.persistence.PBmmGenericType;
import org.openehr.bmm.v2.persistence.PBmmProperty;
import org.openehr.bmm.v2.persistence.PBmmSingleProperty;
import org.openehr.bmm.v2.persistence.PBmmSinglePropertyOpen;
import org.openehr.bmm.v2.persistence.PBmmType;

import java.util.ArrayList;
import java.util.List;

public class BmmPropertyCreator {

    public BmmProperty createBmmProperty(PBmmProperty property, BmmModel schema, BmmClass bmmClass) {
        return property.createBmmProperty(schema, bmmClass);
    }

}
