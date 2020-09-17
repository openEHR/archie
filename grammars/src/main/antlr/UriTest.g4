grammar UriTest;

uri: URI;

// URIs - simple recogniser based on https://tools.ietf.org/html/rfc3986 and
// http://www.w3.org/Addressing/URL/5_URI_BNF.html
URI : // URN |
    URI_SCHEME ':' URI_HIER_PART ( '?' URI_QUERY )? ('#' URI_FRAGMENT)? ;


// URN. TODO: check if still necessary
//fragment URN: ASSIGNED_NAME RQ_COMPONENTS ('#' F_COMPONENT)?;
//fragment ASSIGNED_NAME: 'urn:' NID ':' NSS;
//fragment NID: ALPHANUM_CHAR (ALPHANUM_CHAR | '_')* ALPHANUM_CHAR;
//fragment NSS: URI_XPALPHA (URI_XPALPHA | '/')*;
//fragment RQ_COMPONENTS: ('?+' R_COMPONENT)? ('?=' Q_COMPONENT)?;
//fragment R_COMPONENT: URI_XPALPHA (URI_XPALPHA | '/' | '?')*;
//fragment Q_COMPONENT: URI_XPALPHA (URI_XPALPHA | '/' | '?')*;
//fragment F_COMPONENT: URI_XALPHA+ ( '+' URI_XALPHA+ )* ;
//fragment URI_XPALPHA : URI_XALPHA | '+' ;
//fragment URI_XALPHA : ALPHANUM_CHAR | URI_SAFE | URI_EXTRA | URI_PCT_ENCODED ;
//fragment URI_SAFE   : [$@\\._-] ;
//fragment URI_EXTRA  : [!*"'()] ;

fragment URI_HIER_PART : ( '//' URI_AUTHORITY ) URI_PATH_ABEMPTY |
    URI_PATH_ABSOLUTE |
    URI_PATH_NOSCHEME |
    URI_PATH_EMPTY;

fragment URI_SCHEME : ALPHA_CHAR ( ALPHA_CHAR | DIGIT | '+' | '-' | '.')* ;

fragment URI_AUTHORITY : ( URI_USERINFO '@' )? URI_HOST ( ':' URI_PORT )? ;
fragment URI_USERINFO: (URI_UNRESERVED | URI_PCT_ENCODED | URI_SUB_DELIMS | ':' )* ;
fragment URI_HOST : URI_IP_LITERAL | URI_IPV4_ADDRESS | URI_REG_NAME ; //TODO: ipv6
fragment URI_PORT: DIGIT*;

fragment URI_IP_LITERAL   : '[' URI_IPV6_LITERAL ']'; //TODO, if needed: IPvFuture
fragment URI_IPV4_ADDRESS : URI_DEC_OCTET '.' URI_DEC_OCTET '.' URI_DEC_OCTET '.' URI_DEC_OCTET ;
fragment URI_IPV6_LITERAL : HEX_QUAD (SYM_COLON HEX_QUAD )* SYM_COLON SYM_COLON HEX_QUAD (SYM_COLON HEX_QUAD )* ;

fragment URI_DEC_OCTET  : DIGIT | [1-9] DIGIT | '1' DIGIT DIGIT | '2' [0-4] DIGIT | '25' [0-5];
fragment URI_REG_NAME: (URI_UNRESERVED | URI_PCT_ENCODED | URI_SUB_DELIMS)*;
fragment HEX_QUAD : HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT ;

fragment URI_PATH_ABEMPTY: ('/' URI_SEGMENT ) *;
fragment URI_PATH_ABSOLUTE: '/' ( URI_SEGMENT_NZ ( '/' URI_SEGMENT )* )?;
fragment URI_PATH_NOSCHEME: URI_SEGMENT_NZ_NC ( '/' URI_SEGMENT )*;
fragment URI_PATH_ROOTLESS: URI_SEGMENT_NZ ( '/' URI_SEGMENT )*;
fragment URI_PATH_EMPTY: ;

fragment URI_SEGMENT: URI_PCHAR*;
fragment URI_SEGMENT_NZ: URI_PCHAR+;
fragment URI_SEGMENT_NZ_NC: ( URI_UNRESERVED | URI_PCT_ENCODED | URI_SUB_DELIMS | '@' )+; //non-zero-length segment without any colon ":"

fragment URI_PCHAR: URI_UNRESERVED | URI_PCT_ENCODED | URI_SUB_DELIMS | ':' | '@';

//fragment URI_PATH   : '/' | ( '/' URI_XPALPHA+ )+ ('/')?;
fragment URI_QUERY : (URI_PCHAR | '/' | '?')*;
fragment URI_FRAGMENT  : (URI_PCHAR | '/' | '?')*;

fragment URI_PCT_ENCODED : '%' HEX_DIGIT HEX_DIGIT ;

fragment URI_UNRESERVED: ALPHA_CHAR | DIGIT | '-' | '.' | '_' | '~';
fragment URI_RESERVED: URI_GEN_DELIMS | URI_SUB_DELIMS;
fragment URI_GEN_DELIMS: ':' | '/' | '?' | '#' | '[' | ']' | '@'; //TODO: migrate to [/?#...] notation
fragment URI_SUB_DELIMS: '!' | '$' | '&' | '\'' | '(' | ')'
                         | '*' | '+' | ',' | ';' | '=';

fragment ALPHA_CHAR  : [a-zA-Z] ;
fragment UTF8CHAR    : '\\u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT ;

fragment DIGIT     : [0-9] ;
fragment HEX_DIGIT : [0-9a-fA-F] ;
SYM_COLON: ':';