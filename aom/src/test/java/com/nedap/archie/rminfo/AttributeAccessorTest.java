package com.nedap.archie.rminfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AttributeAccessorTest {
    private static final ModelInfoLookup modelInfoLookup = new LocalClassInfoLookup();
    private static final AttributeAccessor attributeAccessor = new AttributeAccessor(modelInfoLookup);

    private static class Dummy {
    }

    @SuppressWarnings("unused")
    private static class Thing {
        private String name;

        private byte[] data;

        public Thing() {
        }

        public Thing(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public int getFour() {
            // Read-only attribute
            return 4;
        }
    }

    @SuppressWarnings("unused")
    private static class Group {
        private List<Group> subgroups = new ArrayList<>();

        private List<Thing> things = new ArrayList<>();

        private Thing mainThing;

        public List<Group> getSubgroups() {
            return subgroups;
        }

        public void setSubgroups(List<Group> subgroups) {
            this.subgroups = subgroups;
        }

        public List<Thing> getThings() {
            return things;
        }

        public void setThings(List<Thing> things) {
            this.things = things;
        }

        public void addThing(Thing thing) {
            if (things == null) {
                things = new ArrayList<>();
            }
            this.things.add(thing);
        }

        public Thing getMainThing() {
            return mainThing;
        }

        public void setMainThing(Thing mainThing) {
            this.mainThing = mainThing;
        }
    }

    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    private static class BrokenClass {
        // This single valued attribute throws an exception when accessed
        private BrokenClass child;

        // This multiple valued attribute throws an exception when accessed
        private List<BrokenClass> items = new ArrayList<>();

        private Set<BrokenClass> someSet = new HashSet<>();

        public BrokenClass getChild() {
            throw new RuntimeException("Some exception");
        }

        public void setChild(BrokenClass child) {
            throw new RuntimeException("Some exception");
        }

        public List<BrokenClass> getItems() {
            throw new RuntimeException("Some exception");
        }

        public void setItems(List<BrokenClass> values) {
            throw new RuntimeException("Some exception");
        }

        public void addItem(BrokenClass value) {
            throw new RuntimeException("Some exception");
        }

        public Set<BrokenClass> getSomeSet() {
            return someSet;
        }

        public void setSomeSet(Set<BrokenClass> values) {
            this.someSet = values;
        }

        public void addSomeSet(BrokenClass value) {
            this.someSet.add(value);
        }
    }

    public static class LocalClassInfoLookup extends ReflectionModelInfoLookup {
        private LocalClassInfoLookup() {
            super(new ArchieModelNamingStrategy(), Object.class);
        }

        @Override
        protected void addTypes(Class<?> baseClass) {
            addClass(Thing.class);
            addClass(Group.class);
            addClass(BrokenClass.class);
        }

        @Override
        public void processCreatedObject(Object createdObject, CObject constraint) {
            // Do nothing
        }

        @Override
        public String getArchetypeNodeIdFromRMObject(Object rmObject) {
            return null;
        }

        @Override
        public String getArchetypeIdFromArchetypedRmObject(Object rmObject) {
            return null;
        }

        @Override
        public String getNameFromRMObject(Object rmObject) {
            return null;
        }

        @Override
        public Object clone(Object rmObject) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, Object> pathHasBeenUpdated(Object rmObject, Archetype archetype, String pathOfParent, Object parent) {
            return new HashMap<>();
        }

        @Override
        public boolean validatePrimitiveType(String rmTypeName, String rmAttributeName, CPrimitiveObject<?, ?> cObject) {
            return false;
        }

        @Override
        public Collection<RMPackageId> getId() {
            return Lists.newArrayList(new RMPackageId("openEHR", "BROKEN"));
        }
    }

    @Nested
    class AddValue {

        @Test
        public void testMultipleValuedSingle() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            Thing thing3 = new Thing();

            attributeAccessor.addValue(group, "things", thing3);

            assertEquals(3, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
            assertSame(thing3, group.getThings().get(2));
        }

        @Test
        public void testMultipleValuedNull() {
            Group group = new Group();
            group.setThings(Lists.newArrayList(new Thing(), new Thing()));

            NullPointerException exception = assertThrows(NullPointerException.class, () -> attributeAccessor.addValue(group, "things", null));
            assertEquals("value must not be null", exception.getMessage());

            assertEquals(2, group.getThings().size());
        }

        @Test
        public void testMultipleValuedListIntialEmpty() {
            Group group = new Group();

            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            List<Thing> newThings = Lists.newArrayList(thing1, thing2);

            attributeAccessor.addValue(group, "things", newThings);

            assertNotSame(newThings, group.getThings(),"A new collection should be created");
            assertEquals(2, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
        }

        @Test
        public void testMultipleValuedListIntialNull() {
            Group group = new Group();
            group.setThings(null);

            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            List<Thing> newThings = Lists.newArrayList(thing1, thing2);

            attributeAccessor.addValue(group, "things", newThings);

            assertNotSame(newThings, group.getThings(), "A new collection should be created");
            assertEquals(2, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
        }

        @Test
        public void testMultipleValuedListIntialFilled() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            Thing thing3 = new Thing();
            Thing thing4 = new Thing();
            List<Thing> newThings = Lists.newArrayList(thing3, thing4);

            attributeAccessor.addValue(group, "things", newThings);

            assertEquals(4, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
            assertSame(thing3, group.getThings().get(2));
            assertSame(thing4, group.getThings().get(3));
        }

        @Test
        public void testMultipleValuedListEmpty() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            attributeAccessor.addValue(group, "things", Collections.emptyList());

            assertEquals(2, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
        }

        @Test
        public void testMultipleValuedListNull() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            attributeAccessor.addValue(group, "things", Collections.singletonList(null));

            assertEquals(3, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
            assertNull(group.getThings().get(2));
        }

        @Test
        public void testMultipleValuedFallback() {
            // Group has no addSubgroup method
            Group group = new Group();
            group.setSubgroups(null);

            Group subgroup1 = new Group();
            Group subgroup2 = new Group();
            List<Group> newSubgroups = Lists.newArrayList(subgroup1, subgroup2);

            attributeAccessor.addValue(group, "subgroups", newSubgroups);

            assertNotSame(newSubgroups, group.getSubgroups(), "A new collection should be created");
            assertEquals(2, group.getSubgroups().size());
            assertSame(subgroup1, group.getSubgroups().get(0));
            assertSame(subgroup2, group.getSubgroups().get(1));
        }

        @Test
        public void testMultipleValuedWrongCollection() {
            Group group = new Group();
            Set<Thing> things = new HashSet<>();
            things.add(new Thing());
            things.add(new Thing());

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addValue(group, "things", things));
            assertEquals("Value type java.util.HashSet is not a valid type for attribute things of object Group", throwable.getMessage());
        }

        @Test
        public void testMultipleValuedWrongType() {
            Group group = new Group();
            List<Object> newThings = Lists.newArrayList(new Thing(), new Dummy());

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addValue(group, "things", newThings));
            assertEquals("Value type com.nedap.archie.rminfo.AttributeAccessorTest$Dummy is not a valid type for attribute things of object Group", throwable.getMessage());
        }

        @Test
        public void testMultipleValuedException() {
            AttributeAccessor attributeAccessor = new AttributeAccessor(new LocalClassInfoLookup());
            BrokenClass brokenClass = new BrokenClass();

            RuntimeException throwable = assertThrows(RuntimeException.class, () -> attributeAccessor.addValue(brokenClass, "items", new BrokenClass()));
            assertEquals("Error adding value to attribute items of object BrokenClass", throwable.getMessage());
        }

        @Test
        public void testMultipleValuedSetType() {
            AttributeAccessor attributeAccessor = new AttributeAccessor(new LocalClassInfoLookup());
            BrokenClass brokenClass = new BrokenClass();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addValue(brokenClass, "some_set", new BrokenClass()));
            assertEquals("Unknown collection type java.util.Set", throwable.getMessage());
        }

        @Test
        public void testSingleValued() {
            Thing thing = new Thing("old value");

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addValue(thing, "name", "new value"));
            assertEquals("Attribute name of object Thing is not a multiple valued attribute", throwable.getMessage());

            assertEquals("old value", thing.getName());
        }

        @Test
        public void testByteArray() {
            Thing thing = new Thing();
            byte[] bytes = new byte[]{33, 42};

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addValue(thing, "data", bytes));
            assertEquals("Attribute data of object Thing is not a multiple valued attribute", throwable.getMessage());

            assertNull(thing.getData());
        }

        @Test
        public void testUnknownAttribute() {
            Thing thing = new Thing();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addValue(thing, "unknown", "test string"));
            assertEquals("Attribute unknown of object Thing is not a valid attribute", throwable.getMessage());
        }

        @Test
        public void testNonRmObject() {
            Dummy dummy = new Dummy();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addValue(dummy, "value", 12345));
            assertEquals("Attribute value of object Dummy is not a valid attribute", throwable.getMessage());
        }
    }

    @Nested
    class AddOrSetValue {

        @Test
        public void testMultipleValuedSingle() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            Thing thing3 = new Thing();

            attributeAccessor.addOrSetValue(group, "things", thing3);

            assertEquals(3, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
            assertSame(thing3, group.getThings().get(2));
        }

        @Test
        public void testMultipleValuedNull() {
            Group group = new Group();
            group.setThings(Lists.newArrayList(new Thing(), new Thing()));

            NullPointerException exception = assertThrows(NullPointerException.class, () -> attributeAccessor.addOrSetValue(group, "things", null));
            assertEquals("value must not be null", exception.getMessage());

            assertEquals(2, group.getThings().size());
        }

        @Test
        public void testMultipleValuedListIntialEmpty() {
            Group group = new Group();

            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            List<Thing> newThings = Lists.newArrayList(thing1, thing2);

            attributeAccessor.addOrSetValue(group, "things", newThings);

            assertNotSame(newThings, group.getThings(), "A new collection should be created");
            assertEquals(2, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
        }

        @Test
        public void testMultipleValuedListIntialNull() {
            // Group has no addRow method
            Group group = new Group();
            group.setThings(null);

            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            List<Thing> newThings = Lists.newArrayList(thing1, thing2);

            attributeAccessor.addOrSetValue(group, "things", newThings);

            assertNotSame(newThings, group.getThings(), "A new collection should be created");
            assertEquals(2, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
        }

        @Test
        public void testMultipleValuedListIntialFilled() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            Thing thing3 = new Thing();
            Thing thing4 = new Thing();
            List<Thing> newThings = Lists.newArrayList(thing3, thing4);

            attributeAccessor.addOrSetValue(group, "things", newThings);

            assertEquals(4, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
            assertSame(thing3, group.getThings().get(2));
            assertSame(thing4, group.getThings().get(3));
        }

        @Test
        public void testMultipleValuedListEmpty() {
            Group group = new Group();
            group.setThings(Lists.newArrayList(new Thing(), new Thing()));

            attributeAccessor.addOrSetValue(group, "things", Collections.emptyList());

            assertEquals(2, group.getThings().size());
        }

        @Test
        public void testMultipleValuedListNull() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            attributeAccessor.addOrSetValue(group, "things", Collections.singletonList(null));

            assertEquals(3, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
            assertNull(group.getThings().get(2));
        }

        @Test
        public void testMultipleValuedFallback() {
            // Group has no addRow method
            Group group = new Group();

            Group subgroup1 = new Group();
            Group subgroup2 = new Group();
            List<Group> newSubgroups = Lists.newArrayList(subgroup1, subgroup2);

            attributeAccessor.addOrSetValue(group, "subgroups", newSubgroups);

            assertNotSame(newSubgroups, group.getSubgroups(), "A new collection should be created");
            assertEquals(2, group.getSubgroups().size());
            assertSame(subgroup1, group.getSubgroups().get(0));
            assertSame(subgroup2, group.getSubgroups().get(1));
        }

        @Test
        public void testMultipleValuedWrongCollection() {
            Group group = new Group();
            Set<Thing> things = new HashSet<>();
            things.add(new Thing());
            things.add(new Thing());

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(group, "things", things));
            assertEquals("Value type java.util.HashSet is not a valid type for attribute things of object Group", throwable.getMessage());
        }

        @Test
        public void testMultipleValuedWrongType() {
            Group group = new Group();
            List<Object> newThings = Lists.newArrayList(new Thing(), new Dummy());

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(group, "things", newThings));
            assertEquals("Value type com.nedap.archie.rminfo.AttributeAccessorTest$Dummy is not a valid type for attribute things of object Group", throwable.getMessage());
        }

        @Test
        public void testMultipleValuedException() {
            AttributeAccessor attributeAccessor = new AttributeAccessor(new LocalClassInfoLookup());
            BrokenClass brokenClass = new BrokenClass();

            RuntimeException throwable = assertThrows(RuntimeException.class, () -> attributeAccessor.addOrSetValue(brokenClass, "items", new BrokenClass()));
            assertEquals("Error adding value to attribute items of object BrokenClass", throwable.getMessage());
        }

        @Test
        public void testMultipleValuedSetType() {
            AttributeAccessor attributeAccessor = new AttributeAccessor(new LocalClassInfoLookup());
            BrokenClass brokenClass = new BrokenClass();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(brokenClass, "some_set", new BrokenClass()));
            assertEquals("Unknown collection type java.util.Set", throwable.getMessage());
        }

        @Test
        public void testSingleValued() {
            Thing thing = new Thing("old value");
            attributeAccessor.addOrSetValue(thing, "name", "new value");
            assertEquals("new value", thing.getName());
        }

        @Test
        public void testSingleValuedNull() {
            Thing thing = new Thing("old value");
            attributeAccessor.addOrSetValue(thing, "name", null);
            assertNull(thing.getName());
        }

        @Test
        public void testSingleValuedList() {
            Thing thing = new Thing("old value");
            List<String> newValue = Lists.newArrayList("new value");
            attributeAccessor.addOrSetValue(thing, "name", newValue);
            assertEquals("new value", thing.getName());
        }

        @Test
        public void testSingleValuedListEmpty() {
            Thing thing = new Thing("old value");
            List<String> newValue = Lists.newArrayList();
            attributeAccessor.addOrSetValue(thing, "name", newValue);
            assertNull(thing.getName());
        }

        @Test
        public void testSingleValuedListNull() {
            Thing thing = new Thing("old value");
            List<String> newValue = Lists.newArrayList();
            newValue.add(null);
            attributeAccessor.addOrSetValue(thing, "name", newValue);
            assertNull(thing.getName());
        }

        @Test
        public void testSingleValuedListMultiple() {
            Thing thing = new Thing("old value");
            List<String> newValue = Lists.newArrayList();
            newValue.add("first string");
            newValue.add("second string");

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(thing, "name", newValue));
            assertEquals("Attribute name of object Thing does not support multiple values", throwable.getMessage());

            assertEquals("old value", thing.getName());
        }

        @Test
        public void testSingleValuedSet() {
            Thing thing = new Thing("old value");
            Set<String> newValue = Sets.newHashSet("new value");
            attributeAccessor.addOrSetValue(thing, "name", newValue);
            assertEquals("new value", thing.getName());
        }

        @Test
        public void testSingleValuedReadOnlyAttribute() {
            Thing thing = new Thing();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(thing, "four", 5));
            assertEquals("Attribute four of object Thing is a read-only attribute", throwable.getMessage());
        }

        @Test
        public void testSingleValuedWrongType() {
            Thing thing = new Thing("old value");

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(thing, "name", 12345));
            assertEquals("Value type java.lang.Integer is not a valid type for attribute name of object Thing", throwable.getMessage());

            assertEquals("old value", thing.getName());
        }

        @Test
        public void testSingleValuedObject() {
            Group group = new Group();
            Thing thing = new Thing();
            attributeAccessor.addOrSetValue(group, "main_thing", thing);
            assertSame(thing, group.getMainThing());
        }

        @Test
        public void testSingleValuedException() {
            AttributeAccessor attributeAccessor = new AttributeAccessor(new LocalClassInfoLookup());
            BrokenClass brokenClass = new BrokenClass();

            RuntimeException throwable = assertThrows(RuntimeException.class, () -> attributeAccessor.addOrSetValue(brokenClass, "child", new BrokenClass()));
            assertEquals("Error setting value on attribute child of object BrokenClass", throwable.getMessage());
        }

        @Test
        public void testByteArray() {
            Thing thing = new Thing();
            byte[] bytes = new byte[]{33, 42};
            attributeAccessor.addOrSetValue(thing, "data", bytes);
            assertSame(bytes, thing.getData());
        }

        @Test
        public void testByteArrayByte() {
            Thing thing = new Thing();
            byte singleByte = 42;

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(thing, "data", singleByte));
            assertEquals("Value type java.lang.Byte is not a valid type for attribute data of object Thing", throwable.getMessage());
        }

        @Test
        public void testByteArrayIntArray() {
            Thing thing = new Thing();
            int[] ints = new int[]{33, 42};

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(thing, "data", ints));
            assertEquals("Value type int[] is not a valid type for attribute data of object Thing", throwable.getMessage());
        }

        @Test
        public void testUnknownAttribute() {
            Thing thing = new Thing();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(thing, "unknown", "test string"));
            assertEquals("Attribute unknown of object Thing is not a valid attribute", throwable.getMessage());
        }

        @Test
        public void testNonRmObject() {
            Dummy dummy = new Dummy();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.addOrSetValue(dummy, "value", 12345));
            assertEquals("Attribute value of object Dummy is not a valid attribute", throwable.getMessage());
        }
    }

    @Nested
    class GetValue {
        @Test
        public void testNormal() {
            Thing thing = new Thing("test string");
            assertEquals("test string", attributeAccessor.getValue(thing, "name"));
        }

        @Test
        public void testNull() {
            Thing thing = new Thing();
            assertNull(attributeAccessor.getValue(thing, "name"));
        }

        @Test
        public void testUnknownAttribute() {
            Thing thing = new Thing();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.getValue(thing, "unknown"));
            assertEquals("Attribute unknown of object Thing is not a valid attribute", throwable.getMessage());
        }

        @Test
        public void testNonRmObject() {
            Dummy dummy = new Dummy();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.getValue(dummy, "value"));
            assertEquals("Attribute value of object Dummy is not a valid attribute", throwable.getMessage());
        }

        @Test
        public void testException() {
            AttributeAccessor attributeAccessor = new AttributeAccessor(new LocalClassInfoLookup());
            BrokenClass brokenClass = new BrokenClass();

            RuntimeException throwable = assertThrows(RuntimeException.class, () -> attributeAccessor.getValue(brokenClass, "items"));
            assertEquals("Error getting value of attribute items of object BrokenClass", throwable.getMessage());
        }
    }

    @Nested
    class HasAttribute {
        @Test
        public void testHasAttribute() {
            assertTrue(attributeAccessor.hasAttribute(new Thing(), "name"));
            assertFalse(attributeAccessor.hasAttribute(new Thing(), "unknown"));
            assertFalse(attributeAccessor.hasAttribute(new Dummy(), "value"));
        }
    }

    @Nested
    class SetValue {
        @Test
        public void testMultipleValuedSingle() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            Thing thing3 = new Thing();

            attributeAccessor.setValue(group, "things", thing3);

            assertEquals(1, group.getThings().size());
            assertSame(thing3, group.getThings().get(0));
        }

        @Test
        public void testMultipleValuedNull() {
            Group group = new Group();
            group.setThings(Lists.newArrayList(new Thing(), new Thing()));

            attributeAccessor.setValue(group, "things", null);

            assertNull(group.getThings());
        }

        @Test
        public void testMultipleValuedListIntialEmpty() {
            Group group = new Group();

            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            List<Thing> newThings = Lists.newArrayList(thing1, thing2);

            attributeAccessor.setValue(group, "things", newThings);

            assertEquals(2, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
        }

        @Test
        public void testMultipleValuedListIntialNull() {
            Group group = new Group();
            group.setThings(null);

            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            List<Thing> newThings = Lists.newArrayList(thing1, thing2);

            attributeAccessor.setValue(group, "things", newThings);

            assertEquals(2, group.getThings().size());
            assertSame(thing1, group.getThings().get(0));
            assertSame(thing2, group.getThings().get(1));
        }

        @Test
        public void testMultipleValuedListIntialFilled() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            Thing thing3 = new Thing();
            Thing thing4 = new Thing();
            List<Thing> newThings = Lists.newArrayList(thing3, thing4);

            attributeAccessor.setValue(group, "things", newThings);

            assertEquals(2, group.getThings().size());
            assertSame(thing3, group.getThings().get(0));
            assertSame(thing4, group.getThings().get(1));
        }

        @Test
        public void testMultipleValuedListEmpty() {
            Group group = new Group();
            group.setThings(Lists.newArrayList(new Thing(), new Thing()));

            attributeAccessor.setValue(group, "things", Collections.emptyList());

            assertEquals(0, group.getThings().size());
        }

        @Test
        public void testMultipleValuedListNull() {
            Group group = new Group();
            Thing thing1 = new Thing();
            Thing thing2 = new Thing();
            group.setThings(Lists.newArrayList(thing1, thing2));

            attributeAccessor.setValue(group, "things", Collections.singletonList(null));

            assertEquals(1, group.getThings().size());
            assertNull(group.getThings().get(0));
        }

        @Test
        public void testMultipleValuedWrongCollection() {
            Group group = new Group();
            Set<Thing> things = new HashSet<>();
            things.add(new Thing());
            things.add(new Thing());

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(group, "things", things));
            assertEquals("Value type java.util.HashSet is not a valid type for attribute things of object Group", throwable.getMessage());
        }

        @Test
        public void testMultipleValuedWrongType() {
            Group group = new Group();
            List<Object> newThings = Lists.newArrayList(new Thing(), new Dummy());

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(group, "things", newThings));
            assertEquals("Value type com.nedap.archie.rminfo.AttributeAccessorTest$Dummy is not a valid type for attribute things of object Group", throwable.getMessage());
        }

        @Test
        public void testMultipleValuedException() {
            AttributeAccessor attributeAccessor = new AttributeAccessor(new LocalClassInfoLookup());
            BrokenClass brokenClass = new BrokenClass();

            RuntimeException throwable = assertThrows(RuntimeException.class, () -> attributeAccessor.setValue(brokenClass, "items", new BrokenClass()));
            assertEquals("Error setting value on attribute items of object BrokenClass", throwable.getMessage());
        }

        @Test
        public void testMultipleValuedSetType() {
            AttributeAccessor attributeAccessor = new AttributeAccessor(new LocalClassInfoLookup());
            BrokenClass brokenClass = new BrokenClass();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(brokenClass, "some_set", new BrokenClass()));
            assertEquals("Unknown collection type java.util.Set", throwable.getMessage());
        }

        @Test
        public void testSingleValued() {
            Thing thing = new Thing("old value");
            attributeAccessor.setValue(thing, "name", "new value");
            assertEquals("new value", thing.getName());
        }

        @Test
        public void testSingleValuedNull() {
            Thing thing = new Thing("old value");
            attributeAccessor.setValue(thing, "name", null);
            assertNull(thing.getName());
        }

        @Test
        public void testSingleValuedList() {
            Thing thing = new Thing("old value");
            List<String> newValue = Lists.newArrayList("new value");
            attributeAccessor.setValue(thing, "name", newValue);
            assertEquals("new value", thing.getName());
        }

        @Test
        public void testSingleValuedListEmpty() {
            Thing thing = new Thing("old value");
            List<String> newValue = Lists.newArrayList();
            attributeAccessor.setValue(thing, "name", newValue);
            assertNull(thing.getName());
        }

        @Test
        public void testSingleValuedListNull() {
            Thing thing = new Thing("old value");
            List<String> newValue = Lists.newArrayList();
            newValue.add(null);
            attributeAccessor.setValue(thing, "name", newValue);
            assertNull(thing.getName());
        }

        @Test
        public void testSingleValuedListMultiple() {
            Thing thing = new Thing("old value");
            List<String> newValue = Lists.newArrayList();
            newValue.add("first string");
            newValue.add("second string");

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(thing, "name", newValue));
            assertEquals("Attribute name of object Thing does not support multiple values", throwable.getMessage());

            assertEquals("old value", thing.getName());
        }

        @Test
        public void testSingleValuedSet() {
            Thing thing = new Thing("old value");
            Set<String> newValue = Sets.newHashSet("new value");
            attributeAccessor.setValue(thing, "name", newValue);
            assertEquals("new value", thing.getName());
        }

        @Test
        public void testSingleValuedReadOnlyAttribute() {
            Thing thing = new Thing();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(thing, "four", 5));
            assertEquals("Attribute four of object Thing is a read-only attribute", throwable.getMessage());
        }

        @Test
        public void testSingleValuedWrongType() {
            Thing thing = new Thing("old value");

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(thing, "name", 12345));
            assertEquals("Value type java.lang.Integer is not a valid type for attribute name of object Thing", throwable.getMessage());

            assertEquals("old value", thing.getName());
        }

        @Test
        public void testSingleValuedObject() {
            Group group = new Group();
            Thing thing = new Thing();
            attributeAccessor.setValue(group, "main_thing", thing);
            assertSame(thing, group.getMainThing());
        }

        @Test
        public void testSingleValuedException() {
            AttributeAccessor attributeAccessor = new AttributeAccessor(new LocalClassInfoLookup());
            BrokenClass brokenClass = new BrokenClass();

            RuntimeException throwable = assertThrows(RuntimeException.class, () -> attributeAccessor.setValue(brokenClass, "child", new BrokenClass()));
            assertEquals("Error setting value on attribute child of object BrokenClass", throwable.getMessage());
        }

        @Test
        public void testByteArray() {
            Thing thing = new Thing();
            byte[] bytes = new byte[]{33, 42};
            attributeAccessor.setValue(thing, "data", bytes);
            assertSame(bytes, thing.getData());
        }

        @Test
        public void testByteArrayByte() {
            Thing thing = new Thing();
            byte singleByte = 42;

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(thing, "data", singleByte));
            assertEquals("Value type java.lang.Byte is not a valid type for attribute data of object Thing", throwable.getMessage());
        }

        @Test
        public void testByteArrayIntArray() {
            Thing thing = new Thing();
            int[] ints = new int[]{33, 42};

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(thing, "data", ints));
            assertEquals("Value type int[] is not a valid type for attribute data of object Thing", throwable.getMessage());
        }

        @Test
        public void testUnknownAttribute() {
            Thing thing = new Thing();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(thing, "unknown", "test string"));
            assertEquals("Attribute unknown of object Thing is not a valid attribute", throwable.getMessage());
        }

        @Test
        public void testNonRmObject() {
            Dummy dummy = new Dummy();

            IllegalArgumentException throwable = assertThrows(IllegalArgumentException.class, () -> attributeAccessor.setValue(dummy, "value", 12345));
            assertEquals("Attribute value of object Dummy is not a valid attribute", throwable.getMessage());
        }
    }
}
