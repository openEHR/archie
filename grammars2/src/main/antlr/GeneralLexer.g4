//
//  General purpose patterns used in all openEHR parser and lexer tools
//  author:      Thomas Beale <thomas.beale@openEHR.org>
//  support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
//  copyright:   Copyright (c) 2021- openEHR Foundation <http://www.openEHR.org>
//

lexer grammar GeneralLexer;
import BaseLexer;

// ------------------- human-readable identifiers ----------------------

UC_ID    :  ALPHA_UCHAR WORD_CHAR* ;
LC_ID :  '_'? ALPHA_LCHAR WORD_CHAR* ;
