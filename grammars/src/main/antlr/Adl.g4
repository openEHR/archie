//
//  description: Antlr4 grammar for Archetype Definition Language (ADL2)
//  author:      Thomas Beale <thomas.beale@openehr.org>
//  support:     openEHR Specifications PR tracker <https://openehr.atlassian.net/projects/SPECPR/issues>
//  copyright:   Copyright (c) 2015 openEHR Foundation
//  license:     Apache 2.0 License <http://www.apache.org/licenses/LICENSE-2.0.html>
//

grammar Adl;
import cadl, odin;

//
//  ============== Parser rules ==============
//

adl: ( archetype | template | templateOverlay | operationalTemplate ) EOF ;

archetype: 
    SYM_ARCHETYPE metaData?
    ARCHETYPE_HRID
    specializationSection?
    languageSection
    descriptionSection
    definitionSection
    rulesSection?
    rmOverlaySection?
    terminologySection
    annotationsSection?
    ;

template: 
    SYM_TEMPLATE metaData?
    ARCHETYPE_HRID
    specializationSection
    languageSection
    descriptionSection
    definitionSection
    rulesSection?
    rmOverlaySection?
    terminologySection
    annotationsSection?
    (templateOverlay)*
    ;

templateOverlay:
    SYM_TEMPLATE_OVERLAY //this token includes the horizontal comment line
    ARCHETYPE_HRID
    specializationSection
    definitionSection
    rmOverlaySection?
    terminologySection
    ;

operationalTemplate:
    SYM_OPERATIONAL_TEMPLATE metaData?
    ARCHETYPE_HRID
    specializationSection?
    languageSection
    descriptionSection
    definitionSection
    rulesSection?
    rmOverlaySection?
    terminologySection
    annotationsSection?
    componentTerminologiesSection?
    ;

specializationSection : SYM_SPECIALIZE archetype_ref ;
languageSection       : SYM_LANGUAGE odin_text ;
descriptionSection    : SYM_DESCRIPTION odin_text ;
definitionSection     : SYM_DEFINITION c_complex_object ;
rulesSection          : SYM_RULES assertion_list;
terminologySection    : SYM_TERMINOLOGY odin_text ;
annotationsSection    : SYM_ANNOTATIONS odin_text ;
rmOverlaySection       : SYM_RM_OVERLAY odin_text ;
componentTerminologiesSection: SYM_COMPONENT_TERMINOLOGIES odin_text ;

metaData: '(' metaDataItem  (';' metaDataItem )* ')' ;

metaDataItem:
      metaDataTagAdlVersion '=' VERSION_ID
    | metaDataTagUid '=' GUID
    | metaDataTagBuildUid '=' GUID
    | metaDataTagRmRelease '=' VERSION_ID
    | metaDataTagIsControlled
    | metaDataTagIsGenerated
    | identifier ( '=' metaDataValue )?
    ;

metaDataValue:
      primitive_value
    | GUID
    | VERSION_ID
    ;

metaDataTagAdlVersion   : 'adl_version' ;
metaDataTagUid           : 'uid' ;
metaDataTagBuildUid     : 'build_uid' ;
metaDataTagRmRelease    : 'rm_release' ;
metaDataTagIsControlled : 'controlled' ;
metaDataTagIsGenerated  : 'generated' ;
