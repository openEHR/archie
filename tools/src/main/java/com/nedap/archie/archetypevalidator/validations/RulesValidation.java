package com.nedap.archie.archetypevalidator.validations;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.archetypevalidator.ArchetypeValidationBase;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.paths.PathUtil;
import com.nedap.archie.query.APathQuery;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.rules.BinaryOperator;
import com.nedap.archie.rules.Expression;
import com.nedap.archie.rules.ExpressionVariable;
import com.nedap.archie.rules.ForAllStatement;
import com.nedap.archie.rules.Function;
import com.nedap.archie.rules.ModelReference;
import com.nedap.archie.rules.RuleStatement;
import com.nedap.archie.rules.UnaryOperator;
import org.openehr.utils.message.I18n;

import java.util.ArrayList;
import java.util.Collections;
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
                } else if (statement instanceof ExpressionVariable) {
                    ExpressionVariable variable = (ExpressionVariable) statement;
                    validate(variable.getExpression());

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
        if(!validatePath(getPath(reference))) {
            this.addWarning(ErrorType.VRRLPAR, reference.toString());
        }
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
            if(variableToPathMap.containsKey(pathExpression.getVariableReferencePrefix())) {
                return variableToPathMap.get(pathExpression.getVariableReferencePrefix()) + pathExpression.getPath();
            } else {
                addWarning(ErrorType.RULES_VARIABLE_NOT_DEFINED, I18n.t("Variable {0} used, but not defined", pathExpression.getVariableReferencePrefix()));
            }
        }
        return pathExpression.getPath();
    }

    private boolean validatePath(String path) {
        Archetype operationalTemplate = repository.getOperationalTemplate(archetype.getArchetypeId().toString());
        List<PathSegment> pathSegments = new APathQuery(path).getPathSegments();
        int i = pathSegments.size() -1;
        List<ArchetypeModelObject> archetypeModelObjects = null;
        String subPath = null;
        for(; i >= 0; i--) {
            subPath = joinPathUntil(pathSegments, i);
            archetypeModelObjects = operationalTemplate.itemsAtPath(subPath);
            if(!archetypeModelObjects.isEmpty()) {
                break;
            }
        }
        if(archetypeModelObjects.isEmpty()) {
            return false;
        } else {
            String restOfPath = i >= pathSegments.size() -1 ? "" : PathUtil.getPath(pathSegments.subList(i+1, pathSegments.size()));
            if(restOfPath.isEmpty()) {
                return true;
            }
            for(ArchetypeModelObject object: archetypeModelObjects) {
                List<String> typeNames = getTypeNames(object);
                for(String typeName:typeNames) {
                    if (this.combinedModels.hasReferenceModelPath(typeName, restOfPath)) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

    private List<String> getTypeNames(ArchetypeModelObject object) {
        if(object instanceof CObject) {
            return Lists.newArrayList(((CObject) object).getRmTypeName());
        } else if (object instanceof CAttribute) {
            CAttribute attribute = (CAttribute) object;
            ArrayList<String> result= new ArrayList<>();
            for(CObject child:attribute.getChildren()) {
                result.addAll(getTypeNames(child));
            }
            return result;
        }
        return Collections.emptyList();
    }

    private String joinPathUntil(List<PathSegment> pathSegments, int i) {
        return PathUtil.getPath(pathSegments.subList(0, i+1));
    }
}
