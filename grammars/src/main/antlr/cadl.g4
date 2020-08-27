//
// description: Antlr4 grammar for cADL non-primitves sub-syntax of Archetype Definition Language (ADL2)
// author:      Thomas Beale <thomas.beale@openehr.org>
// support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
// copyright:   Copyright (c) 2015 openEHR Foundation
// license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//

grammar cadl;
import adl_rules, odin, adl_keywords;

//
//  ======================= Top-level Objects ========================
//

c_complex_object: type_id '[' ( ROOT_ID_CODE | ID_CODE ) ']' c_occurrences? ( SYM_MATCHES '{' c_attribute_def+ '}' )? ;

// ======================== Components =======================

c_objects: c_non_primitive_object_ordered+ | c_primitive_object ;

sibling_order: ( SYM_AFTER | SYM_BEFORE ) '[' ID_CODE ']' ;

c_non_primitive_object_ordered: sibling_order? c_non_primitive_object ;

c_non_primitive_object:
      c_complex_object
    | c_archetype_root
    | c_complex_object_proxy
    | archetype_slot
    ;

c_archetype_root: SYM_USE_ARCHETYPE type_id '[' ID_CODE (SYM_COMMA archetype_ref)? ']' c_occurrences? ( SYM_MATCHES '{' c_attribute_def+ '}' )? ;

c_complex_object_proxy: SYM_USE_NODE type_id '[' ID_CODE ']' c_occurrences? adl_path ;

archetype_slot:
      c_archetype_slot_head SYM_MATCHES '{' c_includes? c_excludes? '}'
    | c_archetype_slot_head
    ;

c_archetype_slot_head: c_archetype_slot_id c_occurrences? ;

c_archetype_slot_id: SYM_ALLOW_ARCHETYPE type_id '[' ID_CODE ']' SYM_CLOSED? ;

c_attribute_def:
      c_attribute
    | c_attribute_tuple
    | default_value
    ;

c_attribute: (ADL_PATH | attribute_id) c_existence? c_cardinality? ( SYM_MATCHES ('{' c_objects '}' | CONTAINED_REGEXP) )? ;

c_attribute_tuple : '[' attribute_id ( ',' attribute_id )* ']' SYM_MATCHES '{' c_object_tuple ( ',' c_object_tuple )* '}' ;

default_value: SYM_DEFAULT SYM_EQ ('<')? odin_text ('>')?;

c_object_tuple : '[' c_object_tuple_items ']' ;

c_object_tuple_items : c_object_tuple_item ( ',' c_object_tuple_item )* ;

c_object_tuple_item: '{' c_primitive_object '}' | CONTAINED_REGEXP;

c_includes : SYM_INCLUDE assertion+ ;
c_excludes : SYM_EXCLUDE assertion+ ;

c_existence: SYM_EXISTENCE SYM_MATCHES '{' existence '}' ;
existence: INTEGER | INTEGER '..' INTEGER ;

c_cardinality    : SYM_CARDINALITY SYM_MATCHES '{' cardinality '}' ;
cardinality      : multiplicity ( multiplicity_mod multiplicity_mod? )? ; // max of two
ordering_mod     : ';' ( SYM_ORDERED | SYM_UNORDERED ) ;
unique_mod       : ';' SYM_UNIQUE ;
multiplicity_mod : ordering_mod | unique_mod ;

c_occurrences : SYM_OCCURRENCES SYM_MATCHES '{' multiplicity '}' ;
multiplicity  : INTEGER | '*' | INTEGER SYM_INTERVAL_SEP ( INTEGER | '*' ) ;
