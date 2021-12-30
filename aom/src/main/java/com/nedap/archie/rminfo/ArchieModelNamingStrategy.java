package com.nedap.archie.rminfo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by pieter.bos on 29/03/16.
 * A naming helper for both Archie RM and AOM objects
 */
public class ArchieModelNamingStrategy implements ModelNamingStrategy {

    public static final PropertyNamingStrategies.SnakeCaseStrategy snakeCaseStrategy = new PropertyNamingStrategies.SnakeCaseStrategy();

    private boolean standardsCompliantExpressionNames = true;

    public ArchieModelNamingStrategy() {
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
                    return "EXPR_LITERAL";
                case "Constraint":
                    return "EXPR_CONSTRAINT";
                case "ArchetypeIdConstraint":
                    return "EXPR_ARCHETYPE_ID_CONSTRAINT";
                case "ModelReference":
                    return "EXPR_ARCHETYPE_REF";

            }
        }
        String result = snakeCaseStrategy.translate(clazz.getSimpleName()).toUpperCase();

        // For some AOM objects (ie. CComplexObject and CAttribute), the name cannot be gotten
        // through the normal snakecase -> uppercase strategy
        if(name.length() > 1 && name.startsWith("C") && Character.isUpperCase(name.charAt(1))) {
            result = result.replaceFirst("C", "C_");
        }
        return result;
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
