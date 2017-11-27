package org.openehr.bmm.persistence.deserializer;

/*
 * #%L
 * OpenEHR - Java Model Stack
 * %%
 * Copyright (C) 2016 - 2017  Cognitive Medical Systems, Inc (http://www.cognitivemedicine.com).
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 * Author: Claude Nanjo
 */

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmGenericClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.persistence.*;
import org.openehr.bmm.persistence.serializer.BmmSchemaSerializer;
import org.openehr.odin.CompositeOdinObject;
import org.openehr.odin.antlr.OdinVisitorImpl;
import org.openehr.odin.loader.OdinLoaderImpl;
import org.openehr.utils.message.MessageLogger;

import java.util.Map;

import static org.junit.Assert.*;

@Ignore
public class BmmSchemaDeserializerTest1 {


    private PersistedBmmSchema persistedBmmSchema;

    @Before
    public void setup() {
        OdinLoaderImpl loader = new OdinLoaderImpl();
        OdinVisitorImpl visitor = loader.loadOdinFile(BmmSchemaDeserializerTest1.class.getResourceAsStream("/cimi/TestBmm1.bmm"));
        CompositeOdinObject root = visitor.getAstRootNode();
        BmmSchemaDeserializer deserializer = new BmmSchemaDeserializer();
        persistedBmmSchema = deserializer.deserialize(root);
        persistedBmmSchema.loadFinalize();
        persistedBmmSchema.validate();
        MessageLogger errorCache = persistedBmmSchema.getBmmSchemaValidator().getMessageLogger();
        System.out.println(errorCache);
    }

    @Test
    public void testFlattening() {
        persistedBmmSchema.createBmmSchema();
        BmmModel model = persistedBmmSchema.getBmmModel();
        BmmClass nonFlattenedClass = model.getClassDefinition("GrandChildType1");
        assertNotNull(nonFlattenedClass);
        assertTrue(nonFlattenedClass instanceof BmmGenericClass);
        BmmClass flattenedClass = nonFlattenedClass.flattenBmmClass();
        assertTrue(flattenedClass instanceof BmmGenericClass);
        assertEquals(4, flattenedClass.getProperties().size());
        assertNotNull(flattenedClass.getProperties().get("property_1"));
        assertNotNull(flattenedClass.getProperties().get("property_2"));
        assertNotNull(flattenedClass.getProperties().get("property_3"));
        assertNotNull(flattenedClass.getProperties().get("property_4"));
    }

    @Test
    public void testSchemaDeserialiation() {
        deserializeSchema(persistedBmmSchema);
    }

    public void deserializeSchema(PersistedBmmSchema schema) {
        Map<String, PersistedBmmPackage> packages = persistedBmmSchema.getPackages();
        assertNotNull(schema);
        testHeader();
        testIncludes();
        testPackageStructure();
        testClassDefinitions(schema);
        testPrimitiveDefinitions(schema);
    }

    public void testHeader() {
        assertEquals("My publisher", persistedBmmSchema.getRmPublisher());
        assertEquals("3.1", persistedBmmSchema.getRmRelease());
        assertEquals("Test1", persistedBmmSchema.getSchemaName());
        assertEquals("Monday, October 19, 2015", persistedBmmSchema.getSchemaRevision());
        assertEquals("test", persistedBmmSchema.getSchemaLifecycleState());
        assertEquals("John Doe", persistedBmmSchema.getSchemaAuthor());
        assertEquals("A manually created very simplified BMM for testing purposes", persistedBmmSchema.getSchemaDescription());
        assertEquals(2, persistedBmmSchema.getSchemaContributors().size());
        assertEquals("Any", persistedBmmSchema.getArchetypeParentClass());
        assertEquals("DATA_VALUE", persistedBmmSchema.getArchetypeDataValueParentClass());
        assertEquals("ParentPackage", persistedBmmSchema.getArchetypeRmClosurePackages().get(0));
    }

    public void testIncludes() {
        assertEquals(2, persistedBmmSchema.getIncludes().size());
        assertEquals("my_include.2.1.12", persistedBmmSchema.getIncludes().get("my_include.2.1.12".toUpperCase()).getId());
        assertEquals("your_include.1_1_11", persistedBmmSchema.getIncludes().get("your_include.1_1_11".toUpperCase()).getId());
    }

    public void testPackageStructure() {
        assertEquals(1, persistedBmmSchema.getPackages().size());
        PersistedBmmPackage bmmPackage = persistedBmmSchema.getPackage("ParentPackage");
        assertEquals("ParentPackage", bmmPackage.getName());
        assertEquals(6, bmmPackage.getClasses().size());
        assertEquals("ParentType1", bmmPackage.getClasses().get(0));
        assertEquals("ParentType2", bmmPackage.getClasses().get(1));
        testSecondLevelPackages(bmmPackage);
    }

