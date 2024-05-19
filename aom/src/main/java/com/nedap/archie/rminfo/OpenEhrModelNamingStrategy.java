package com.nedap.archie.rminfo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.google.common.collect.Lists;
import com.nedap.archie.aom.CPrimitiveObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pieter.bos on 29/03/16.
 * A naming helper for both Archie RM and AOM objects
 */
public class OpenEhrModelNamingStrategy implements ModelNamingStrategy {

    public static final PropertyNamingStrategies.SnakeCaseStrategy snakeCaseStrategy = new PropertyNamingStrategies.SnakeCaseStrategy();

    private final boolean standardsCompliantExpressionNames;

    public OpenEhrModelNamingStrategy() {
        standardsCompliantExpressionNames = true;
    }

    public OpenEhrModelNamingStrategy(boolean standardCompliantExpressionNames) {
        this.standardsCompliantExpressionNames = standardCompliantExpressionNames;
    }

    private static final HashMap<String, String> rulesArchieToStandardTypeNamesMap = new HashMap<>();
    static {
        rulesArchieToStandardTypeNamesMap.put("Operator", "EXPR_OPERATOR");
        rulesArchieToStandardTypeNamesMap.put("UnaryOperator", "EXPR_UNARY_OPERATOR");
        rulesArchieToStandardTypeNamesMap.put("BinaryOperator", "EXPR_BINARY_OPERATOR");
        rulesArchieToStandardTypeNamesMap.put("Leaf", "EXPR_LEAF");
        rulesArchieToStandardTypeNamesMap.put("Function", "EXPR_FUNCTION");
        rulesArchieToStandardTypeNamesMap.put("VariableReference", "EXPR_VARIABLE_REF");
        rulesArchieToStandardTypeNamesMap.put("Constant", "EXPR_LITERAL");
        rulesArchieToStandardTypeNamesMap.put("Constraint", "EXPR_CONSTRAINT");
        rulesArchieToStandardTypeNamesMap.put("ArchetypeIdConstraint", "EXPR_ARCHETYPE_ID_CONSTRAINT");
        rulesArchieToStandardTypeNamesMap.put("ModelReference", "EXPR_ARCHETYPE_REF");
        rulesArchieToStandardTypeNamesMap.put("RuleStatement", "STATEMENT");
    }

    @Override
    public String getTypeName(Class<?> clazz) {
        // For some RM objects the name is an exception on the snakecase -> uppercase strategy
        String name = clazz.getSimpleName();
        switch(name) {
            case "DvEHRURI":
                return "DV_EHR_URI";
            case "UIDBasedId":
                return "UID_BASED_ID";
            default:
        }
        if(standardsCompliantExpressionNames) {
            if(rulesArchieToStandardTypeNamesMap.containsKey(name)) {
                return rulesArchieToStandardTypeNamesMap.get(name);
            }
        }
        return convertToUpperSnakeCase(clazz);
    }

    private String convertToUpperSnakeCase(Class<?> clazz) {
        String name = clazz.getSimpleName();
        String result = snakeCaseStrategy.translate(name).toUpperCase();

        // For some AOM objects (ie. CComplexObject and CAttribute), the name cannot be gotten
        // through the normal snakecase -> uppercase strategy
        if(name.length() > 1 && name.startsWith("C") && Character.isUpperCase(name.charAt(1))) {
            result = result.replaceFirst("C", "C_");
        }
        return result;
    }


    @Override
    public List<String> getAlternativeTypeNames(Class<?> clazz) {

        String name = clazz.getSimpleName();
        if(rulesArchieToStandardTypeNamesMap.containsKey(name)) {
            if(standardsCompliantExpressionNames) {
                return Lists.newArrayList(convertToUpperSnakeCase(clazz));
            } else {
                if(rulesArchieToStandardTypeNamesMap.containsKey(name)) {
                    return Lists.newArrayList(rulesArchieToStandardTypeNamesMap.get(name));
                }
            }
        }
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
        } else if (method.getName().startsWith("is")) {
            if(method.getName().equalsIgnoreCase("isIntegral")) {
                return "is_integral";
            } else {
                return snakeCaseStrategy.translate(method.getName().substring(2)).toLowerCase();
            }
        }
        return method.getName();
    }

    @Override
    public String getTypeNameForCPrimitiveType(Class<?> clazz) {

        // If it is a CPrimitiveObject, get rid of the leading 'C'
        String result = clazz.getSimpleName();
        if (CPrimitiveObject.class.isAssignableFrom(clazz)) {
            result = result.substring(1);
        }

        return snakeCaseStrategy.translate (result).toUpperCase();
    }

}
