//
//  description: Antlr4 grammar for openEHR Expression Language.
//  author:      Thomas Beale <thomas.beale@openehr.org>
//  contributors:Pieter Bos <pieter.bos@nedap.com>
//  support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
//  copyright:   Copyright (c) 2016- openEHR Foundation <http://www.openEHR.org>
//  license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//

grammar El;
import PathLexer, CPrimitiveValuesParser;

//
//  ======================= Top-level Objects ========================
//

statementBlock: statement+ EOF ;

// ------------------------- statements ---------------------------
statement: declaration | assignment | assertion;

declaration:
      variableDeclaration
    | constantDeclaration
    ;

variableDeclaration: variableName ':' typeId ( SYM_ASSIGNMENT expression )? ;

constantDeclaration: constantName ':' typeId  ( SYM_EQ primitiveObject )? ;

assignment:
      binding
    | localAssignment
    ;

//
// The following is the means of binding a data context path to a local variable
// TODO: remove this rule when proper external bindings are supported
binding: variableName SYM_ASSIGNMENT rawPath ;

localAssignment: variableName SYM_ASSIGNMENT expression ;

assertion: ( ( ALPHA_LC_ID | ALPHA_UC_ID ) ':' )? booleanExpr ;

//
// -------------------------- Expressions --------------------------
//
expression:
      booleanExpr
    | arithmeticExpr
    ;

//
// Expressions evaluating to boolean values, using standard precedence
// The equalityBinop ones are not strictly necessary, but allow the use
// of booleanLeaf = true, which some people like
//
booleanExpr:
      SYM_NOT booleanExpr
    | booleanExpr SYM_AND booleanExpr
    | booleanExpr SYM_XOR booleanExpr
    | booleanExpr SYM_OR booleanExpr
    | booleanExpr SYM_IMPLIES booleanExpr
    | booleanLeaf equalityBinop booleanLeaf
    | booleanLeaf
    ;

//
// Atomic Boolean-valued expression elements
// TODO: SYM_EXISTS alternative to be replaced by defined() predicate
booleanLeaf:
      booleanLiteral
    | forAllExpr
    | thereExistsExpr
    | SYM_EXISTS ( rawPath | variableSubPath )
    | '(' booleanExpr ')'
    | relationalExpr
    | equalityExpr
    | constraintExpr
    | valueRef
    ;

booleanLiteral:
      SYM_TRUE
    | SYM_FALSE
    ;

//
//  Universal and existential quantifier
// TODO: 'in' probably isn't needed in the long term
forAllExpr: SYM_FOR_ALL VARIABLE_ID ( ':' | 'in' ) valueRef '|'? booleanExpr ;

thereExistsExpr: SYM_THERE_EXISTS VARIABLE_ID ( ':' | 'in' ) valueRef '|'? booleanExpr ;

// Constraint expressions
// This provides a way of using one operator (matches) to compare a
// value (LHS) with a value range (RHS). As per ADL, the value range
// for ordered types like Integer, Date etc may be a single value,
// a list of values, or a list of intervals, and in future, potentially
// other comparators, including functions (e.g. divisible_by_N).
//
// For non-ordered types like String and Terminology_code, the RHS
// is in other forms, e.g. regex for Strings.
//
// The matches operator can be used to generate a Boolean value that
// may be used within an expression like any other Boolean (hence it
// is a booleanLeaf).
// TODO: non-primitive objects might be supported on the RHS in future.
constraintExpr: ( arithmeticExpr | valueRef ) SYM_MATCHES '{' cInlinePrimitiveObject '}' ;

//
// Expressions evaluating to arithmetic values, using standard precedence
//
arithmeticExpr:
      <assoc=right> arithmeticExpr '^' arithmeticExpr
    | arithmeticExpr ( '/' | '*' | '%' ) arithmeticExpr
    | arithmeticExpr ( '+' | '-' ) arithmeticExpr
    | arithmeticLeaf
    ;

arithmeticLeaf:
      arithmeticLiteral
    | valueRef
    | '(' arithmeticExpr ')'
    ;

arithmeticLiteral:
      integerValue
    | realValue
    | dateValue
    | dateTimeValue
    | timeValue
    | durationValue
    ;

//
// Equality expression between any arithmetic value; precedence is
// lowest, so only needed between leaves, since () will be needed for
// larger expressions anyway
//
equalityExpr: arithmeticExpr equalityBinop arithmeticExpr ;

equalityBinop:
      SYM_EQ
    | SYM_NE
    ;

//
// Relational expressions of arithmetic operands generating Boolean values
//
relationalExpr: arithmeticExpr relationalBinop arithmeticExpr ;

relationalBinop:
      SYM_GT
    | SYM_LT
    | SYM_LE
    | SYM_GE
    ;

//
// instances references: data references, variables, and function calls.
// TODO: Remove rawPath from this rule when external binding supported
//
valueRef:
      functionCall
    | rawPath
    | variableSubPath
    | variableName
    | constantName
    ;

variableName: VARIABLE_ID ;

// TODO: change to [] form, e.g.     book_list [{title.contains("Quixote")}]
variableSubPath: VARIABLE_WITH_PATH;

// TODO: Remove this rule when external binding supported
rawPath: ADL_PATH ;

constantName: ALPHA_UC_ID ;

functionCall: ALPHA_LC_ID '(' functionArgs? ')' ;

functionArgs: expression ( ',' expression )* ;

typeId: ALPHA_UC_ID ( '<' typeId ( ',' typeId )* '>' )? ;


//
// ---------- Lexer definitions ----------
//

// ---------- lines and comments ----------
CMT_LINE   : '--' .*? EOL -> skip ;             // increment line count
EOL        : '\r'? '\n'   -> channel(HIDDEN) ;  // increment line count
WS         : [ \t\r]+     -> channel(HIDDEN) ;

// --------- symbols ----------
SYM_ASSIGNMENT: ':=' | '::=' ;

SYM_NE : '/=' | '!=' | '≠' ;
SYM_EQ : '=' ;
SYM_GT : '>' ;
SYM_LT : '<' ;
SYM_LE : '<=' | '≤' ;
SYM_GE : '>=' | '≥' ;

SYM_THEN     : [Tt][Hh][Ee][Nn] ;
SYM_AND      : [Aa][Nn][Dd] | '∧' ;
SYM_OR       : [Oo][Rr] | '∨' ;
SYM_XOR      : [Xx][Oo][Rr] ;
SYM_NOT      : [Nn][Oo][Tt] | '!' | '~' | '¬' ;
SYM_IMPLIES  : [Ii][Mm][Pp][Ll][Ii][Ee][Ss] | '⇒' ;
SYM_FOR_ALL  : 'for_all' | '∀' ;
SYM_THERE_EXISTS: 'there_exists' | '∃' ;
SYM_EXISTS   : 'exists' ;
SYM_MATCHES  : [Mm][Aa][Tt][Cc][Hh][Ee][Ss] | [Ii][Ss]'_'[Ii][Nn] | '∈' ;

// TODO: remove when [] path predicates supported
VARIABLE_WITH_PATH: VARIABLE_ID ADL_ABSOLUTE_PATH ;

VARIABLE_ID: '$' ALPHA_LC_ID ;
