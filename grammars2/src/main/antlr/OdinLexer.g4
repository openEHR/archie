//
// description: Antlr4 grammar for consuming Object Data Instance Notation (ODIN) text
//              for proper parsing later
// author:      Thomas Beale <thomas.beale@openehr.org>
// support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
// copyright:   Copyright (c) 2021- openEHR Foundation <http://www.openEHR.org>
// license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//

lexer grammar OdinLexer;
import PrimitiveValuesLexer, BaseLexer;


// ---------- lines and comments ----------
CMT_LINE   : '--' .*? EOL -> skip ;             // increment line count
EOL        : '\r'? '\n'   -> channel(HIDDEN) ;  // increment line count
WS         : [ \t\r]+     -> channel(HIDDEN) ;

// --------------------- URIs --------------------

// URI recogniser based on https://tools.ietf.org/html/rfc3986 and
// http://www.w3.org/Addressing/URL/5_URI_BNF.html

URI : URI_SCHEME ':' URI_HIER_PART ( '?' URI_QUERY )? ('#' URI_FRAGMENT)? ;

fragment URI_HIER_PART :
      ( '//' URI_AUTHORITY ) URI_PATH_ABEMPTY
    | URI_PATH_ABSOLUTE
    | URI_PATH_ROOTLESS
    | URI_PATH_EMPTY
    ;

fragment URI_SCHEME : ALPHA_CHAR ( ALPHA_CHAR | DIGIT | '+' | '-' | '.')* ;

fragment URI_AUTHORITY : ( URI_USERINFO '@' )? URI_HOST ( ':' URI_PORT )? ;
fragment URI_USERINFO: (URI_UNRESERVED | URI_PCT_ENCODED | URI_SUB_DELIMS | ':' )* ;
fragment URI_HOST : URI_IP_LITERAL | URI_IPV4_ADDRESS | URI_REG_NAME ; //TODO: ipv6
fragment URI_PORT: DIGIT* ;

fragment URI_IP_LITERAL   : '[' URI_IPV6_LITERAL ']'; //TODO, if needed: IPvFuture
fragment URI_IPV4_ADDRESS : URI_DEC_OCTET '.' URI_DEC_OCTET '.' URI_DEC_OCTET '.' URI_DEC_OCTET ;
fragment URI_IPV6_LITERAL : HEX_QUAD (':' HEX_QUAD )* '::' HEX_QUAD (':' HEX_QUAD )* ;

fragment URI_DEC_OCTET  : DIGIT | [1-9] DIGIT | '1' DIGIT DIGIT | '2' [0-4] DIGIT | '25' [0-5] ;
fragment URI_REG_NAME: (URI_UNRESERVED | URI_PCT_ENCODED | URI_SUB_DELIMS)* ;
fragment HEX_QUAD : HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT ;

fragment URI_PATH_ABEMPTY: ('/' URI_SEGMENT )* ;
fragment URI_PATH_ABSOLUTE: '/' ( URI_SEGMENT_NZ ( '/' URI_SEGMENT )* )? ;
fragment URI_PATH_NOSCHEME: URI_SEGMENT_NZ_NC ( '/' URI_SEGMENT )* ;
fragment URI_PATH_ROOTLESS: URI_SEGMENT_NZ ( '/' URI_SEGMENT )* ;
fragment URI_PATH_EMPTY: ;

fragment URI_SEGMENT: URI_PCHAR* ;
fragment URI_SEGMENT_NZ: URI_PCHAR+ ;
fragment URI_SEGMENT_NZ_NC: ( URI_UNRESERVED | URI_PCT_ENCODED | URI_SUB_DELIMS | '@' )+ ; //non-zero-length segment without any colon ":"

fragment URI_PCHAR: URI_UNRESERVED | URI_PCT_ENCODED | URI_SUB_DELIMS | ':' | '@' ;

//fragment URI_PATH   : '/' | ( '/' URI_XPALPHA+ )+ ('/')?;
fragment URI_QUERY : (URI_PCHAR | '/' | '?')* ;
fragment URI_FRAGMENT  : (URI_PCHAR | '/' | '?')* ;

fragment URI_PCT_ENCODED : '%' HEX_DIGIT HEX_DIGIT ;

fragment URI_UNRESERVED: ALPHA_CHAR | DIGIT | '-' | '.' | '_' | '~' ;
fragment URI_RESERVED: URI_GEN_DELIMS | URI_SUB_DELIMS ;
fragment URI_GEN_DELIMS: [:/?#[\]@] ;
fragment URI_SUB_DELIMS: [!$&'()*+,;=] ;

// -------------------- symbols for lists ------------------------
SYM_LIST_CONTINUE: '...' ;

// ------------------ symbols  ----------------------
SYM_EQ : '=' ;

// -------------------- ODIN paths ------------------------
ODIN_PATH : ODIN_PATH_PREDICATE? ODIN_PATH_SEGMENT+ ;
fragment ODIN_PATH_SEGMENT: '/' ALPHA_LC_ID ODIN_PATH_PREDICATE? ;
fragment ODIN_PATH_PREDICATE: '[' .+? ']' ;
