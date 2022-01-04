package com.nedap.archie.rminfo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Created by pieter.bos on 29/03/16.
 * A naming helper for both Archie RM and AOM objects
 */
public class ArchieModelNamingStrategy implements ModelNamingStrategy {

    public static final PropertyNamingStrategies.SnakeCaseStrategy snakeCaseStrategy = new PropertyNamingStrategies.SnakeCaseStrategy();

    private final boolean standardsCompliantExpressionNames;

    public ArchieModelNamingStrategy() {
        standardsCompliantExpressionNames = true;
    }

    public ArchieModelNamingStrategy(boolean standardCompliantExpressionNames) {
        this.standardsCompliantExpressionNames = standardCompliantExpressionNames;
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
            switch (name) {
                case "Operator":
                    return "EXPR_OPERATOR";
                case "UnaryOperator":
                    return "EXPR_UNARY_OPERATOR";
                case "BinaryOperator":
                    return "EXPR_BINARY_OPERATOR";
                case "Leaf":
                    return "EXPR_LITERAL";
                case "Function":
                    return "EXPR_FUNCTION";
                case "VariableReference":
                    return "EXPR_VARIABLE_REF";
                case "Constant":
                    return "EXPR_CONSTANT";
                case "Constraint":
                    return "EXPR_CONSTRAINT";
                case "ArchetypeIdConstraint":
                    return "EXPR_ARCHETYPE_ID_CONSTRAINT";
                case "ModelReference":
                    return "EXPR_ARCHETYPE_REF";

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
        if(!standardsCompliantExpressionNames) {
            return Collections.emptyList();
        }
        String name = clazz.getSimpleName();
        switch (name) {
            case "Operator":
            case "UnaryOperator":
            case "BinaryOperator":
            case "Leaf":
            case "Function":
            case "VariableReference":
            case "Constant":
            case "Constraint":
            case "ArchetypeIdConstraint":
            case "ModelReference":
                return Lists.newArrayList(getAlternativeTypeNames(clazz));

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
}
