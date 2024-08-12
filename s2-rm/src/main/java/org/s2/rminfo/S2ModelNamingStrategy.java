package org.s2.rminfo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.google.common.collect.Lists;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.rminfo.ModelNamingStrategy;
import com.nedap.archie.rminfo.RMProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Thomas Beale on 24/04/24.
 * A naming helper for both Archie RM and AOM objects
 */
public class S2ModelNamingStrategy implements ModelNamingStrategy {

    public static final PropertyNamingStrategies.SnakeCaseStrategy snakeCaseStrategy = new PropertyNamingStrategies.SnakeCaseStrategy();

    private final boolean standardsCompliantExpressionNames;

    public S2ModelNamingStrategy() {
        standardsCompliantExpressionNames = true;
    }

    public S2ModelNamingStrategy(boolean standardCompliantExpressionNames) {
        this.standardsCompliantExpressionNames = standardCompliantExpressionNames;
    }

    private static final HashMap<String, String> rulesArchieToStandardTypeNamesMap = new HashMap<>();

    @Override
    public String getTypeName(Class<?> clazz) {
        // For some RM objects the name is an exception on the snakecase -> uppercase strategy
        String name = clazz.getSimpleName();

        if(standardsCompliantExpressionNames) {
            if(rulesArchieToStandardTypeNamesMap.containsKey(name)) {
                return rulesArchieToStandardTypeNamesMap.get(name);
            }
        }
        String snakeCase = convertToSnakeCase(clazz);
        String result = snakeCase.substring(0,1).toUpperCase();
        result = result.concat (snakeCase.substring(1));
        return result;
    }

    private String convertToSnakeCase(Class<?> clazz) {
        String name = clazz.getSimpleName();
        String result = snakeCaseStrategy.translate(name);

        // For some AOM objects (ie. CComplexObject and CAttribute), the name cannot be gotten
        // through the normal snakecase -> uppercase strategy
        if(name.length() > 1 && name.startsWith("C") && Character.isUpperCase(name.charAt(1))) {
            result = result.replaceFirst("C", "C_");
        }
        return result;
    }


    @Override
    public List<String> getAlternativeTypeNames(Class<?> clazz) {
        return Collections.emptyList();
    }

    @Override
    public String getAttributeName(Field field, Method method) {
        if(field != null) {
            RMProperty annotation = field.getDeclaredAnnotation(RMProperty.class);
            if (annotation != null) {
                return annotation.value();
            }
        }

        if(method != null) {
            RMProperty annotation = method.getDeclaredAnnotation(RMProperty.class);
            if(annotation != null) {
                return annotation.value();
            }
        }

        if(field != null) {
            return snakeCaseStrategy.translate(field.getName());
        }

        if(method.getName().startsWith("get") ||
                method.getName().startsWith("set") ||
                method.getName().startsWith("add") ) {
            return snakeCaseStrategy.translate(method.getName().substring(3)).toLowerCase();
        }
        else if (method.getName().startsWith("is")) {
            return snakeCaseStrategy.translate(method.getName().substring(2)).toLowerCase();
        }
        return method.getName();
    }

    @Override
    public String getTypeNameForCPrimitiveType(Class<?> clazz) {
        String result = clazz.getSimpleName();
        if (CPrimitiveObject.class.isAssignableFrom(clazz)) {
            result = result.substring(1);
        }

        return result;
    }
}
