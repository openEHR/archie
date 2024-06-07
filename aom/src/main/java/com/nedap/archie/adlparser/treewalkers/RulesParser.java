package com.nedap.archie.adlparser.treewalkers;

import com.nedap.archie.adlparser.antlr.AdlParser.*;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rules.*;
import com.nedap.archie.serializer.odin.OdinValueParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by pieter.bos on 27/10/15.
 */
public class RulesParser extends BaseTreeWalker {

    private PrimitivesConstraintParser primitivesConstraintParser;
    public static final Pattern VARIABLE_ASSIGNMENT_PATTERN = Pattern.compile("\\$(?<name>.*)\\:(?<type>.*)");

    private final MetaModels metaModels;

    public RulesParser(ANTLRParserErrors errors, MetaModels metaModels) {
        super(errors);
        primitivesConstraintParser = new PrimitivesConstraintParser(errors, metaModels);
        this.metaModels = metaModels;
    }

    public RuleStatement parse(AssertionContext assertionContext) {

        Assertion assertion = new Assertion();
        if(assertionContext.booleanAssertion() != null) {
            BooleanAssertionContext context = assertionContext.booleanAssertion();
            assertion.setStringExpression(context.getText());//TODO: this has whitespace stripped. Get the Lexer input instead
            if (context.identifier() != null) {
                assertion.setTag(context.identifier().getText());
            }
            assertion.setExpression(parseExpression(context.expression()));
            return assertion;
        } else if (assertionContext.variableDeclaration() != null) {
            VariableDeclaration declaration = parseVariableDeclaration(assertionContext.variableDeclaration());
            return declaration;
        }
        return assertion;
    }

    private VariableDeclaration parseVariableDeclaration(VariableDeclarationContext context) {
        ExpressionVariable result = new ExpressionVariable();
        setVariableNameAndType(context, result);
        result.setExpression(parseExpression(context.expression()));
        return result;

    }

    private void setVariableNameAndType(VariableDeclarationContext context, ExpressionVariable result) {
        Matcher matcher = VARIABLE_ASSIGNMENT_PATTERN.matcher(context.VARIABLE_DECLARATION().getText());

        if(matcher.matches()) {
            result.setName(matcher.group("name"));
            result.setType(ExpressionType.fromString(matcher.group("type")));
        } else {
            throw new IllegalStateException("variable declaration does not conform to $<name>:<type>. This should have been handled in the lexer and is likely a bug in Archie: " + context.getText());
        }
    }

    private Expression parseExpression(ExpressionContext context) {

        if(context.SYM_IMPLIES() != null) {
            BinaryOperator expression = new BinaryOperator();
            expression.setType(ExpressionType.BOOLEAN);
            expression.setOperator(OperatorKind.parse(context.SYM_IMPLIES().getText()));
            expression.addOperand(parseExpression(context.expression()));
            expression.addOperand(parseForAllExpression(context.booleanForAllExpression()));
            return expression;
        } else {
            return parseForAllExpression(context.booleanForAllExpression());
        }

    }

    private Expression parseForAllExpression(BooleanForAllExpressionContext context) {
        if(context.SYM_FOR_ALL() != null) {

            Expression pathExpression = null;

            if (context.adlRulesPath() != null) {
                pathExpression = parseModelReference(context.adlRulesPath());
            } else {
                pathExpression = parseVariableReference(context.variableReference());
            }
            String variableName = context.identifier().getText();
            return new ForAllStatement(variableName,
                    pathExpression,
                    parseForAllExpression(context.booleanForAllExpression()));
        } else {
            return parseOrExpression(context.booleanOrExpression());
        }

    }

    private Expression parseOrExpression(BooleanOrExpressionContext context) {
        if(context.SYM_OR() != null) {
            BinaryOperator expression = new BinaryOperator();
            expression.setType(ExpressionType.BOOLEAN);
            expression.setOperator(OperatorKind.parse(context.SYM_OR().getText()));
            expression.addOperand(parseOrExpression(context.booleanOrExpression()));
            expression.addOperand(parseAndExpression(context.booleanAndExpression()));
            return expression;
        } else {
            return parseAndExpression(context.booleanAndExpression());
        }
    }