    public void testSecondLevelPackages(PersistedBmmPackage bmmParentPackage) {
        Map<String, PersistedBmmPackage> packages = bmmParentPackage.getPackages();
        assertEquals(2, packages.size());
        PersistedBmmPackage childPackage1 = packages.get("CHILDPACKAGE1");
        assertEquals("ChildPackage1", childPackage1.getName());
        assertEquals(8, childPackage1.getClasses().size());
        PersistedBmmPackage childPackage2 = packages.get("CHILDPACKAGE2");
        assertEquals("ChildPackage2", childPackage2.getName());
        assertEquals(7, childPackage2.getClasses().size());
    }

    public void testClassDefinitions(PersistedBmmSchema model) {
        assertEquals(21, model.getClassDefinitions().size());
        assertEquals(0, model.getPrimitives().size());
        testParentType1(model);
        testParentType2(model);
        testParentType1(model);
        testInterval(model);
        testOrdered(model);
        testParty(model);
        testQuantity(model);
        testProportionKind(model);
        testProportionKind2(model);
        testMagnitudeStatus(model);
        testNamePart(model);
        testQuantityInterval(model);
        testPatient(model);
        testReferenceRange(model);
        testRangeOfIntervalOfQuantity(model);
        testCrazyType(model);
    }

    public void testPrimitiveDefinitions(PersistedBmmSchema model) {
        assertEquals(0, model.getPrimitives().size());
    }

