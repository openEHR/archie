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
    rmOverlaySection?
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
    rmOverlaySection?
    terminologySection
    annotationsSection?
    templateOverlay*
    ;

templateOverlay:
    SYM_TEMPLATE_OVERLAY header
    specializeSection
    definitionSection
    rmOverlaySection?
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
    rmOverlaySection?
    terminologySection
    annotationsSection?
    componentTerminologiesSection?
    ;

//
// ------------------- header section --------------------
//

// Header: meta-data items in parentheses followed by archetype ID
header: metaData? ARCHETYPE_HRID ;

metaData: METADATA_START metaDataItem ( METADATA_SEP metaDataItem )* METADATA_END ;

metaDataItem:
      metaDataValueItem
    | metaDataFlag
    ;

metaDataValueItem : ALPHANUM_ID '=' metaDataItemValue ;
metaDataFlag : ALPHANUM_ID ;

metaDataItemValue : ARCHETYPE_HRID | GUID | VERSION_ID | ALPHANUM_ID | OID ;

// ------------------- specialise section --------------------
specializeSection : SPECIALIZE_HEADER ARCHETYPE_REF ;

//
// Archetype content sections follow the pattern
//   SectionId Lines*
// The lines (i.e. text block) of each section is passed to the appropriate kind of
// specific parser.
//
languageSection    : LANGUAGE_HEADER odinText ;
descriptionSection : DESCRIPTION_HEADER odinText ;
definitionSection  : DEFINITION_HEADER cadlText ;
rulesSection       : RULES_HEADER elText ;
rmOverlaySection   : RM_OVERLAY_HEADER odinText ;
terminologySection : TERMINOLOGY_HEADER odinText ;
annotationsSection : ANNOTATIONS_HEADER odinText ;
componentTerminologiesSection: COMPONENT_TERMINOLOGIES_HEADER odinText ;

odinText : ODIN_LINE+ ;
cadlText : CADL_LINE+ ;
elText   : EL_LINE+ ;
