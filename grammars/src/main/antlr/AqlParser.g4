//
//  description:  ANTLR4 parser grammar for Archetype Query Language (AQL)
//  authors:      Sebastian Iancu, Code24, Netherlands
//                Teun van Hemert, Nedap, Netherlands
//                Thomas Beale, Ars Semantica UK, openEHR Foundation Management Board
//  contributors: This version of the grammar is a complete rewrite of previously published antlr3 grammar,
//                based on current AQL specifications in combination with grammars of AQL implementations.
//                The openEHR Foundation would like to recognise the following people for their contributions:
//                  - Chunlan Ma & Heath Frankel, Ocean Health Systems, Australia
//                  - Bostjan Lah, Better, Slovenia
//                  - Christian Chevalley, EHRBase, Germany
//                  - Michael Böckers, Nedap, Netherlands
//  support:      openEHR Specifications PR tracker <https://specifications.openehr.org/releases/QUERY/open_issues>
//  copyright:    Copyright (c) 2021- openEHR Foundation
//  license:      Creative Commons CC-BY-SA <https://creativecommons.org/licenses/by-sa/3.0/>
//

// This grammar is optimized for use by Nedap. It accepts the same language as the grammar from the specification,
// but is more efficient as it produces a shallower parse tree, and where convenient alternatives and nodes are aliassed.

parser grammar AqlParser;

options { tokenVocab=AqlLexer; }

query
    : SELECT DISTINCT? (TOP limit=INTEGER direction=(FORWARD|BACKWARD)?)? selectExpr (SYM_COMMA selectExpr)* FROM fromExpr (WHERE whereExpr)? (ORDER BY orderByExpr (SYM_COMMA orderByExpr)*)? (LIMIT limit=INTEGER (OFFSET offset=INTEGER)?)? SYM_SEMICOLON? EOF
    ;

selectExpr
    : columnExpr (AS aliasName=IDENTIFIER)?
    ;

whereExpr
    : identifiedExpr                                                                                                    #whereExprIdentifiedExpr
    | NOT whereExpr                                                                                                     #whereExprNot
    | left=whereExpr AND right=whereExpr                                                                                #whereExprAnd
    | left=whereExpr OR right=whereExpr                                                                                 #whereExprOr
    | SYM_LEFT_PAREN whereExpr SYM_RIGHT_PAREN                                                                          #whereExprParentheses
    ;

orderByExpr
    : identifiedPath order=(DESCENDING|DESC|ASCENDING|ASC)?
    ;

columnExpr
    : identifiedPath                                                                                                    #columnExprIdentifiedPath
    | primitive                                                                                                         #columnExprPrimitive
    | aggregateFunctionCall                                                                                             #columnExprAggregateFunctionCall
    | functionCall                                                                                                      #columnExprFunctionCall
    ;

fromExpr
    : classExprOperand (NOT? CONTAINS fromExpr)?                                                                        #fromExprSimple
    | left=fromExpr AND right=fromExpr                                                                                  #fromExprAnd
    | left=fromExpr OR right=fromExpr                                                                                   #fromExprOr
    | SYM_LEFT_PAREN fromExpr SYM_RIGHT_PAREN                                                                           #fromExprParentheses
    ;

identifiedExpr
    : EXISTS identifiedPath                                                                                             #identifiedExprExists
    | identifiedPath operator=COMPARISON_OPERATOR terminal                                                              #identifiedExprIdentifiedPathComparison
    | functionCall operator=COMPARISON_OPERATOR terminal                                                                #identifiedExprFunctionCallComparison
    | identifiedPath LIKE likeOperand                                                                                   #identifiedExprLike
    | identifiedPath MATCHES matchesOperand                                                                             #identifiedExprMatches
    | SYM_LEFT_PAREN identifiedExpr SYM_RIGHT_PAREN                                                                     #identifiedExprParentheses
    ;

classExprOperand
    : IDENTIFIER variable=IDENTIFIER? pathPredicate?                                                                    #classExprOperandSimple
    | VERSION variable=IDENTIFIER? (SYM_LEFT_BRACKET versionPredicate SYM_RIGHT_BRACKET)?                               #classExprOperandVersion
    ;

