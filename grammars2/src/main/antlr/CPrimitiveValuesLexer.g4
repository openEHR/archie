//
// description: Antlr4 grammar for cADL primitives, used within Cadl grammar, but also by
//              other languages that allow constraints on primitive objects. This has to include
//              other relevant Lexer grammars in the correct order, in order to generate a
//              correct total tokens file for use by the parser grammar.
// author:      Thomas Beale <thomas.beale@openehr.org>
// contributors:Pieter Bos <pieter.bos@nedap.com>
// support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
// copyright:   Copyright (c) 2015 openEHR Foundation <http://www.openEHR.org>
// license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//

lexer grammar CPrimitiveValuesLexer;
import OpenehrPatterns, PrimitiveValuesLexer;

// ---------- ISO8601-based date/time/duration constraint patterns

DATE_CONSTRAINT_PATTERN      : YEAR_PATTERN '-' MONTH_PATTERN '-' DAY_PATTERN ;
TIME_CONSTRAINT_PATTERN      : HOUR_PATTERN ':' MINUTE_PATTERN ':' SECOND_PATTERN ;
DATE_TIME_CONSTRAINT_PATTERN : DATE_CONSTRAINT_PATTERN 'T' TIME_CONSTRAINT_PATTERN ;
DURATION_CONSTRAINT_PATTERN  : 'P' [yY]?[mM]?[Ww]?[dD]? ( 'T' [hH]?[mM]?[sS]? )? ;

fragment YEAR_PATTERN   : 'yyyy' | 'YYYY' | 'yyy' | 'YYY' ;
fragment MONTH_PATTERN  : 'mm' | 'MM' | '??' | 'XX' | 'xx' ;
fragment DAY_PATTERN    : 'dd' | 'DD' | '??' | 'XX' | 'xx'  ;
fragment HOUR_PATTERN   : 'hh' | 'HH' | '??' | 'XX' | 'xx'  ;
fragment MINUTE_PATTERN : 'mm' | 'MM' | '??' | 'XX' | 'xx'  ;
fragment SECOND_PATTERN : 'ss' | 'SS' | '??' | 'XX' | 'xx'  ;


// ---------- C_STRING Regex matcher ------------
// Matches '/' or '^' delimited regex inside '{}' Cadl constraint delims
// logical form - REGEX: '/' ( '\\/' | ~'/' )+ '/' | '^' ( '\\^' | ~'^' )+ '^';

C_STRING_SLASH_REGEX: '{/' SLASH_REGEX_CHAR+ '/}';
fragment SLASH_REGEX_CHAR: REGEX_ESCAPE_SEQ | '\\/' | ~[/\n\r] ;

C_STRING_CARET_REGEX: '{^' CARET_REGEX_CHAR+ '^}' ;
fragment CARET_REGEX_CHAR: REGEX_ESCAPE_SEQ | '\\^' | ~[^\n\r] ;

fragment REGEX_ESCAPE_SEQ: '\\' ['"?abfnrtv\\] ;

// -------------------- Symbols ------------------------

SYM_SLASH: '/' ;