    public void testParentType1(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "ParentType1", 2);
        PersistedBmmProperty property = null;
        property = testProperty(bmmClass, "property_1", true, false, false, false);
        testSingleProperty(property, "String");
        property = testProperty(bmmClass, "property_2", false, false, false, false);
        testSinglePropertyOpen(property, "T");
    }

    public void testParentType2(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "ParentType2", 2);
        PersistedBmmProperty property = null;
        property = testProperty(bmmClass, "property_1", false, false, false, false);
        testSimpleGenericProperty(property, "Interval", "Quantity");
        property = testProperty(bmmClass, "property_2", true, false, false, false);
        testSimpleContainerProperty(property, "List", "QUANTITY");
    }

    public void testInterval(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "Interval", 2, 1);
        assertEquals("Any", bmmClass.getAncestors().get(0));
        assertNotNull(bmmClass.getGenericParameterDefinitions());
        assertEquals(1, bmmClass.getGenericParameterDefinitions().size());
        PersistedBmmGenericParameter parameter = (PersistedBmmGenericParameter)bmmClass.getGenericParameterDefinitions().get("T");//TODO Why the cast???!!!
        assertEquals("T", parameter.getName());
        assertEquals("Ordered", parameter.getConformsToType());
        PersistedBmmProperty property = testProperty(bmmClass, "lower", false, false, false, false);
        assertTrue(property instanceof PersistedBmmSinglePropertyOpen);
        PersistedBmmSinglePropertyOpen openProperty = (PersistedBmmSinglePropertyOpen)property;
        assertEquals("lower", openProperty.getName());
        assertEquals("T", openProperty.getType());
        property = testProperty(bmmClass, "upper", false, false, false, false);
        assertTrue(property instanceof PersistedBmmSinglePropertyOpen);
        openProperty = (PersistedBmmSinglePropertyOpen)property;
        assertEquals("upper", openProperty.getName());
        assertEquals("T", openProperty.getType());
    }

    public void testOrdered(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "Ordered", 0);
    }

    public void testParty(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "Party", 0);
    }

    public void testQuantity(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "Quantity", 0, 1);
        assertEquals("Ordered", bmmClass.getAncestors().get(0));
    }

    public void testProportionKind(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "ProportionKind", 0, 1);
        assertEquals("Integer", bmmClass.getAncestors().get(0));
        assertTrue(bmmClass instanceof PersistedBmmEnumerationInteger);
    }
    public void testProportionKind2(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "ProportionKind2", 0, 1);
        assertTrue(bmmClass instanceof PersistedBmmEnumerationInteger);
    }

    public void testMagnitudeStatus(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "MagnitudeStatus", 0, 1);
        assertTrue(bmmClass instanceof PersistedBmmEnumerationString);
    }

    public void testNamePart(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "NamePart", 0, 1);
        assertTrue(bmmClass instanceof PersistedBmmEnumerationString);
    }

    public void testQuantityInterval(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "QuantityInterval", 1, 1);
        assertEquals("Any", bmmClass.getAncestors().get(0));
        PersistedBmmProperty property = testProperty(bmmClass, "qty_interval_attr", false, false, false, false);
        assertTrue(property instanceof PersistedBmmGenericProperty);
        PersistedBmmGenericProperty genericProperty = (PersistedBmmGenericProperty)property;
        assertNotNull(genericProperty.getTypeDefinition());
        assertEquals("Interval", genericProperty.getTypeDefinition().getRootType());
        assertEquals("Quantity", genericProperty.getTypeDefinition().getGenericParameters().get("1"));
    }

    public void testPatient(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "Patient", 1, 1);
        assertEquals("Any", bmmClass.getAncestors().get(0));
        PersistedBmmProperty property = testProperty(bmmClass, "careProvider", false, false, false, false);
        assertTrue(property instanceof PersistedBmmContainerProperty);
        PersistedBmmContainerProperty containerProperty = (PersistedBmmContainerProperty)property;
        assertEquals("careProvider", containerProperty.getName());
        assertNotNull(containerProperty.getTypeDefinition());
        assertEquals("List", containerProperty.getTypeDefinition().getContainerType());
        assertNotNull(containerProperty.getTypeDefinition().getTypeDefinition());
        assertTrue(containerProperty.getTypeDefinition().getTypeDefinition() instanceof PersistedBmmGenericType);
        PersistedBmmGenericType genericType = (PersistedBmmGenericType)containerProperty.getTypeDefinition().getTypeDefinition();
        assertEquals("ResourceReference", genericType.getRootType());
        assertEquals("Party", genericType.getGenericParameters().get("1"));
    }

    public void testReferenceRange(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "ReferenceRange", 1, 1);
        assertEquals("Any", bmmClass.getAncestors().get(0));
        assertNotNull(bmmClass.getGenericParameterDefinitions());
        assertEquals(1, bmmClass.getGenericParameterDefinitions().size());
        assertEquals("T", ((PersistedBmmGenericParameter)bmmClass.getGenericParameterDefinitions().get("T")).getName());//TODO Why do I have to cast here?
        assertEquals("Ordered", ((PersistedBmmGenericParameter)bmmClass.getGenericParameterDefinitions().get("T")).getConformsToType());//TODO Why do I have to cast here?
        PersistedBmmProperty property = testProperty(bmmClass, "range", true, false, false, false);
    }

    public void testRangeOfIntervalOfQuantity(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "RangeOfIntervalOfQuantity", 1, 1);
        assertEquals("Any", bmmClass.getAncestors().get(0));
        PersistedBmmProperty property = testProperty(bmmClass, "range", false, false, false, false);
        assertTrue(property instanceof PersistedBmmGenericProperty);
        PersistedBmmGenericProperty genericProperty = (PersistedBmmGenericProperty)property;
        assertNotNull(genericProperty.getTypeDefinition());
        PersistedBmmGenericType genericPropertyType = genericProperty.getTypeDefinition();
        assertEquals("ReferenceRange", genericPropertyType.getRootType());
        assertNotNull(genericPropertyType.getGenericParameterDefinitions());
        assertTrue(genericPropertyType.getGenericParameterDefinitions().get("T") instanceof PersistedBmmGenericType);
        PersistedBmmGenericType genericTypeOfType = (PersistedBmmGenericType)genericPropertyType.getGenericParameterDefinitions().get("T");
        assertEquals("Interval", genericTypeOfType.getRootType());
        assertEquals("Quantity", genericTypeOfType.getGenericParameters().get("1"));
    }

    public void testCrazyType(PersistedBmmSchema model) {
        PersistedBmmClass bmmClass = testClass(model, "CrazyType", 1, 1);
        assertEquals("Any", bmmClass.getAncestors().get(0));
        PersistedBmmProperty range = testProperty(bmmClass, "range", false, false, false,false);
        assertNotNull(range.getTypeDefinition());
        assertTrue(range instanceof PersistedBmmGenericProperty);
        PersistedBmmGenericType rangeType = (PersistedBmmGenericType)range.getTypeDefinition();
        assertEquals("ReferenceRange", rangeType.getRootType());
        assertEquals(4, rangeType.getGenericParameterDefinitions().size());
        //Test for presence of T
        assertTrue(rangeType.getGenericParameterDefinitions().get("T") instanceof PersistedBmmGenericType);
        PersistedBmmGenericType T = (PersistedBmmGenericType)rangeType.getGenericParameterDefinitions().get("T");
        assertEquals("Interval", T.getRootType());
        assertEquals("Quantity", T.getGenericParameters().get("1"));
        assertEquals(1, T.getGenericParameters().size());
        //Test for presence of U
        assertTrue(rangeType.getGenericParameterDefinitions().get("U") instanceof PersistedBmmSimpleType);
        PersistedBmmSimpleType U = (PersistedBmmSimpleType)rangeType.getGenericParameterDefinitions().get("U");
        assertEquals("Integer", U.getType());
        //Test for presence of V
        assertTrue(rangeType.getGenericParameterDefinitions().get("V") instanceof PersistedBmmContainerType);
        PersistedBmmContainerType V = (PersistedBmmContainerType)rangeType.getGenericParameterDefinitions().get("V");
        assertEquals("List", V.getContainerType());
        assertEquals("Quantity", V.getType());
        //Test for presence of W
        assertTrue(rangeType.getGenericParameterDefinitions().get("W") instanceof PersistedBmmContainerType);
        PersistedBmmContainerType W = (PersistedBmmContainerType)rangeType.getGenericParameterDefinitions().get("W");
        assertEquals("List", W.getContainerType());
        PersistedBmmGenericType wGenericType = (PersistedBmmGenericType)W.getTypeDefinition();
        assertEquals("Interval", wGenericType.getRootType());
        assertEquals(1, wGenericType.getGenericParameters().size());
        assertEquals("Quantity", wGenericType.getGenericParameters().get("1"));



    }

    public PersistedBmmClass testClass(PersistedBmmSchema model, String className, int propertyCount) {
       return testClass(model, className, propertyCount, 0);
    }

    public PersistedBmmClass testClass(PersistedBmmSchema model, String className, int propertyCount, int ancestorCount) {
        PersistedBmmClass bmmClass = model.getClassDefinition(className);
        assertNotNull(bmmClass);
        assertEquals(propertyCount, bmmClass.getProperties().size());
        assertEquals(ancestorCount, bmmClass.getAncestors().size());
        return bmmClass;
    }

    public PersistedBmmProperty testProperty(PersistedBmmClass bmmClass, String name, boolean isMandatory, boolean isComputed, boolean isImInfrastructure, boolean isImRuntime) {
        PersistedBmmProperty property = bmmClass.getPropertyByName(name);
        assertNotNull(property);
        assertEquals(name, property.getName());
        assertEquals(isMandatory, property.getMandatory());
        assertEquals(isComputed, property.getComputed());
        assertEquals(isImInfrastructure, property.getImInfrastructure());
        assertEquals(isImRuntime, property.getImRuntime());
        return property;
    }

    public void testSingleProperty(PersistedBmmProperty property, String type) {
        assertTrue(property instanceof PersistedBmmSingleProperty);
        PersistedBmmSingleProperty singleProperty = (PersistedBmmSingleProperty)property;
        assertEquals(type, singleProperty.getType());
    }

    public void testSinglePropertyOpen(PersistedBmmProperty property, String type) {
        assertTrue(property instanceof PersistedBmmSinglePropertyOpen);
        PersistedBmmSinglePropertyOpen singlePropertyOpen = (PersistedBmmSinglePropertyOpen)property;
        assertEquals(type, singlePropertyOpen.getType());
    }

    public void testSimpleGenericProperty(PersistedBmmProperty property, String rootType, String genericParameter) {
        assertTrue(property instanceof PersistedBmmGenericProperty);
        PersistedBmmGenericProperty genericProperty = (PersistedBmmGenericProperty)property;
        assertNotNull(genericProperty.getTypeDefinition());
        assertEquals(rootType, genericProperty.getTypeDefinition().getRootType());
        assertEquals(1, genericProperty.getTypeDefinition().getGenericParameters().size());
        assertTrue(genericProperty.getTypeDefinition().getGenericParameters().values().contains(genericParameter));
    }

    public void testSimpleContainerProperty(PersistedBmmProperty property, String containerType, String itemType) {
        assertTrue(property instanceof PersistedBmmContainerProperty);
        PersistedBmmContainerProperty containerProperty = (PersistedBmmContainerProperty)property;
        assertNotNull(containerProperty.getTypeDefinition());
        assertEquals(containerType, containerProperty.getTypeDefinition().getContainerType());
        assertEquals(itemType, containerProperty.getTypeDefinition().getType());
    }

    @Test
    public void testRoundTrip() {
        try {
            BmmSchemaSerializer serializer = new BmmSchemaSerializer(persistedBmmSchema);
            String schemaString = serializer.serialize();
            System.out.println(schemaString);
            OdinLoaderImpl loader = new OdinLoaderImpl();
            OdinVisitorImpl visitor = loader.loadOdinFromString(schemaString);
            CompositeOdinObject root = visitor.getAstRootNode();
            BmmSchemaDeserializer deserializer = new BmmSchemaDeserializer();
            PersistedBmmSchema deserializedSchema = deserializer.deserialize(root);
            deserializeSchema(deserializedSchema);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Deserialization/serialization round trip failed.");
        }
    }
}