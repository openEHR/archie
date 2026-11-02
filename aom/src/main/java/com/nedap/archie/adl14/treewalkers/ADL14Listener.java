package com.nedap.archie.adl14.treewalkers;

import com.nedap.archie.adl14.ADL14ConversionConfiguration;
import com.nedap.archie.adlparser.antlr.Adl14BaseListener;
import com.nedap.archie.adlparser.antlr.Adl14Parser.*;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.*;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * ANTLR listener for an ADLS file. Uses the listener construction for the topmost elements, switches to custom treewalker
 * for elements lower in the tree. This approach saves some code and complexity.
 *
 * Created by pieter.bos on 19/10/15.
 */
public class ADL14Listener extends Adl14BaseListener {

    private ANTLRParserErrors errors;

    private Archetype rootArchetype;

    private Archetype archetype;
    private Adl14CComplexObjectParser subTreeWalker;
    private Adl14TerminologyParser terminologyParser;

    private Odin14ValueParser odinParser;

    public ADL14Listener(ANTLRParserErrors errors, ADL14ConversionConfiguration configuration) {
        this.errors = errors;
        odinParser = new Odin14ValueParser(configuration);
        subTreeWalker = new Adl14CComplexObjectParser(errors, odinParser);
        terminologyParser = new Adl14TerminologyParser(errors, configuration, odinParser);

    }

    /** top-level constructs */
    @Override
    public void enterArchetype(ArchetypeContext ctx) {
        rootArchetype = new AuthoredArchetype();
        archetype = rootArchetype;
        parseArchetypeHRID(ctx.ARCHETYPE_HRID());
    }

    @Override
    public void exitArchetype(ArchetypeContext ctx) {
        rootArchetype.setDifferential(true); //Technically this should be false in ADL 1.4, but it'll be converted to differential later
    }

    private void parseArchetypeHRID(TerminalNode hrId) {
        if(hrId != null) {
            ArchetypeHRID archetypeID = new ArchetypeHRID(hrId.getText());
            archetype.setArchetypeId(archetypeID);
        }
    }

    public void enterMeta_data_item(Meta_data_itemContext ctx) {
        /*
         SYM_ADL_VERSION '=' VERSION_ID
        | SYM_UID '=' (GUID | OID)
        | SYM_BUILD_UID '=' (GUID | OID)
        | SYM_RM_RELEASE '=' VERSION_ID
        | SYM_IS_CONTROLLED
        | SYM_IS_GENERATED
        | identifier ( '=' meta_data_value )?

         */
        if(archetype instanceof AuthoredArchetype) {
            AuthoredArchetype authoredArchetype = (AuthoredArchetype) archetype;

            if(ctx.meta_data_tag_adl_version() != null) {
                if(ctx.VERSION_ID() != null) {
                    authoredArchetype.setAdlVersion(ctx.VERSION_ID().getText());
                } else if (ctx.REAL() != null) {
                    authoredArchetype.setAdlVersion(ctx.REAL().getText());
                }
            }
            if(ctx.meta_data_tag_build_uid() != null) {
                authoredArchetype.setBuildUid(ctx.guid_or_oid().getText());
            }
            if(ctx.meta_data_tag_rm_release() != null) {
                authoredArchetype.setRmRelease(ctx.VERSION_ID().getText());
            }
            if(ctx.meta_data_tag_is_controlled() != null) {
                authoredArchetype.setControlled(true);
            }
            if(ctx.meta_data_tag_is_generated() != null) {
                authoredArchetype.setGenerated(true);
            }
            if(ctx.meta_data_tag_uid() != null) {
                authoredArchetype.setUid(ctx.guid_or_oid().getText());
            }
            else if(ctx.identifier() != null) {
                authoredArchetype.addOtherMetadata(ctx.identifier().getText(), ctx.meta_data_value() == null ? null : ctx.meta_data_value().getText());
            }
        }

    }

    /**
     * one level below: definition, language, etc.
     */
    @Override
    public void enterDefinition_section(Definition_sectionContext ctx) {
        CComplexObject definition = subTreeWalker.parseComplexObject(ctx.c_complex_object());
        archetype.setDefinition(definition);
    }

    @Override
    public void enterLanguage_section(Language_sectionContext ctx) {
        archetype.setAuthoredResourceContent(odinParser.convert(ctx.odin_text().getText(), LanguageSection.class));
    }

    @Override
    public void enterTerminology_section(Terminology_sectionContext ctx) {
        archetype.setTerminology(terminologyParser.parseTerminology(ctx));
    }

    @Override
    public void enterDescription_section(Description_sectionContext ctx) {
        archetype.setDescription(odinParser.convert(ctx.odin_text().getText(), ResourceDescription.class));
    }

    @Override
    public void enterSpecialization_section(Specialization_sectionContext ctx) {
        if(ctx != null && ctx.archetype_ref() != null) {
            archetype.setParentArchetypeId(ctx.archetype_ref().getText());
        }
    }

    public void enterRules_section(Rules_sectionContext ctx) {
        archetype.setRules(subTreeWalker.parseRules(ctx));
    }

    /* getters for result */
    public Archetype getArchetype() {
        return rootArchetype;
    }

    public ANTLRParserErrors getErrors() {
        return errors;
    }



}
