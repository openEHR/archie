package com.nedap.archie.rminfo;

import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import org.openehr.rm.archetyped.FeederAudit;
import org.openehr.rm.composition.Composition;
import org.openehr.rm.composition.Observation;
import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.Event;
import org.openehr.rm.datastructures.History;
import org.openehr.rm.datastructures.Item;
import org.openehr.rm.datavalues.DvEHRURI;
import org.openehr.rm.datavalues.DvIdentifier;
import org.openehr.rm.datavalues.DvURI;
import org.openehr.rm.datavalues.encapsulated.DvParsable;
import org.openehr.rm.datavalues.quantity.DvQuantity;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.identification.UID;
import org.openehr.rm.support.identification.UIDBasedId;
import org.openehr.rm.support.identification.UUID;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by stefan.teijgeler on 31/08/16.
 */
public class openEhrRMInfoLookupTest {

    private ModelInfoLookup modelInfoLookup = OpenEhrRmInfoLookup.getInstance();

    @Test
    public void classForRMType() {
        // Not an exhaustive list, just a few examples and edge cases
        assertEquals(Composition.class, modelInfoLookup.getClass("COMPOSITION"));
        assertEquals(Observation.class, modelInfoLookup.getClass("OBSERVATION"));
        assertEquals(Cluster.class, modelInfoLookup.getClass("CLUSTER"));
        assertEquals(DvParsable.class, modelInfoLookup.getClass("DV_PARSABLE"));
        assertEquals(DvURI.class, modelInfoLookup.getClass("DV_URI"));
        assertEquals(DvEHRURI.class, modelInfoLookup.getClass("DV_EHR_URI"));
        assertEquals(UIDBasedId.class, modelInfoLookup.getClass("UID_BASED_ID"));
        assertEquals(UID.class, modelInfoLookup.getClass("UID"));
        assertEquals(UUID.class, modelInfoLookup.getClass("UUID"));
        assertEquals(ArchetypeID.class, modelInfoLookup.getClass("ARCHETYPE_ID"));
    }

    @Test
    public void typeInfoForClass() {
        RMTypeInfo typeInfo = modelInfoLookup.getTypeInfo(Observation.class);

        assertEquals(Observation.class, typeInfo.getJavaClass());
        assertEquals("OBSERVATION", typeInfo.getRmName());
        assertEquals("data", typeInfo.getAttribute("data").getRmName());
    }

    @Test
    public void typeInfoForString() {
        RMTypeInfo typeInfo = modelInfoLookup.getTypeInfo("OBSERVATION");

        assertEquals(Observation.class, typeInfo.getJavaClass());
        assertEquals("OBSERVATION", typeInfo.getRmName());
        assertEquals("data", typeInfo.getAttribute("data").getRmName());
    }

    @Test
    public void attributeInfo() {
        RMAttributeInfo attributeInfo = modelInfoLookup.getAttributeInfo(Observation.class, "data");

        assertEquals(attributeInfo.getRmName(), "data");
        assertEquals(attributeInfo.getField().getName(), "data");
        assertEquals(attributeInfo.getGetMethod().getName(), "getData");
        assertEquals(attributeInfo.getSetMethod().getName(), "setData");
        assertEquals(attributeInfo.getType(), History.class);
        assertEquals(attributeInfo.getTypeInCollection(), History.class);
        assertEquals(attributeInfo.isMultipleValued(), false);
    }

    @Test
    public void attributeInfoOfCollection() {
        RMAttributeInfo attributeInfo = modelInfoLookup.getAttributeInfo(FeederAudit.class, "originating_system_item_ids");

        assertEquals(attributeInfo.getType(), List.class);
        assertEquals(attributeInfo.getTypeInCollection(), DvIdentifier.class);
        assertEquals(attributeInfo.isMultipleValued(), true);
    }

    @Test
    public void attributeInfoOfParameterizedCollectionType() {
        RMAttributeInfo attributeInfo = modelInfoLookup.getAttributeInfo(History.class, "events");

        assertEquals(attributeInfo.getType(), List.class);
        assertEquals(attributeInfo.getTypeInCollection(), Event.class);
        assertEquals(attributeInfo.isMultipleValued(), true);
    }

    @Test
    public void attributeInfoOfParameterizedExtendsCollectionType() {
        RMAttributeInfo attributeInfo = modelInfoLookup.getAttributeInfo(Cluster.class, "items");

        assertEquals(attributeInfo.getType(), List.class);
        assertEquals(attributeInfo.getTypeInCollection(), Item.class);
        assertEquals(attributeInfo.isMultipleValued(), true);
    }

    @Test
    public void getField() throws Exception {
        Field items = OpenEhrRmInfoLookup.getInstance().getField(Cluster.class, "items");
        assertEquals(Cluster.class.getDeclaredField("items"), items);
    }

    @Test
    public void rmTypeInfo() {
        RMTypeInfo uriInfo = modelInfoLookup.getTypeInfo("DV_URI");
        assertEquals("DV_URI", uriInfo.getRmName());
        assertEquals(DvURI.class, uriInfo.getJavaClass());
        RMAttributeInfo valueAttribute = uriInfo.getAttribute("value");
        assertNotNull(valueAttribute);
        assertEquals("value", valueAttribute.getRmName());
        assertEquals(URI.class, valueAttribute.getType());
        assertFalse(valueAttribute.isNullable());
        assertNotNull(valueAttribute.getGetMethod());
        assertNotNull(valueAttribute.getSetMethod());
        assertNull(valueAttribute.getAddMethod());
    }

    @Test
    public void genericType() {
        RMTypeInfo quantityInfo = modelInfoLookup.getTypeInfo("DV_QUANTITY");
        assertEquals("DV_QUANTITY", quantityInfo.getRmName());
        assertEquals(DvQuantity.class, quantityInfo.getJavaClass());
        RMAttributeInfo valueAttribute = quantityInfo.getAttribute("magnitude");
        assertNotNull(valueAttribute);
        assertEquals("magnitude", valueAttribute.getRmName());
        assertFalse(valueAttribute.isNullable());
        assertEquals("DvQuantity extends DvAmount<Double> should have a double magnitude field", Double.class, valueAttribute.getType());

    }

    @Test
    public void addMethod() {
        RMTypeInfo itemListInfo = modelInfoLookup.getTypeInfo("ITEM_LIST");
        assertEquals("addItem", itemListInfo.getAttribute("items").getAddMethod().getName());
    }

    @Test
    public void nullable() {
        RMAttributeInfo precisionInfo = modelInfoLookup.getAttributeInfo("DV_QUANTITY", "precision");
        assertTrue(precisionInfo.isNullable());
    }




}
