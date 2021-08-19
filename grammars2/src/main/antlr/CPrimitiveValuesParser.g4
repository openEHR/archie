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

cString: ( stringValue | stringListValue | regexConstraint ) assumedStringValue? ;
regexConstraint: SLASH_REGEX | CARET_REGEX ;
assumedStringValue: ';' stringValue ;

// ADL2 term types: [ac3], [ac3; at5], [at5]
// NOTE: an assumed at-code (the ';' AT_CODE pattern) can only occur after an ac-code not after the single at-code
cTerminologyCode: '[' ( AC_CODE ( ';' AT_CODE )? | AT_CODE ) ']' ;

cBoolean: ( booleanValue | booleanListValue ) assumedBooleanValue? ;
assumedBooleanValue: ';' booleanValue ;


