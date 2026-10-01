package org.openehr.bmm.v2.persistence.converters;

import org.openehr.bmm.v2.persistence.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PBmmEqualsAssertions {

    public static void assertSchemaEquals(PBmmSchema schema1, PBmmSchema schema2) {
        assertEquals(schema1.getSchemaId(), schema2.getSchemaId());


        assertEquals(schema1.getRmPublisher(), schema2.getRmPublisher());
        assertEquals(schema1.getRmRelease(), schema2.getRmRelease());
        assertEquals(schema1.getBmmVersion(), schema2.getBmmVersion());
        assertEquals(schema1.getModelName(), schema2.getModelName());
        assertEquals(schema1.getSchemaName(), schema2.getSchemaName());
        assertEquals(schema1.getSchemaRevision(), schema2.getSchemaRevision());
        assertEquals(schema1.getSchemaLifecycleState(), schema2.getSchemaLifecycleState());
        assertEquals(schema1.getSchemaAuthor(), schema2.getSchemaAuthor());
        assertEquals(schema1.getSchemaDescription(), schema2.getSchemaDescription());

        assertEquals(schema1.getSchemaContributors(), schema2.getSchemaContributors());
        assertEquals(schema1.getArchetypeRmClosurePackages(), schema2.getArchetypeRmClosurePackages());

        assertEquals(schema1.getArchetypeParentClass(), schema2.getArchetypeParentClass());
        assertEquals(schema1.getArchetypeDataValueParentClass(), schema2.getArchetypeDataValueParentClass());
        assertEquals(schema1.getArchetypeVisualizeDescendantsOf(), schema2.getArchetypeVisualizeDescendantsOf());

        assertEquals(schema1.getPrimitiveTypes().keySet(), schema2.getPrimitiveTypes().keySet());
        for(String key:schema1.getPrimitiveTypes().keySet()) {
            assertClassEquals(schema1.getPrimitiveTypes().get(key), schema2.getPrimitiveTypes().get(key));
        }

        assertEquals(schema1.getClassDefinitions().keySet(), schema2.getClassDefinitions().keySet());
        for(String key:schema1.getClassDefinitions().keySet()) {
            assertClassEquals(schema1.getClassDefinitions().get(key), schema2.getClassDefinitions().get(key));
        }

        assertEquals(schema1.getPackages().keySet(), schema2.getPackages().keySet());
        for(String key: schema1.getPackages().keySet()) {
            assertPackageEquals(schema1.getPackages().get(key), schema2.getPackages().get(key));
        }



        assertEquals(schema1.getIncludes().keySet(), schema2.getIncludes().keySet());
        for(String key:schema1.getIncludes().keySet()) {
            assertIncludesEquals(schema1.getIncludes().get(key), schema2.getIncludes().get(key));
        }

    }

    private static void assertPackageEquals(PBmmPackage package1, PBmmPackage package2) {
        assertEquals(package1.getDocumentation(), package2.getDocumentation());
        assertEquals(package1.getName(), package2.getName());
        assertEquals(package1.getClasses(), package2.getClasses());
        assertEquals(package1.getPackages().keySet(), package2.getPackages().keySet());
        for(String key: package1.getPackages().keySet()) {
            assertPackageEquals(package1.getPackages().get(key), package2.getPackages().get(key));
        }
    }


    public static void assertIncludesEquals(BmmIncludeSpec include1, BmmIncludeSpec include2) {
        assertEquals(include1.getId(), include2.getId());

    }

    public static void assertClassEquals(PBmmClass class1, PBmmClass class2) {
        assertEquals(class1.getClass(), class2.getClass());//TODO: manually check enumerations
        assertEquals(class1.getDocumentation(), class2.getDocumentation());
        assertEquals(class1.getName(), class2.getName());
        assertEquals(class1.getAncestors(), class2.getAncestors());
        assertEquals(class1.isAbstract(), class2.isAbstract());
        assertEquals(class1.isOverride(), class2.isOverride());

        //private Map<String, PBmmType> ancestorDefs;

        assertEquals(class1.getProperties().keySet(), class2.getProperties().keySet());
        for(String propertyName: class1.getProperties().keySet()) {
            assertPropertyEquals(class1.getProperties().get(propertyName), class2.getProperties().get(propertyName));
        }

        assertEquals(class1.getGenericParameterDefs().keySet(), class2.getGenericParameterDefs().keySet());
        for(String parameterDefName: class1.getGenericParameterDefs().keySet()) {
            assertGenericParameterDefEquals(class1.getGenericParameterDefs().get(parameterDefName), class2.getGenericParameterDefs().get(parameterDefName));
        }

        //TODO: assertAncestorDefsEquals

    }

    public static void assertPropertyEquals(PBmmProperty<?> pBmmProperty1, PBmmProperty<?> pBmmProperty2) {

        assertEquals(pBmmProperty1.getClass(), pBmmProperty2.getClass());

        assertEquals(pBmmProperty1.getDocumentation(), pBmmProperty2.getDocumentation());
        assertEquals(pBmmProperty1.getName(), pBmmProperty2.getName());
        assertEquals(pBmmProperty1.isMandatory(), pBmmProperty2.isMandatory());
        assertEquals(pBmmProperty1.isComputed(), pBmmProperty2.isComputed());
        assertEquals(pBmmProperty1.isImInfrastructure(), pBmmProperty2.isImInfrastructure());
        assertEquals(pBmmProperty1.isImRuntime(), pBmmProperty2.isImRuntime());


        assertTypeEquals(pBmmProperty1.getTypeRef(), pBmmProperty2.getTypeRef());
        if (pBmmProperty1 instanceof PBmmContainerProperty) {
            assertEquals(((PBmmContainerProperty) pBmmProperty1).getCardinality(), ((PBmmContainerProperty) pBmmProperty1).getCardinality());
        }
    }

    public static void assertTypeEquals(PBmmType typeRef1, PBmmType typeRef2) {
        assertEquals(typeRef1.asTypeString(), typeRef2.asTypeString());
    }

    public static void assertGenericParameterDefEquals(PBmmGenericParameter param1, PBmmGenericParameter param2) {
        assertEquals(param1.getDocumentation(), param2.getDocumentation());
        assertEquals(param1.getConformsToType(), param2.getConformsToType());
        assertEquals(param1.getName(), param2.getName());
    }
}
