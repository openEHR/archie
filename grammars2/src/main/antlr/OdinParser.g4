//
// description: Antlr4 grammar for consuming Object Data Instance Notation (ODIN) text
//              for proper parsing later
// author:      Thomas Beale <thomas.beale@openehr.org>
// support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
// copyright:   Copyright (c) 2021- openEHR Foundation <http://www.openEHR.org>
// license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//

parser grammar OdinParser;
options { tokenVocab=OdinLexer; }
import PrimitiveValuesParser;

//
//  ======================= Top-level Objects ========================
//

odinObject : ( odinAttrVal+ | odinObjectValueBlock ) EOF? ;

// ----------------- complex objects ------------------

odinAttrVal : odinAttrName '=' odinObjectBlock ';'? ;

odinAttrName : UC_ID | LC_ID ;

odinObjectBlock :
      odinObjectValueBlock
    | odinObjectReferenceBlock
    ;

// TODO: naked URIs are allowed only in this context; future versions of ODIN will
// only allow them as quoted Strings
odinObjectValueBlock : rmTypeSpec? '<' ( primitiveObject | odinAttrVal+ | odinKeyedObject+ | ODIN_URI )? '>' ;
rmTypeSpec : '(' rmTypeId ')' ;

odinKeyedObject : odinKeySpec '=' odinObjectBlock ;
odinKeySpec: '[' primitiveValue ']' ;

// ----------------- references ------------------

odinObjectReferenceBlock : '<' odinPathList '>' ;

odinPathList : odinPath ( ',' SYM_LIST_CONTINUE | ( ',' odinPath )+ )? ;

odinPath : odinKeySpec? odinPathSegment+ ;
odinPathSegment: '/' LC_ID odinKeySpec? ;

// ---------------- model identifiers --------------
rmTypeId : UC_ID ( '<' rmTypeId ( ',' rmTypeId )* '>' )? ;