    private Expression parseAndExpression(BooleanAndExpressionContext context) {
        if(context.SYM_AND() != null) {
            BinaryOperator expression = new BinaryOperator();
            expression.setType(ExpressionType.BOOLEAN);
            expression.setOperator(OperatorKind.parse(context.SYM_AND().getText()));
            expression.addOperand(parseAndExpression(context.booleanAndExpression()));
            expression.addOperand(parseXorExpression(context.booleanXorExpression()));
            return expression;
        } else {
            return parseXorExpression(context.booleanXorExpression());
        }
    }

    private Expression parseXorExpression(BooleanXorExpressionContext context) {
        if(context.SYM_XOR() != null) {
            BinaryOperator expression = new BinaryOperator();
            expression.setType(ExpressionType.BOOLEAN);
            expression.setOperator(OperatorKind.parse(context.SYM_XOR().getText()));
            expression.addOperand(parseBooleanNotExpression(context.booleanNotExpression()));
            expression.addOperand(parseXorExpression(context.booleanXorExpression()));
            return expression;
        } else {
            return parseBooleanNotExpression(context.booleanNotExpression());
        }
    }

    private Expression parseBooleanNotExpression(BooleanNotExpressionContext context) {
        if(context.SYM_NOT() != null) {
            return new UnaryOperator(ExpressionType.BOOLEAN, OperatorKind.not, context.SYM_NOT().getText(), parseBooleanNotExpression(context.booleanNotExpression()));
        } else {
            return parseBooleanConstraintExpression(context.booleanConstraintExpression());
        }
    }

    private Expression parseBooleanConstraintExpression(BooleanConstraintExpressionContext context) {
        if(context.booleanConstraint() != null) {
            return parseBooleanConstraint(context.booleanConstraint());
        } else {
            return parseEqualityExpression(context.equalityExpression());
        }
    }

    private Expression parseBooleanLiteral(BooleanLiteralContext context) {
        return new Constant<Boolean>(ExpressionType.BOOLEAN, context.SYM_TRUE() != null ? true : false);
    }

    private ModelReference parseModelReference(AdlRulesPathContext context) {
        String variableReference = null;
        String path = context.ADL_PATH().getText();
        if(context.SYM_VARIABLE_START() != null) {
            variableReference = CComplexObjectParser.getFirstAttributeOfPath(path);
            path = CComplexObjectParser.getPathMinusFirstAttribute(path);
        }

        return new ModelReference(variableReference, path);
    }


    private Expression parseBooleanConstraint(BooleanConstraintContext context) {
        Expression leftOperand = null;
        if(context.equalityExpression() != null) {
            leftOperand = parseEqualityExpression(context.equalityExpression());
        }

        CPrimitiveObject<?, ?> cPrimitiveObject = null;
        if(context.c_primitive_object() != null) {
            cPrimitiveObject = primitivesConstraintParser.parsePrimitiveObject(context.c_primitive_object());
        } else {
            cPrimitiveObject = primitivesConstraintParser.parseRegex(context.CONTAINED_REGEXP());
        }

        return new BinaryOperator(ExpressionType.BOOLEAN, OperatorKind.matches, context.SYM_MATCHES().getText(), leftOperand, new Constraint<>(cPrimitiveObject));
    }

    private Expression parseEqualityExpression(EqualityExpressionContext context) {
        if(context.equalityBinop() != null) {
            Expression left = parseEqualityExpression(context.equalityExpression());
            Expression right = parseRelOpExpression(context.relOpExpression());
            if(left.getType() != null && right.getType() != null && left.getType() != right.getType()) {
                throw new IllegalArgumentException("arithmetic relop expression with different types: " + left.getType() + " + " + right.getType());
            }
            String operatorSymbol = context.equalityBinop().getText();
            return new BinaryOperator(left.getType(), OperatorKind.parse(operatorSymbol), operatorSymbol, left, right);
        } else {
            return parseRelOpExpression(context.relOpExpression());
        }
    }

    private Expression parseRelOpExpression(RelOpExpressionContext context) {
        if(context.relationalBinop() != null) {
            Expression left = parseRelOpExpression(context.relOpExpression());
            Expression right = parseArithmeticExpression(context.arithmeticExpression());
            if(left.getType() != null && right.getType() != null && left.getType() != right.getType()) {
                throw new IllegalArgumentException("arithmetic relop expression with different types: " + left.getType() + " + " + right.getType());
            }
            String operatorSymbol = context.relationalBinop().getText();
            return new BinaryOperator(left.getType(), OperatorKind.parse(operatorSymbol), operatorSymbol, left, right);
        } else {
            return parseArithmeticExpression(context.arithmeticExpression());
        }

    }