terminal
    : primitive                                                                                                         #terminalPrimitive
    | param=PARAMETER                                                                                                   #terminalParameter
    | identifiedPath                                                                                                    #terminalIdentifiedPath
    | functionCall                                                                                                      #terminalFunctionCall
    ;

identifiedPath
    : IDENTIFIER pathPredicate? (SYM_SLASH objectPath)?
    ;

pathPredicate
    : SYM_LEFT_BRACKET standardPredicate SYM_RIGHT_BRACKET                                                              #pathPredicateStandardPredicate
    | SYM_LEFT_BRACKET archetypePredicate SYM_RIGHT_BRACKET                                                             #pathPredicateArchetypePredicate
    | SYM_LEFT_BRACKET nodePredicate SYM_RIGHT_BRACKET                                                                  #pathPredicateNodePredicate
    ;

standardPredicate
    : objectPath operator=COMPARISON_OPERATOR pathPredicateOperand
    ;

archetypePredicate
    : ARCHETYPE_HRID                                                                                                    #archetypePredicateArchetypeHrid
    | PARAMETER                                                                                                         #archetypePredicateParameter
    ;

nodePredicate
    : code=(ID_CODE | AT_CODE) (SYM_COMMA value=(STRING | PARAMETER | TERM_CODE | AT_CODE | ID_CODE))?                  #nodePredicateCode
    | ARCHETYPE_HRID (SYM_COMMA value=(STRING | PARAMETER | TERM_CODE | AT_CODE | ID_CODE))?                            #nodePredicateArchetypeHrid
    | PARAMETER                                                                                                         #nodePredicateParameter
    | objectPath operator=COMPARISON_OPERATOR pathPredicateOperand                                                      #nodePredicateComparison
    | objectPath MATCHES CONTAINED_REGEX                                                                                #nodePredicateMatches
    | left=nodePredicate AND right=nodePredicate                                                                        #nodePredicateAnd
    | left=nodePredicate OR right=nodePredicate                                                                         #nodePredicateOr
    ;

versionPredicate
    : LATEST_VERSION                                                                                                    #versionPredicateLatestVersion
    | ALL_VERSIONS                                                                                                      #versionPredicateAllVersions
    | standardPredicate                                                                                                 #versionPredicateStandardPredicate
    ;

pathPredicateOperand
    : primitive                                                                                                         #pathPredicateOperandPrimitive
    | objectPath                                                                                                        #pathPredicateOperandObjectPath
    | PARAMETER                                                                                                         #pathPredicateOperandParameter
    | ID_CODE                                                                                                           #pathPredicateOperandIdCode
    | AT_CODE                                                                                                           #pathPredicateOperandAtCode
    ;

objectPath
    : pathPart (SYM_SLASH pathPart)*
    ;
pathPart
    : id=IDENTIFIER pathPredicate?
    ;

likeOperand
    : STRING                                                                                                            #likeOperandString
    | PARAMETER                                                                                                         #likeOperandParameter
    ;
matchesOperand
    : SYM_LEFT_CURLY valueListItem (SYM_COMMA valueListItem)* SYM_RIGHT_CURLY                                           #matchesOperandValueList
    | terminologyFunction                                                                                               #matchesOperandTerminologyFunction
    | SYM_LEFT_CURLY URI SYM_RIGHT_CURLY                                                                                #matchesOperandUri
    ;

valueListItem
    : primitive                                                                                                         #valueListItemPrimitive
    | PARAMETER                                                                                                         #valueListItemParameter
    | terminologyFunction                                                                                               #valueListItemTerminologyFunction
    ;

primitive
    : STRING                                                                                                            #primitiveString
    | numericPrimitive                                                                                                  #primitiveNumericPrimitive
    | DATE                                                                                                              #primitiveDate
    | TIME                                                                                                              #primitiveTime
    | DATETIME                                                                                                          #primitiveDateTime
    | BOOLEAN                                                                                                           #primitiveBoolean
    | NULL                                                                                                              #primitiveNull
    ;

