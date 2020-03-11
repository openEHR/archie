package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.archetypevalidator.ArchetypeValidationBase;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.paths.PathUtil;
import com.nedap.archie.query.APathQuery;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.ForAllStatement;
import com.nedap.archie.rules.Function;
import com.nedap.archie.rules.ModelReference;
import com.nedap.archie.rules.RuleStatement;
import com.nedap.archie.rules.UnaryOperator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RulesValidation extends ArchetypeValidationBase {


    private Map<String, String> variableToPathMap;

    @Override
    public void validate() {
        variableToPathMap = new LinkedHashMap<>();
        if(archetype.getRules() != null && archetype.getRules().getRules() != null) {
            for(RuleStatement statement:archetype.getRules().getRules()) {
                if(statement instanceof Assertion) {
                    Assertion toValidate = (Assertion) statement;
                    validate(toValidate);
                }
            }
        }
    }

    private void validate(Assertion toValidate) {
        Expression expression = toValidate.getExpression();
        validate(expression);
    }

    private void validate(Expression expression) {
        if(expression instanceof BinaryOperator) {
            validate((BinaryOperator) expression);
        } else if (expression instanceof ModelReference) {
            validate((ModelReference) expression);
        } else if (expression instanceof UnaryOperator) {
            validate((UnaryOperator) expression);
        } else if (expression instanceof Function) {
            validate((Function) expression);
        } else if (expression instanceof ForAllStatement) {
            validate((ForAllStatement) expression);
        }
    }

    private void validate(BinaryOperator operator) {
        validate(operator.getLeftOperand());
        validate(operator.getRightOperand());
    }

    private void validate(UnaryOperator operator) {
        validate(operator.getOperand());
    }

    private void validate(ModelReference reference) {
        validatePath(getPath(reference));
    }

    private void validate(Function function) {
        for(Expression argument:function.getArguments()) {
            validate(argument);
        }
    }

    private void validate(ForAllStatement statement) {
        if(statement.getPathExpression() instanceof ModelReference) {
            String path = getPath((ModelReference) statement.getPathExpression());
            variableToPathMap.put(statement.getVariableName(), path);
            validate(statement.getAssertion());
            variableToPathMap.remove(statement.getVariableName());
        } else {
            //cannot validate yet
        }

    }

    private String getPath(ModelReference pathExpression) {
        if(pathExpression.getVariableReferencePrefix() != null && !pathExpression.getVariableReferencePrefix().isEmpty()) {
            return pathExpression.getVariableReferencePrefix() + pathExpression.getPath();
        }
        return pathExpression.getPath();
    }

    private boolean validatePath(String path) {
        Archetype operationalTemplate = repository.getOperationalTemplate(archetype.getArchetypeId().toString());
        List<PathSegment> pathSegments = new APathQuery(path).getPathSegments();
        int i = pathSegments.size() -1;
        List<ArchetypeModelObject> archetypeModelObjects = null;
        for(; i >= 0; i--) {
            String subPath = joinPathUntil(pathSegments, i);
            archetypeModelObjects = operationalTemplate.itemsAtPath(subPath);
            if(!archetypeModelObjects.isEmpty()) {
                break;
            }
        }
        if(archetypeModelObjects.isEmpty()) {
            return false;
        } else {
            String restOfPath = PathUtil.getPath(pathSegments.subList(i, pathSegments.size()));
            for(ArchetypeModelObject object: archetypeModelObjects) {
                String typeName = getTypeName(object);
                if(typeName != null && this.combinedModels.hasReferenceModelPath(typeName, restOfPath)) {
                    return true;
                }
            }
        }
        return false;

    }

    private String getTypeName(ArchetypeModelObject object) {
        if(object instanceof CObject) {
            return ((CObject) object).getRmTypeName();
        }
        return null;
    }

    private String joinPathUntil(List<PathSegment> pathSegments, int i) {
        return PathUtil.getPath(pathSegments.subList(0, i+1));
    }
}