    private Expression parseArithmeticExpression(ArithmeticExpressionContext context) {
        if(context.plusMinusBinop() != null) {
            Expression left = parseArithmeticExpression(context.arithmeticExpression().get(0));
            Expression right = parseArithmeticExpression(context.arithmeticExpression().get(1));
            String operatorSymbol = context.plusMinusBinop().getText();
            return new BinaryOperator(right.getType(), OperatorKind.parse(operatorSymbol), operatorSymbol, left, right);
        } else if(context.multBinop() != null) {
            Expression left = parseArithmeticExpression(context.arithmeticExpression().get(0));
            Expression right = parseArithmeticExpression(context.arithmeticExpression().get(1));
            String operatorSymbol = context.multBinop().getText();
            return new BinaryOperator(right.getType(), OperatorKind.parse(operatorSymbol), operatorSymbol, left, right);
        } else if(context.powBinop() != null) {
            Expression left = parseArithmeticExpression(context.arithmeticExpression().get(0));
            Expression right = parseArithmeticExpression(context.arithmeticExpression().get(1));
            String operatorSymbol = context.powBinop().getText();
            return new BinaryOperator(right.getType(), OperatorKind.parse(operatorSymbol), operatorSymbol, left, right);
        }else {
            return parseExpressionLeaf(context.expressionLeaf());
        }
    }



    private Expression parseExpressionLeaf(ExpressionLeafContext context) {
        if(context.integer_value() != null) {
            return new Constant<>(ExpressionType.INTEGER, Long.parseLong(context.integer_value().getText()));
        }
        else if(context.real_value() != null) {
            return new Constant<>(ExpressionType.REAL, Double.parseDouble(context.real_value().getText()));
        } else if (context.string_value() != null) {
            return new Constant<>(ExpressionType.STRING, OdinValueParser.parseOdinStringValue(context.string_value()));
        }
        else if(context.adlRulesPath() != null) {
            ModelReference reference = parseModelReference(context.adlRulesPath());
            if(context.SYM_EXISTS() != null) {
                return new UnaryOperator(ExpressionType.BOOLEAN, OperatorKind.exists, context.SYM_EXISTS().getText(), reference);
            } else {
                return reference;
            }
        }
        else if(context.expression() != null) {
            Expression expression = this.parseExpression(context.expression());
            expression.setPrecedenceOverridden(true);
            return expression;
        }
        else if(context.expressionLeaf() != null) { // - arithmetic expression
            return new UnaryOperator(ExpressionType.REAL, OperatorKind.minus, "-", parseExpressionLeaf(context.expressionLeaf()));
        }
        else if(context.variableReference() != null) {
            return parseVariableReference(context.variableReference());
        }
        else if(context.booleanLiteral() != null) {
            return parseBooleanLiteral(context.booleanLiteral());
        }
        else if(context.variableReference() != null) {
            return parseVariableReference(context.variableReference());
        } else if(context.functionName() != null) {
            return parseFunctionCall(context);
        }
        throw new IllegalArgumentException("cannot parse unknown arithmetic leaf type: " + context.getText());
    }

    private Expression parseFunctionCall(ExpressionLeafContext context) {
        String functionName = context.functionName().getText();
        List<Expression> arguments = parseArgumentList(context.argumentList());
        return new Function(functionName, arguments);
    }

    private List<Expression> parseArgumentList(ArgumentListContext argumentListContext) {
        List<Expression> expressions;
        if (argumentListContext == null) {  // No arguments
            expressions = new ArrayList<>();
        } else {
            expressions = new ArrayList<>(argumentListContext.expression().size());
            for (ExpressionContext expressionContext : argumentListContext.expression()) {
                expressions.add(parseExpression(expressionContext));
            }
        }
        return expressions;
    }

    private Expression parseVariableReference(VariableReferenceContext context) {
        VariableReference reference = new VariableReference();
        //TODO: retrieve declaration from actual declaration, instead of just setting the name
        VariableDeclaration declaration = new VariableDeclaration();
        declaration.setName(context.identifier().  getText());
        reference.setDeclaration(declaration);
        return reference;
    }
}
