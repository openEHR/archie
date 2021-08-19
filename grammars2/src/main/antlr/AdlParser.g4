//
//  description: Antlr4 grammar for Archetype Definition Language (ADL2). This is a line-oriented
//               grammar designed to extract the verious sub-parts of an archetype and parse them
//               in a second pass, using the appropriate sub-language parser, ie. Cadl, EL, ODIN etc.
//  author:      Thomas Beale <thomas.beale@openehr.org>
//  contributors:Pieter Bos <pieter.bos@nedap.com>
//  support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
//  copyright:   Copyright (c) 2015- openEHR Foundation <http://www.openEHR.org>
//  license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//

//
// This grammar is separated to allow modes in the lexical part.
// Combined grammars can't have modes.
//
parser grammar AdlParser;
options { tokenVocab=AdlLexer; }

//
//  ======================= Top-level Objects ========================
//

adlObject: ( authoredArchetype | template | templateOverlay | operationalTemplate ) EOF ;

//
// ------------------ Archetypes -------------------
//
authoredArchetype:
    SYM_ARCHETYPE header
    specializeSection?
    languageSection
    descriptionSection
    definitionSection
    rulesSection?
    terminologySection
    annotationsSection?
    ;

//
// --------- Templates, including overlays ----------
//
template:
    SYM_TEMPLATE header
    specializeSection
    languageSection
    descriptionSection
    definitionSection
    rulesSection?
    terminologySection
    annotationsSection?
    templateOverlay*
    ;

templateOverlay:
    SYM_TEMPLATE_OVERLAY header
    specializeSection
    definitionSection
    terminologySection
    ;

//
// ------------- Operational Templates --------------
//
operationalTemplate:
    SYM_OPERATIONAL_TEMPLATE header
    languageSection
    descriptionSection
    definitionSection
    rulesSection?
    terminologySection
    annotationsSection?
    componentTerminologiesSection?
    ;

//
// ------------------- sub-parts --------------------
//

// Header: meta-data items in parentheses followed by archetype ID
header: metaData? ARCHETYPE_HRID ;

metaData: '(' metaDataItem ( ';' metaDataItem )* ')' ;

metaDataItem:
      metaDataValueItem
    | metaDataFlag
    ;

metaDataValueItem : ALPHANUM_ID '=' metaDataItemValue ;
metaDataFlag : ALPHANUM_ID ;

metaDataItemValue : ARCHETYPE_HRID | GUID | VERSION_ID | ALPHANUM_ID ;

// Specialise section
specializeSection : SPECIALIZE_SECTION (ARCHETYPE_REF|ARCHETYPE_HRID) ;

//
// Archetype content sections follow the pattern
//   SectionId Lines*
// The lines (i.e. text block) of each section is passed to the appropriate kind of
// specific parser.
//
languageSection    : LANGUAGE_SECTION ODIN_LINE+ ;
descriptionSection : DESCRIPTION_SECTION ODIN_LINE+ ;
definitionSection  : DEFINITION_SECTION CADL_LINE+ ;
rulesSection       : RULES_SECTION EL_LINE+ ;
terminologySection : TERMINOLOGY_SECTION ODIN_LINE+ ;
annotationsSection : ANNOTATIONS_SECTION ODIN_LINE+ ;
componentTerminologiesSection: COMPONENT_TERMINOLOGIES_SECTION ODIN_LINE+ ;

