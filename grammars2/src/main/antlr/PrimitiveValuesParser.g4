//
// Grammar defining compact terminal value types, including atoms, lists and intervals that
// may be used in ODIN but also other formalisms such as JSON (schema driven) and YAML.
// author:      Pieter Bos <pieter.bos@nedap.com>, Thomas Beale <thomas.beale@openEHR.org>
// support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
// copyright:   Copyright (c) 2018- openEHR Foundation <http://www.openEHR.org>
//

parser grammar PrimitiveValuesParser;
options { tokenVocab=PrimitiveValuesLexer; }

primitiveObject :
      primitiveValue
    | primitiveListValue
    | primitiveIntervalValue
    ;

primitiveValue :
      stringValue
    | integerValue
    | realValue
    | booleanValue
    | characterValue
    | termCodeValue
    | dateValue
    | timeValue
    | dateTimeValue
    | durationValue
    | uriValue
    ;

primitiveListValue :
      stringListValue
    | integerListValue
    | realListValue
    | booleanListValue
    | characterListValue
    | termCodeListValue
    | dateListValue
    | timeListValue
    | dateTimeListValue
    | durationListValue
    ;

primitiveIntervalValue :
      integerIntervalValue
    | realIntervalValue
    | dateIntervalValue
    | timeIntervalValue
    | dateTimeIntervalValue
    | durationIntervalValue
    ;

stringValue : STRING ;
stringListValue : stringValue ( ( ',' stringValue )+ | ',' SYM_LIST_CONTINUE ) ;

integerValue : ( SYM_PLUS | SYM_MINUS )? INTEGER ;
integerListValue : integerValue ( ( ',' integerValue )+ | ',' SYM_LIST_CONTINUE ) ;
integerIntervalValue :
      '|' SYM_GT? integerValue '..' SYM_LT? integerValue '|'
    | '|' relop? integerValue '|'
    | '|' integerValue SYM_PLUS_OR_MINUS integerValue '|'
    ;
integerIntervalListValue : integerIntervalValue ( ( ',' integerIntervalValue )+ | ',' SYM_LIST_CONTINUE ) ;

realValue : ( SYM_PLUS | SYM_MINUS )? REAL ;
realListValue : realValue ( ( ',' realValue )+ | ',' SYM_LIST_CONTINUE ) ;
realIntervalValue :
      '|' SYM_GT? realValue '..' SYM_LT? realValue '|'
    | '|' relop? realValue '|'
    | '|' realValue SYM_PLUS_OR_MINUS realValue '|'
    ;
realIntervalListValue : realIntervalValue ( ( ',' realIntervalValue )+ | ',' SYM_LIST_CONTINUE ) ;

booleanValue : SYM_TRUE | SYM_FALSE ;
booleanListValue : booleanValue ( ( ',' booleanValue )+ | ',' SYM_LIST_CONTINUE ) ;

characterValue : CHARACTER ;
characterListValue : characterValue ( ( ',' characterValue )+ | ',' SYM_LIST_CONTINUE ) ;

dateValue : ISO8601_DATE ;
dateListValue : dateValue ( ( ',' dateValue )+ | ',' SYM_LIST_CONTINUE ) ;
dateIntervalValue :
      '|' SYM_GT? dateValue '..' SYM_LT? dateValue '|'
    | '|' relop? dateValue '|'
    | '|' dateValue SYM_PLUS_OR_MINUS durationValue '|'
    ;
dateIntervalListValue : dateIntervalValue ( ( ',' dateIntervalValue )+ | ',' SYM_LIST_CONTINUE ) ;

timeValue : ISO8601_TIME ;
timeListValue : timeValue ( ( ',' timeValue )+ | ',' SYM_LIST_CONTINUE ) ;
timeIntervalValue :
      '|' SYM_GT? timeValue '..' SYM_LT? timeValue '|'
    | '|' relop? timeValue '|'
    | '|' timeValue SYM_PLUS_OR_MINUS durationValue '|'
    ;
timeIntervalListValue : timeIntervalValue ( ( ',' timeIntervalValue )+ | ',' SYM_LIST_CONTINUE ) ;

dateTimeValue : ISO8601_DATE_TIME ;
dateTimeListValue : dateTimeValue ( ( ',' dateTimeValue )+ | ',' SYM_LIST_CONTINUE ) ;
dateTimeIntervalValue :
      '|' SYM_GT? dateTimeValue '..' SYM_LT? dateTimeValue '|'
    | '|' relop? dateTimeValue '|'
    | '|' dateTimeValue SYM_PLUS_OR_MINUS durationValue '|'
    ;
dateTimeIntervalListValue : dateTimeIntervalValue ( ( ',' dateTimeIntervalValue )+ | ',' SYM_LIST_CONTINUE ) ;

durationValue : ( SYM_PLUS | SYM_MINUS )? ISO8601_DURATION ;
durationListValue : durationValue ( ( ',' durationValue )+ | ',' SYM_LIST_CONTINUE ) ;
durationIntervalValue :
      '|' SYM_GT? durationValue '..' SYM_LT? durationValue '|'
    | '|' relop? durationValue '|'
    | '|' durationValue SYM_PLUS_OR_MINUS durationValue '|'
    ;
durationIntervalListValue : durationIntervalValue ( ( ',' durationIntervalValue )+ | ',' SYM_LIST_CONTINUE ) ;

termCodeValue : TERM_CODE_REF ;
termCodeListValue : termCodeValue ( ( ',' termCodeValue )+ | ',' SYM_LIST_CONTINUE ) ;

uriValue : URI ;


relop : SYM_LE | SYM_GE | SYM_GT | SYM_LT ;

