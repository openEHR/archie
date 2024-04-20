package com.nedap.archie.rminfo;

import com.google.common.reflect.TypeToken;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.base.RMObject;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility that defines the java mapping of type and attribute names of a given reference model.
 *
 * Use it to obtain java classes for RM Type Names, and java fields, getters, setters and types for RM Attribute Names
 *
 * This class is never directly created, but subclasses must be created that setup the correct model. Create a subclass
 * per model you want to use with Archie, for example one for an OpenEHR RM implementation, or the CIMI RM implementation
 *
 * Created by pieter.bos on 02/02/16.
 */
public abstract class ReflectionModelInfoLookup implements ModelInfoLookup {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionModelInfoLookup.class);

    private ModelNamingStrategy namingStrategy;

    private String packageName;
    private ClassLoader classLoader;

    private Map<String, RMTypeInfo> rmTypeNamesToRmTypeInfo = new HashMap<>();
    private Map<Class<?>, RMTypeInfo> classesToRmTypeInfo = new HashMap<>();

    private boolean inConstructor = true;
    private boolean addAttributesWithoutField = true;

    /**
     * All methods that cannot be called by using reflection. For example getClass();
     */
    private Set<String> forbiddenMethods = new HashSet<>(
            Arrays.asList("getClass", "wait", "notify", "notifyAll", "clone", "finalize")
    );

    public ReflectionModelInfoLookup(ModelNamingStrategy namingStrategy, Class<?> baseClass) {
        this(namingStrategy, baseClass, ReflectionModelInfoLookup.class.getClassLoader(), true);
    }

    public ReflectionModelInfoLookup(ModelNamingStrategy namingStrategy, String packageName, ClassLoader classLoader) {
        this.packageName = packageName;
        this.namingStrategy = namingStrategy;

        // add types
        this.classLoader = classLoader;
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes.filterResultsBy(s -> true));
        Set<String> typeNames = reflections.getAllTypes();

        typeNames.forEach(typeName -> {
            try {
                addClass(classLoader.loadClass(typeName));
            } catch (ClassNotFoundException e) {
                logger.error("error loading model info lookup", e);
            }
        });

        addSuperAndSubclassInfo();
        addAlternativeTypeNames();
        inConstructor = false;
    }

    public ReflectionModelInfoLookup(ModelNamingStrategy namingStrategy, Class<?> baseClass, ClassLoader classLoader, boolean addAttributesWithoutField) {
        this.namingStrategy = namingStrategy;
        this.addAttributesWithoutField = addAttributesWithoutField;

        this.classLoader = classLoader;

        // add types
        addTypes(baseClass);

        addSuperAndSubclassInfo();
        addAlternativeTypeNames();
        inConstructor = false;
    }

    /**
     * Adds bindings to alternative type names, for parsing backwards compatible JSON with
     * old incompatible type names
     */
    private void addAlternativeTypeNames() {
        for(Class clazz:this.classesToRmTypeInfo.keySet()) {
            for(String alternativeName:namingStrategy.getAlternativeTypeNames(clazz)) {
                String originalName = namingStrategy.getTypeName(clazz);
                this.rmTypeNamesToRmTypeInfo.put(alternativeName, rmTypeNamesToRmTypeInfo.get(originalName));
            }
        }
    }

    /**
     * Get the set of classes in a package (recursively) that are also descendants of a base class.
     * This may be used to generate an appropriate set of classes for specialisations of the addTYpe()
     * method below.
     *
     * @param packageName e.g. "org.openehr"
     * @param baseClass 'Any' class for the classes in the package of interest
     * @return Set of matching classes
     */
    public Set<Class<?>> findAllClassesInPackage(String packageName, Class<?> baseClass) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage(packageName)
                        .setScanners(Scanners.SubTypes
                                .filterResultsBy(s -> true)
                        )
        );

        return reflections.getSubTypesOf (baseClass)
                .stream()
                .filter(cl -> !cl.isInterface()                  // ignore interfaces
                        && !cl.getName().endsWith("$1"))         // ignore anonymous inner classes
                .collect(Collectors.toSet());
    }

    /**
     * Override to disable reflections scanning
     * @param baseClass
     */
    protected void addTypes(Class<?> baseClass) {
        addSubtypesOf(baseClass);
    }

    private void addSuperAndSubclassInfo() {
        for(RMTypeInfo typeInfo:rmTypeNamesToRmTypeInfo.values()) {
            Class<?> superclass = typeInfo.getJavaClass().getSuperclass();
            if(!superclass.equals(Object.class)) {
                addDescendantClass(typeInfo, superclass);
            }

            for(Class<?> interfaceClass:typeInfo.getJavaClass().getInterfaces()) {
                addDescendantClass(typeInfo, interfaceClass);
            }
        }

    }

    private void addDescendantClass(RMTypeInfo typeInfo, Class<?> interfaceClass) {
        RMTypeInfo superClassTypeInfo = this.getTypeInfo(interfaceClass);
        if(superClassTypeInfo != null) {
            typeInfo.addDirectParentClass(superClassTypeInfo);
            superClassTypeInfo.addDirectDescendantClass(typeInfo);
        }
    }

    /**
     * Add all subtypes of the given class
     * @param baseClass
     */
    protected <T> void addSubtypesOf(Class<T> baseClass) {
        Reflections reflections = new Reflections(ClasspathHelper.forClass(baseClass), Scanners.SubTypes.filterResultsBy(s -> true));
        Set<Class<? extends T>> classes = reflections.getSubTypesOf(baseClass);

        classes.forEach(this::addClass);
        addClass(baseClass);
    }

    protected void addClass(Class<?> clazz) {
        String rmTypeName = namingStrategy.getTypeName(clazz);
        RMTypeInfo typeInfo = new RMTypeInfo(clazz, rmTypeName);
        addAttributeInfo(clazz, typeInfo);
        addInvariantChecks(clazz, typeInfo);
        rmTypeNamesToRmTypeInfo.put(rmTypeName, typeInfo);
        classesToRmTypeInfo.put(clazz, typeInfo);
        if(!inConstructor) {
            //if someone called this after initial creation, we need to update super/subclass info.
            //could be done more efficiently by only updating for the added class and parents/descendants, but
            //should not be a problem to do it this way
            addSuperAndSubclassInfo();
            addAlternativeTypeNames();
        }
    }

    private void addAttributeInfo(Class<?> clazz, RMTypeInfo typeInfo) {
        //TODO: it's possible to constrain some method as well. should we do that here too?
        TypeToken<?> typeToken = TypeToken.of(clazz);

        Set<Field> allFields = ReflectionUtils.getAllFields(clazz);
        Map<String, Field> fieldsByName = allFields.stream()
                .filter( field -> !field.getName().startsWith("$")) //jacoco adds $ fields.. annoying :)
                .collect(Collectors.toMap((field) -> field.getName(), (field) -> field,
                        (duplicate1, duplicate2) -> duplicate1));
        for(Field field: fieldsByName.values()) {
            addRMAttributeInfo(clazz, typeInfo, typeToken, field);
        }
        if(addAttributesWithoutField) {
            Set<Method> getters = ReflectionUtils.getAllMethods(clazz, (method) -> method.getName().startsWith("get") || method.getName().startsWith("is"));
            Map<String, Method> gettersByName = getters.stream()
                    .filter(this::shouldAdd)
                    // Only use the most specific method for each name
                    .collect(Collectors.toMap(Method::getName, method -> method, new SpecificMethodSelector()));
            for (Method getMethod : gettersByName.values()) {
                addRMAttributeInfo(clazz, typeInfo, typeToken, getMethod, fieldsByName);
            }
        }
    }

    private void addInvariantChecks(Class clazz, RMTypeInfo typeInfo) {
        Set<Method> allInvariants = ReflectionUtils.getAllMethods(clazz, (method) -> method.getAnnotation(Invariant.class) != null);
        for(Method method:allInvariants) {
            if(method.getParameterCount() != 0) {
                throw new RuntimeException("An invariant check must not have any parameters, in method " + clazz.getSimpleName() + "::" + method.getName());
            }
            Class<?> returnType = method.getReturnType();
            if(!(returnType.equals(Boolean.class) || returnType.equals(boolean.class))) {
                throw new RuntimeException("An invariant check must return a boolean parameter, was " + returnType.getSimpleName() + " in method " + clazz.getSimpleName() + "::" + method.getName());
            }
            Invariant annotation = method.getAnnotation(Invariant.class);
            typeInfo.addInvariantMethod(method, annotation);
        }

    }

    protected boolean shouldAdd(Method method) {
        if(method == null) {
            return true;
        }
        //do not add invariants
        if(method.getAnnotation(Invariant.class) != null) {
            return false;
        }
        //do not add private or protected properties, they will result in errors
        return Modifier.isPublic(method.getModifiers()) && method.getAnnotation(RMPropertyIgnore.class) == null;
    }

    protected void addRMAttributeInfo(Class<?> clazz, RMTypeInfo typeInfo, TypeToken<?> typeToken, Method getMethod, Map<String, Field> fieldsByName) {
        String javaFieldName = null;
        if(getMethod.getName().startsWith("is")) {
            javaFieldName = lowerCaseFirstChar(getMethod.getName().substring(2));
        } else {
            javaFieldName = lowerCaseFirstChar(getMethod.getName().substring(3));
        }
        Field field = fieldsByName.get(javaFieldName);
        if(field == null) {
            field = fieldsByName.get(getMethod.getName());
        }
        String javaFieldNameUpperCased = upperCaseFirstChar(javaFieldName);
        Method setMethod = null, addMethod = null;

        if (getMethod == null) {
            getMethod = getMethod(clazz, "is" + javaFieldNameUpperCased);
        }
        if (getMethod != null) {
            setMethod = getMethod(clazz, "set" + javaFieldNameUpperCased, getMethod.getReturnType());
            addMethod = getAddMethod(clazz, typeToken, javaFieldNameUpperCased, getMethod);
        } else {
            logger.debug("No get method found for attribute {} on class {}", javaFieldName, clazz.getSimpleName());
        }

        String attributeName = namingStrategy.getAttributeName(field, getMethod);

        TypeToken<?> fieldType = typeToken.resolveType(getMethod.getGenericReturnType());;

        Class<?> rawFieldType = fieldType.getRawType();
        Class<?> typeInCollection = getTypeInCollection(fieldType);
        // if (setMethod != null) {

        RMAttributeInfo attributeInfo = new RMAttributeInfo(
                attributeName,
                field,
                rawFieldType,
                typeInCollection,
                this.namingStrategy.getTypeName(typeInCollection),
                isNullable(clazz, getMethod, field),
                getMethod,
                setMethod,
                addMethod,
                determineIfComputed(clazz, getMethod, field, setMethod, addMethod)
        );
        if(typeInfo.getAttribute(attributeName) == null) {
            typeInfo.addAttribute(attributeInfo);
        }
        //} else {
        //    logger.info("property without a set method ignored for field {} on class {}", attributeName, clazz.getSimpleName());
        // }
    }

    private boolean determineIfComputed(Class<?> clazz, Method getMethod, Field field, Method setMethod, Method addMethod) {
        boolean computed = setMethod == null && addMethod == null && field == null;

        RMProperty annotation = getAnnotation(clazz, getMethod, field, RMProperty.class);
        if(annotation != null && annotation.computed() != PropertyType.AUTO_DETECT) {
            computed = annotation.computed() == PropertyType.COMPUTED;
        }
        return computed;
    }

    protected boolean isNullable(Class<?> clazz, Method getMethod, Field field) {
        return getAnnotation(clazz, getMethod, field, Nullable.class) != null;
    }

    private <T extends Annotation> T getAnnotation(Class<?> clazz, Method getMethod, Field field, Class<T> annotationClass) {
        if(field != null) {
            T annotation = (T) field.getAnnotation(annotationClass);
            if(annotation != null) {
                return annotation;
            }
        }
        if(getMethod != null) {
            T annotation = (T) getMethod.getAnnotation(annotationClass);
            if(annotation != null) {
                return annotation;
            }
        }
        return null;
    }


    private void addRMAttributeInfo(Class<?> clazz, RMTypeInfo typeInfo, TypeToken<?> typeToken, Field field) {
        String javaFieldName = field.getName();
        String javaFieldNameUpperCased = upperCaseFirstChar(javaFieldName);
        Method getMethod = getMethod(clazz, "get" + javaFieldNameUpperCased);
        Method setMethod = null, addMethod = null;
        if (getMethod == null) {
            getMethod = getMethod(clazz, "is" + javaFieldNameUpperCased);
        }
        if (getMethod != null) {
            setMethod = getMethod(clazz, "set" + javaFieldNameUpperCased, getMethod.getReturnType());
            addMethod = getAddMethod(clazz, typeToken, javaFieldNameUpperCased, getMethod);
        } else {
            logger.debug("No get method found for field {} on class {}", field.getName(), clazz.getSimpleName());
        }

        if(javaFieldName.startsWith("is")) {
            //special case
            String fieldNameWithoutPrefix = javaFieldName.substring(2);
            String withoutPrefixUpperCased = upperCaseFirstChar(fieldNameWithoutPrefix);
            if (getMethod == null) {
                getMethod = getMethod(clazz, "is" + withoutPrefixUpperCased);
            }
            if (getMethod != null) {
                if(setMethod == null) {
                    setMethod = getMethod(clazz, "set" + withoutPrefixUpperCased, getMethod.getReturnType());
                }
                if(addMethod == null) {
                    addMethod = getAddMethod(clazz, typeToken, withoutPrefixUpperCased, getMethod);
                }
            } else {
                logger.debug("No get method found for attribute {} on class {}", javaFieldName, clazz.getSimpleName());
            }

        }
        String attributeName = namingStrategy.getAttributeName(field, getMethod);

        TypeToken<?> fieldType = null;
        if (getMethod != null) {
            fieldType = typeToken.resolveType(getMethod.getGenericReturnType());
        } else {
            fieldType = typeToken.resolveType(field.getGenericType());
        }

        Class<?> rawFieldType = fieldType.getRawType();
        Class<?> typeInCollection = getTypeInCollection(fieldType);
        if (setMethod != null && (shouldAdd(setMethod) && shouldAdd(getMethod))) {
            RMAttributeInfo attributeInfo = new RMAttributeInfo(
                    attributeName,
                    field,
                    rawFieldType,
                    typeInCollection,
                    namingStrategy.getTypeName(typeInCollection),
                    isNullable(clazz, getMethod, field),
                    getMethod,
                    setMethod,
                    addMethod,
                    determineIfComputed(clazz, getMethod, field, setMethod, addMethod)
            );
            typeInfo.addAttribute(attributeInfo);
        } else {
            logger.debug("property without a set method ignored for field {} on class {}", field.getName(), clazz.getSimpleName());
        }
    }

    private Class<?> getTypeInCollection(TypeToken<?> fieldType) {
        Class<?> rawFieldType = fieldType.getRawType();
        if (Collection.class.isAssignableFrom(rawFieldType)) {
            Type[] actualTypeArguments = ((ParameterizedType) fieldType.getType()).getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                //the java reflection api is kind of tricky with types. This works for the archie RM, but may cause problems for other RMs. The fix is implementing more ways
                if (actualTypeArguments[0] instanceof Class) {
                    return (Class<?>) actualTypeArguments[0];
                } else if (actualTypeArguments[0] instanceof ParameterizedType) {
                    ParameterizedType parameterizedTypeInCollection = (ParameterizedType) actualTypeArguments[0];
                    return (Class<?>) parameterizedTypeInCollection.getRawType();
                } else if (actualTypeArguments[0] instanceof java.lang.reflect.TypeVariable) {
                    return (Class<?>) ((java.lang.reflect.TypeVariable<?>) actualTypeArguments[0]).getBounds()[0];
                }
            }
        } else if(rawFieldType.isArray()) {
            return rawFieldType.getComponentType();
        }
        return rawFieldType;
    }

    private Method getAddMethod(Class<?> clazz, TypeToken<?> typeToken, String javaFieldNameUpperCased, Method getMethod) {
        Method addMethod = null;
        if (Collection.class.isAssignableFrom(getMethod.getReturnType())) {
            Type[] typeArguments = ((ParameterizedType) getMethod.getGenericReturnType()).getActualTypeArguments();
            if (typeArguments.length == 1) {
                TypeToken<?> singularParameter = typeToken.resolveType(typeArguments[0]);
                //TODO: does this work or should we use the typeArguments[0].getSomething?
                String addMethodName = "add" + toSingular(javaFieldNameUpperCased);
                addMethod = getMethod(clazz, addMethodName, singularParameter.getRawType());
                if (addMethod == null) {
                    //Due to generics, this does not always work
                    Set<Method> allAddMethods = ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withName(addMethodName));
                    if (allAddMethods.size() == 1) {
                        addMethod = allAddMethods.iterator().next();
                    } else {
                        logger.debug("strange number of add methods for field {} on class {}", javaFieldNameUpperCased, clazz.getSimpleName());
                    }
                }
            }
        }
        return addMethod;
    }

    private String toSingular(String javaFieldNameUpperCased) {
        if(javaFieldNameUpperCased.endsWith("s")) {
            return javaFieldNameUpperCased.substring(0, javaFieldNameUpperCased.length() - 1);
        }
        //TODO: a way to override plural names to go back to singular names. Use a library?
        return javaFieldNameUpperCased;
    }

    private Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch(NoSuchMethodException ex) {
            return null;
        }
    }

    private String upperCaseFirstChar(String name) {
        return new StringBuilder(name).replace(0,1,
                Character.toString(Character.toUpperCase(name.charAt(0)))
        ).toString();
    }

    private String lowerCaseFirstChar(String name) {
        return new StringBuilder(name).replace(0,1,
                Character.toString(Character.toLowerCase(name.charAt(0)))
        ).toString();
    }

    @Override
    public Class<?> getClass(String rmTypeName) {
        String strippedRmTypeName = getTypeWithoutGenericType(rmTypeName);
        RMTypeInfo rmTypeInfo = rmTypeNamesToRmTypeInfo.get(strippedRmTypeName);
        return rmTypeInfo == null ? null : rmTypeInfo.getJavaClass();
    }

    @Override
    public Class<?> getClassToBeCreated(String rmTypename) {
        return getClass(rmTypename);
    }

    @Override
    public Map<String, Class<?>> getRmTypeNameToClassMap() {
        HashMap<String, Class<?>> result = new HashMap<>();
        for(String rmTypeName: rmTypeNamesToRmTypeInfo.keySet()) {
            result.put(rmTypeName, rmTypeNamesToRmTypeInfo.get(rmTypeName).getJavaClass());
        }
        return result;
    }

    @Override
    public RMTypeInfo getTypeInfo(Class<?> clazz) {
        return this.classesToRmTypeInfo.get(clazz);
    }

    @Override
    public Field getField(Class<?> clazz, String attributeName) {
        RMTypeInfo typeInfo = classesToRmTypeInfo.get(clazz);
        RMAttributeInfo attributeInfo = typeInfo == null ? null : typeInfo.getAttribute(attributeName);
        return attributeInfo == null ? null : attributeInfo.getField();
    }

    @Override
    public RMTypeInfo   getTypeInfo(String rmTypeName) {
        String strippedRmTypeName = getTypeWithoutGenericType(rmTypeName);
        return this.rmTypeNamesToRmTypeInfo.get(strippedRmTypeName);
    }

    private String getTypeWithoutGenericType(String rmTypeName) {
        if(rmTypeName.indexOf('<') > 0) {
            //strip generic types, cannot handle them yet
            rmTypeName = rmTypeName.substring(0, rmTypeName.indexOf('<'));
        }
        return rmTypeName;
    }

    @Override
    public RMAttributeInfo getAttributeInfo(Class<?> clazz, String attributeName) {
        RMTypeInfo typeInfo = this.classesToRmTypeInfo.get(clazz);
        return typeInfo == null ? null : typeInfo.getAttribute(attributeName);
    }

    @Override
    public RMAttributeInfo getAttributeInfo(String rmTypeName, String attributeName) {
        String strippedRmTypeName = getTypeWithoutGenericType(rmTypeName);
        RMTypeInfo typeInfo = this.rmTypeNamesToRmTypeInfo.get(strippedRmTypeName);
        return typeInfo == null ? null : typeInfo.getAttribute(attributeName);
    }

    @Override
    public List<RMTypeInfo> getAllTypes() {
        return new ArrayList<>(classesToRmTypeInfo.values());
    }

    @Override
    public ModelNamingStrategy getNamingStrategy() {
        return namingStrategy;
    }

    /**
     * Convert the given reference model object to the object required for the archetype constraint.
     *
     * for example, a CTerminologyCode can be used to check a CodePhrase or a DvCodedText. This cannot be directly checked and must be converted first.
     *
     * @param object
     * @param cPrimitiveObject
     * @return
     */
    @Override
    public Object convertToConstraintObject(Object object, CPrimitiveObject<?, ?> cPrimitiveObject) {
        return object;
    }

    @Override
    public Object convertConstrainedPrimitiveToRMObject(Object object) {
        //TODO: this should take an AttributeInfo as param, so to be able to pick the right object
        return object;
    }
}
