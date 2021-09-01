//
// description: Antlr4 grammar for cADL primitives, used within Cadl grammar, but also by
//              other languages that allow constraints on primitive objects.
// author:      Thomas Beale <thomas.beale@openehr.org>
// contributors:Pieter Bos <pieter.bos@nedap.com>
// support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
// copyright:   Copyright (c) 2015 openEHR Foundation <http://www.openEHR.org>
// license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//

parser grammar CPrimitiveValuesParser;
options { tokenVocab=CPrimitiveValuesLexer; }
import PrimitiveValuesParser;

//
// Here we match C_STRING regexes separately because the lexer match pattern
// includes the '{}', which is required to avoid random matching of e.g. paths
// and other patterns containing '/' character as regexes.
// TODO: this can be fixed by only allowing '^^' delimited regexes or some other
// method that makes regexes more unique.
//
cInlinePrimitiveObjectDef:
      '{' cInlinePrimitiveObject '}'
    | cStringRegex
    ;

// TODO: cStringRegex does not allow any assumed value; if we want this
// it will either need modal parsing on regexes, which still produces
// a painful lexical object that has to be pulled apart later, or (better)
// rewrite slash regexes to carat regexes.
cStringRegex: ( C_STRING_SLASH_REGEX | C_STRING_CARET_REGEX ) ;

cInlinePrimitiveObject:
      cInteger
    | cReal
    | cDate
    | cTime
    | cDateTime
    | cDuration
    | cString
    | cTerminologyCode
    | cBoolean
    ;

// ------------ Primitive type constraints -------------

cInteger: ( integerValue | integerListValue | integerIntervalValue | integerIntervalListValue ) assumedIntegerValue? ;
assumedIntegerValue: ';' integerValue ;

cReal: ( realValue | realListValue | realIntervalValue | realIntervalListValue ) assumedRealValue? ;
assumedRealValue: ';' realValue ;

cDateTime: ( DATE_TIME_CONSTRAINT_PATTERN | dateTimeValue | dateTimeListValue | dateTimeIntervalValue | dateTimeIntervalListValue ) assumedDateTimeValue? ;
assumedDateTimeValue: ';' dateTimeValue ;

cDate: ( DATE_CONSTRAINT_PATTERN | dateValue | dateListValue | dateIntervalValue | dateIntervalListValue ) assumedDateValue? ;
assumedDateValue: ';' dateValue ;

cTime: ( TIME_CONSTRAINT_PATTERN | timeValue | timeListValue | timeIntervalValue | timeIntervalListValue ) assumedTimeValue? ;
assumedTimeValue: ';' timeValue ;

cDuration: ( DURATION_CONSTRAINT_PATTERN ( '/' ( durationIntervalValue | durationValue ))?
    | durationValue | durationListValue | durationIntervalValue | durationIntervalListValue ) assumedDurationValue?
    ;
assumedDurationValue: ';' durationValue ;

// The following could be used if only caret-delimited regexes were allowed
//cString: ( stringValue | stringListValue | CARET_REGEX ) assumedStringValue? ;
cString: ( stringValue | stringListValue ) assumedStringValue? ;
assumedStringValue: ';' stringValue ;

// ADL2 term types: [ac3], [ac3; at5], [at5]
// NOTE: an assumed at-code (the ';' AT_CODE pattern) can only occur after an ac-code not after the single at-code
cTerminologyCode: '[' ( AC_CODE ( ';' AT_CODE )? | AT_CODE ) ']' ;

cBoolean: ( booleanValue | booleanListValue ) assumedBooleanValue? ;
assumedBooleanValue: ';' booleanValue ;


