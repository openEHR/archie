//
//  description: Antlr4 lexer rules for Archetype Definition Language (ADL2), based on two-pass
//               approach used in associated AdlParser.
//  author:      Thomas Beale <thomas.beale@openehr.org>
//  contributors:Pieter Bos <pieter.bos@nedap.com>
//  support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
//  copyright:   Copyright (c) 2015- openEHR Foundation <http://www.openEHR.org>
//  license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//

lexer grammar AdlLexer;
import OpenehrPatterns;

// ---------- lines and comments ----------

H_CMT_LINE : '--------' '-'*? EOL  ;            // long comment line for splitting ADL2 template overlays
CMT_LINE   : '--' .*? EOL -> skip ;             // increment line count
EOL        : '\r'? '\n'   -> channel(HIDDEN) ;  // throw out EOLs in default mode
WS         : [ \t\r]+     -> channel(HIDDEN) ;

// ----------------- ADL keywords ---------------

SYM_ARCHETYPE            : [Aa][Rr][Cc][Hh][Ee][Tt][Yy][Pp][Ee] ;
SYM_TEMPLATE_OVERLAY     : [Tt][Ee][Mm][Pp][Ll][Aa][Tt][Ee]'_'[Oo][Vv][Ee][Rr][Ll][Aa][Yy] ;
SYM_TEMPLATE             : [Tt][Ee][Mm][Pp][Ll][Aa][Tt][Ee] ;
SYM_OPERATIONAL_TEMPLATE : [Oo][Pp][Ee][Rr][Aa][Tt][Ii][Oo][Nn][Aa][Ll]'_'[Tt][Ee][Mm][Pp][Ll][Aa][Tt][Ee] ;

SPECIALIZE_SECTION      : EOL SYM_SPECIALIZE WS? EOL ;
fragment SYM_SPECIALIZE : [Ss][Pp][Ee][Cc][Ii][Aa][Ll][Ii][SsZz][Ee] ;

LANGUAGE_SECTION      : EOL SYM_LANGUAGE WS? EOL -> mode (LANGUAGE) ;
fragment SYM_LANGUAGE : [Ll][Aa][Nn][Gg][Uu][Aa][Gg][Ee] ;

// have to look for definition section in default mode for template overlays
DEFINITION_SECTION      : EOL SYM_DEFINITION WS? EOL -> mode (DEFINITION) ;
fragment SYM_DEFINITION : [Dd][Ee][Ff][Ii][Nn][Ii][Tt][Ii][Oo][Nn] ;

// ---------------- meta-data keywords and symbols ---------------
SYM_EQ         : '=' ;
ALPHANUM_ID : [a-zA-Z0-9][a-zA-Z0-9_]* ;

//
// ---------------- modal lexing of main sections ----------------
//

// language section: look for 'description' section, otherwise grab complete lines
mode LANGUAGE;
DESCRIPTION_SECTION      : EOL SYM_DESCRIPTION WS? EOL -> mode (DESCRIPTION) ;
fragment SYM_DESCRIPTION : [Dd][Ee][Ss][Cc][Rr][Ii][Pp][Tt][Ii][Oo][Nn] ;
ODIN_LINE                : NON_EOL* EOL ;
fragment NON_EOL         : ~'\n' ;

// description section: look for 'definition' section, otherwise grab complete lines
mode DESCRIPTION ;
DEFINITION_SECTION2: EOL SYM_DEFINITION WS? EOL -> mode (DEFINITION), type(DEFINITION_SECTION);
ODIN_LINE_DESC     : NON_EOL* EOL -> type (ODIN_LINE);

// definition section: look for 'rules' and/or 'terminology' sections,
// otherwise grab complete lines
mode DEFINITION ;
RULES_SECTION           : EOL SYM_RULES WS? EOL -> mode (RULES);
fragment SYM_RULES      : [Rr][Uu][Ll][Ee][Ss] ;
TERMINOLOGY_SECTION     : EOL SYM_TERMINOLOGY WS? EOL -> mode (TERMINOLOGY);
fragment SYM_TERMINOLOGY: [Tt][Ee][Rr][Mm][Ii][Nn][Oo][Ll][Oo][Gg][Yy] ;
CADL_LINE               : NON_EOL* EOL ;

// rules section: look for 'terminology' section, otherwise grab complete lines
mode RULES ;
TERMINOLOGY_SECTION2 : EOL SYM_TERMINOLOGY WS? EOL -> mode (TERMINOLOGY), type (TERMINOLOGY_SECTION);
EL_LINE              : NON_EOL* EOL ;

// terminology section: look for 'annotations' and/or 'component_terminologies' sections or end,
// otherwise grab complete lines; allow for final line with no EOL
mode TERMINOLOGY;
ANNOTATIONS_SECTION             : EOL SYM_ANNOTATIONS WS? EOL -> mode (ANNOTATIONS);
fragment SYM_ANNOTATIONS        : [Aa][Nn][Nn][Oo][Tt][Aa][Tt][Ii][Oo][Nn][Ss] ;
COMPONENT_TERMINOLOGIES_SECTION : EOL SYM_COMPONENT_TERMINOLOGIES WS? EOL+ -> mode (COMPONENT_TERMINOLOGIES);
fragment SYM_COMPONENT_TERMINOLOGIES : [Cc][Oo][Mm][Pp][Oo][Nn][Ee][Nn][Tt]'_'[Tt][Ee][Rr][Mm][Ii][Nn][Oo][Ll][Oo][Gg][Ii][Ee][Ss] ;
TEMPLATE_DIVIDER                : H_CMT_LINE -> channel(HIDDEN), mode (DEFAULT_MODE) ;
ODIN_LINE_TERM                  : ( NON_EOL* EOL | NON_EOL+ ) -> type (ODIN_LINE) ;

// annotations section: look for 'component_terminologies' section or end,
// otherwise grab complete lines; allow for final line with no EOL
mode ANNOTATIONS;
COMPONENT_TERMINOLOGIES_SECTION2 : EOL SYM_COMPONENT_TERMINOLOGIES WS? EOL+ -> mode (COMPONENT_TERMINOLOGIES), type (COMPONENT_TERMINOLOGIES_SECTION);
TEMPLATE_DIVIDER2                : H_CMT_LINE -> channel(HIDDEN), mode (DEFAULT_MODE) ;
ODIN_LINE_ANNOT                  : ( NON_EOL* EOL | NON_EOL+ ) -> type (ODIN_LINE) ;

// component_terminologies section
// grab complete lines; allow for final line with no EOL
mode COMPONENT_TERMINOLOGIES;
ODIN_LINE_CT : ( NON_EOL* EOL | NON_EOL+ ) -> type (ODIN_LINE) ;