numericPrimitive
    : INTEGER                                                                                                           #numericPrimitiveInteger
    | REAL                                                                                                              #numericPrimitiveReal
    | SCI_INTEGER                                                                                                       #numericPrimitiveSciInteger
    | SCI_REAL                                                                                                          #numericPrimitiveSciReal
    | SYM_MINUS numericPrimitive                                                                                        #numericPrimitiveMinus
    ;

functionCall
    : terminologyFunction                                                                                               #functionCallTerminologyFunction
    | LENGTH SYM_LEFT_PAREN expression=terminal SYM_RIGHT_PAREN                                                         #functionCallLength
    | CONTAINS SYM_LEFT_PAREN expression=terminal SYM_COMMA substring=terminal SYM_RIGHT_PAREN                          #functionCallContains
    | POSITION SYM_LEFT_PAREN expression=terminal SYM_COMMA substring=terminal SYM_RIGHT_PAREN                          #functionCallPosition
    | SUBSTRING SYM_LEFT_PAREN expression=terminal SYM_COMMA position=INTEGER SYM_COMMA length=INTEGER SYM_RIGHT_PAREN  #functionCallSubstring
    | CONCAT SYM_LEFT_PAREN terminal (SYM_COMMA terminal)* SYM_RIGHT_PAREN                                              #functionCallConcat
    | CONCAT_WS SYM_LEFT_PAREN separator=STRING SYM_COMMA terminal (SYM_COMMA terminal)* SYM_RIGHT_PAREN                #functionCallConcatWs
    | ABS SYM_LEFT_PAREN terminal SYM_RIGHT_PAREN                                                                       #functionCallAbs
    | MOD SYM_LEFT_PAREN dividend=terminal SYM_COMMA divisor=terminal SYM_RIGHT_PAREN                                   #functionCallMod
    | CEIL SYM_LEFT_PAREN terminal SYM_RIGHT_PAREN                                                                      #functionCallCeil
    | FLOOR SYM_LEFT_PAREN terminal SYM_RIGHT_PAREN                                                                     #functionCallFloor
    | ROUND SYM_LEFT_PAREN terminal SYM_COMMA decimal=INTEGER SYM_RIGHT_PAREN                                           #functionCallRound
    | CURRENT_DATE SYM_LEFT_PAREN SYM_RIGHT_PAREN                                                                       #functionCallCurrentDate
    | CURRENT_TIME SYM_LEFT_PAREN SYM_RIGHT_PAREN                                                                       #functionCallCurrentTime
    | CURRENT_DATE_TIME SYM_LEFT_PAREN SYM_RIGHT_PAREN                                                                  #functionCallCurrentDateTime
    | NOW SYM_LEFT_PAREN SYM_RIGHT_PAREN                                                                                #functionCallNow
    | CURRENT_TIMEZONE SYM_LEFT_PAREN SYM_RIGHT_PAREN                                                                   #functionCallCurrentTimezone
    | name=IDENTIFIER SYM_LEFT_PAREN (terminal (SYM_COMMA terminal)*)? SYM_RIGHT_PAREN                                  #functionCallExtension
    ;

aggregateFunctionCall
    : COUNT SYM_LEFT_PAREN (DISTINCT? identifiedPath | SYM_ASTERISK) SYM_RIGHT_PAREN                                    #aggregateFunctionCallCount
    | MIN SYM_LEFT_PAREN identifiedPath SYM_RIGHT_PAREN                                                                 #aggregateFunctionCallMin
    | MAX SYM_LEFT_PAREN identifiedPath SYM_RIGHT_PAREN                                                                 #aggregateFunctionCallMax
    | SUM SYM_LEFT_PAREN identifiedPath SYM_RIGHT_PAREN                                                                 #aggregateFunctionCallSum
    | AVG SYM_LEFT_PAREN identifiedPath SYM_RIGHT_PAREN                                                                 #aggregateFunctionCallAvg
    ;

terminologyFunction
    : TERMINOLOGY SYM_LEFT_PAREN operation=STRING SYM_COMMA service_api=STRING SYM_COMMA params_uri=STRING SYM_RIGHT_PAREN
    ;
