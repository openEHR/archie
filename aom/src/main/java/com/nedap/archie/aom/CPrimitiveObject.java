package com.nedap.archie.aom;

import com.nedap.archie.rminfo.ArchieModelNamingStrategy;
import com.nedap.archie.rminfo.ModelInfoLookup;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Primitive object. Parameterized with a Constraint type and AssumedAndDefault value type, to be able to override
 * the methods in subclasses easily
 *
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_PRIMITIVE_OBJECT")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CPrimitiveObject<Constraint, ValueType> extends CDefinedObject<ValueType> {

    public static final String PRIMITIVE_NODE_ID_VALUE = "id9999";

    private Boolean enumeratedTypeConstraint;

    public abstract ValueType getAssumedValue();

    public abstract void setAssumedValue(ValueType assumedValue);

    public abstract List<Constraint> getConstraint();

    public abstract void setConstraint(List<Constraint> constraint);

    public abstract void addConstraint(Constraint constraint);

    public Boolean getEnumeratedTypeConstraint() {
        return enumeratedTypeConstraint;
    }

    public void setEnumeratedTypeConstraint(Boolean enumeratedTypeConstraint) {
        this.enumeratedTypeConstraint = enumeratedTypeConstraint;
    }



    public String getNodeId() {
        return PRIMITIVE_NODE_ID_VALUE;
    }

    public void setNodeId(String nodeId) {
        if(!nodeId.equals(PRIMITIVE_NODE_ID_VALUE)) {
            throw new UnsupportedOperationException("Cannot set node id on a CPrimitiveObject");
        }
    }

    /**
     * True if the given value is a valid value for this constraint
     * Must be overridden in classes where the AssumedAndDefaultValue is not the actual value.
     * For example when it is an interval or pattern
     *
     * @param value
     * @return
     */
    public boolean isValidValue(ValueType value) {
        if(getConstraint().isEmpty()) {
            return true;
        }
        for(Constraint constraint:getConstraint()) {
            if(Objects.equals(constraint, value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if the given value is a valid value for this constraint
     * first Converts the value to a checkable value using the given ModelInfoLookup
     * For example when it is an interval or pattern
     *
     * @param value
     * @return
     */
    public boolean isValidValue(ModelInfoLookup lookup, Object value) {
        Object convertedValue = lookup.convertToConstraintObject(value, this);
        return isValidValue((ValueType) convertedValue);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");
        boolean first = true;
        for(Constraint constraint:getConstraint()) {
            if(!first) {
                result.append(", ");
            }
            first = false;
            if(constraint instanceof String) {
                result.append('"');
                result.append(((String) constraint).replace("\"", "\\\""));
                result.append('"');
            } else {
                result.append(constraint.toString());
            }
        }
        result.append("}");
        return result.toString();
    }

    @Override
    public boolean cConformsTo(CObject other, BiFunction<String, String, Boolean> rmTypesConformant) {
        if(other instanceof CPrimitiveObject && other.getClass().equals(getClass())) {
            if(other == null) {
                return false;
            }
            return occurrencesConformsTo(other) && getRmTypeName().equalsIgnoreCase(other.getRmTypeName());
        } else {
            return false;
        }
    }

    public String constrainedTypename () {
        //TODO: this works usually, but probably needs RM access
        //TODO: add to parserPostProcessor that rmTypeName will be set
        return ArchieModelNamingStrategy.snakeCaseStrategy.translate(this.getClass().getSimpleName().substring(1));
    }

    public String getRmTypeName() {
        return constrainedTypename();
    }

    public boolean hasAssumedValue() {
        return getAssumedValue() != null;
    }

    @Override
    public boolean isLeaf() {
        return true; //primitive nodes are leafs.
    }

}

