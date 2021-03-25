//
//  description: Antlr4 grammar for Rules sub-syntax of Archetype Definition Language (ADL2)
//  author:      Thomas Beale <thomas.beale@openehr.org>
//  support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
//  copyright:   Copyright (c) 2015 openEHR Foundation
//  license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//
//TODO: We could rebuild this based on a modified xpath-grammar. Will make this easier to comply with xpath syntax

grammar adl_rules;
import cadl_primitives;

//
//  ============== Parser rules ==============
//

assertion_list: (assertion (';')?)+ ;// {_input.LA(1) == WS || _input.LA(1) == LINE}?) +;//whitespace parsing to prevent ambiguity

assertion: variableDeclaration | booleanAssertion;

variableDeclaration: VARIABLE_DECLARATION SYM_ASSIGNMENT expression;

booleanAssertion: ( identifier SYM_COLON )? expression ;



//
// Expressions evaluating to boolean values
//


expression
    : booleanForAllExpression
    | expression SYM_IMPLIES booleanForAllExpression
    ;

booleanForAllExpression
    : booleanOrExpression
    | SYM_FOR_ALL SYM_VARIABLE_START identifier SYM_IN (adlRulesPath | variableReference) SYM_SATISFIES? booleanForAllExpression;

booleanOrExpression
    : booleanAndExpression
    | booleanOrExpression SYM_OR booleanAndExpression
    ;

booleanAndExpression
	:	booleanXorExpression
	|	booleanAndExpression SYM_AND booleanXorExpression
	;

booleanXorExpression
	:	booleanNotExpression
	|	booleanXorExpression SYM_XOR booleanNotExpression
	;

booleanNotExpression
    : SYM_NOT booleanNotExpression
    | booleanConstraintExpression
	;

booleanConstraintExpression
    : booleanConstraint
    | equalityExpression;


booleanConstraint: equalityExpression SYM_MATCHES ('{' c_primitive_object '}' | CONTAINED_REGEXP );

equalityExpression:
    relOpExpression
    | equalityExpression equalityBinop relOpExpression ;

relOpExpression:
    arithmeticExpression
    | relOpExpression relationalBinop arithmeticExpression ;


//
// Expressions evaluating to all kinds of value types
//

arithmeticExpression
   : <assoc=right> arithmeticExpression powBinop arithmeticExpression
   | arithmeticExpression multBinop arithmeticExpression
   | arithmeticExpression plusMinusBinop arithmeticExpression
   | expressionLeaf
   ;

expressionLeaf
    : booleanLiteral
    | integer_value
    | real_value
    | string_value
    | adlRulesPath
    | SYM_EXISTS adlRulesPath
    | functionName '(' (argumentList)? ')'
    | variableReference
    | '(' expression ')'
    | '-' expressionLeaf
    ;

argumentList:
    expression (',' expression)*
    ;

functionName:
    identifier;

adlRulesPath          : SYM_VARIABLE_START? ADL_PATH;

variableReference: SYM_VARIABLE_START identifier;

plusMinusBinop: '+' | '-';
multBinop: '*' | '/' | '%';
powBinop: '^';

equalityBinop
    : '='
    | '!='
    ;

relationalBinop
    : '<='
    | '<'
    | '>='
    | '>'
    ;

booleanLiteral
    : SYM_TRUE
    | SYM_FALSE
    ;

SYM_FOR_ALL
    : 'for_all'
    | '∀'
    | 'every' //if we follow xpath syntax, let's do that here as well (xpath 2 and xpath 3)
    ;

SYM_IN:
    'in'; //should be | '∈';, but that clashes with SYM_MATCHES, wich is also '∈'.

SYM_SATISFIES:
    'satisfies'; //from xpath - solves some parser ambiguity in future cases!



