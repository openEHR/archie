package org.openehr.bmm.v2.validation.converters;

import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmContainerProperty;
import org.openehr.bmm.core.BmmContainerType;
import org.openehr.bmm.core.BmmGenericClass;
import org.openehr.bmm.core.BmmGenericProperty;
import org.openehr.bmm.core.BmmGenericType;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.core.BmmType;
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
        //getTypeDefinition().createBmmType(bmmSchema, classDefinition);
        BmmType type = new TypeCreator().createBmmType(property.getTypeRef(), schema, bmmClass);

        if(property instanceof PBmmSinglePropertyOpen) {
            return createSimpleProperty(property, type);
        } else if (property instanceof PBmmSingleProperty) {
            return createSimpleProperty(property, type);
        } else if (property instanceof PBmmGenericProperty) {
            return createGenericProperty(schema, (PBmmGenericProperty) property, (BmmGenericType) type, bmmClass);
        } else if (property instanceof PBmmContainerProperty) {
            return createContainerProperty((PBmmContainerProperty) property, (BmmContainerType) type);
        } else {
            throw new RuntimeException("unknown property class: " + property.getClass().getName());
        }

    }

    private BmmContainerProperty createContainerProperty(PBmmContainerProperty property, BmmContainerType type) {
        BmmContainerProperty bmmContainerProperty = new BmmContainerProperty(property.getName(), type);
        setBasics(property, bmmContainerProperty);
        if(property.getCardinality() != null) {
            Interval<Integer> cardinality = property.getCardinality();
            bmmContainerProperty.setCardinality(new MultiplicityInterval(cardinality.getLower(), cardinality.isLowerIncluded(),
                    cardinality.isLowerUnbounded(),
                    cardinality.getUpper(), cardinality.isUpperIncluded(),
                    cardinality.isUpperUnbounded()));
        }
        return bmmContainerProperty;
    }

    private BmmProperty createSimpleProperty(PBmmProperty property, BmmType typeDefinition) {
        BmmProperty bmmProperty = new BmmProperty(property.getName(), typeDefinition);
        setBasics(property, bmmProperty);
        return bmmProperty;
    }

    private BmmGenericProperty createGenericProperty(BmmModel schema, PBmmGenericProperty property, BmmGenericType typeDefinition, BmmClass bmmClass) {
        BmmGenericProperty bmmProperty = new BmmGenericProperty(property.getName(), typeDefinition);
        setBasics(property, bmmProperty);
        PBmmGenericType pbmmType = property.getTypeRef();
        BmmGenericType genericTypeDef = new BmmGenericType();
        genericTypeDef.setBaseClass((BmmGenericClass) schema.getClassDefinition(pbmmType.getRootType()));
        List<BmmType> genericParams = new ArrayList<>();
        TypeCreator typeCreator = new TypeCreator();
        for(PBmmType genericParamType: pbmmType.getGenericParameterRefs()) {
            genericParams.add(typeCreator.createBmmType(genericParamType, schema, bmmClass));
        }
        genericTypeDef.setGenericParameters(genericParams);
        bmmProperty.setGenericTypeDef(genericTypeDef);
        //bmmProperty.setGenericTypeDef();
        return bmmProperty;
    }

    private void setBasics(PBmmProperty property, BmmProperty bmmProperty) {
        bmmProperty.setDocumentation(property.getDocumentation());
        bmmProperty.setMandatory(property.isMandatory());
        bmmProperty.setComputed(property.isComputed());
        bmmProperty.setImInfrastructure(property.isImInfrastructure());
        bmmProperty.setImRuntime(property.isImRuntime());

        if(bmmProperty.getMandatory() == null) {
            bmmProperty.setMandatory(false);
        }
        if(bmmProperty.getComputed() == null) {
            bmmProperty.setComputed(false);
        }
        if(bmmProperty.getImInfrastructure() == null) {
            bmmProperty.setImInfrastructure(false);
        }

        if(bmmProperty.getImRuntime() == null) {
            bmmProperty.setImRuntime(false);
        }
    }


}
