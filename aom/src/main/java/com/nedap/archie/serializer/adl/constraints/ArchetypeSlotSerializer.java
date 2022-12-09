package com.nedap.archie.serializer.adl.constraints;


import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;
import com.nedap.archie.serializer.adl.ADLRulesSerializer;
import com.nedap.archie.serializer.adl.ArchetypeSerializeUtils;

/**
 * Created by bna on 27.01.2015.
 * <p>
 * allow_archetype CLUSTER[at1030] occurrences matches {0..1} matches {	-- Exertion
 * include
 * archetype_id/value matches {/openEHR-EHR-CLUSTER\.level_of_exertion(-[a-zA-Z0-9_]+)*\.v1/}
 * }
 */
public class ArchetypeSlotSerializer extends ConstraintSerializer<ArchetypeSlot> {

    public ArchetypeSlotSerializer(ADLDefinitionSerializer serializer) {
        super(serializer);
    }

    @Override
    public void serialize(ArchetypeSlot cobj) {
        builder.indent().newline();
        appendSiblingOrder(cobj);
        builder.append("allow_archetype")
                .append(" ")
                .append(cobj.getRmTypeName()).append("[").append(cobj.getNodeId()).append("]");
        if(cobj.isClosed()) {
            builder.append(" closed");
        }
        if (cobj.getOccurrences() != null) {
            builder.append(" occurrences matches {");
            ArchetypeSerializeUtils.buildOccurrences(builder, cobj.getOccurrences());
            builder.append("}");
        }
        if (!cobj.isClosed()) {
            appendMatches(cobj);
        }
        builder.unindent();
    }

    private void appendMatches(ArchetypeSlot cobj) {
        int mark = builder.mark();
        builder.append(" matches { ");
        builder.lineComment(serializer.getTermText(cobj));
        boolean hasContent = false;

        ADLRulesSerializer serializer = new ADLRulesSerializer(builder, super.serializer);
        if (cobj.getIncludes() != null && cobj.getIncludes().size() > 0) {
            hasContent = true;
            builder.indent().newline()
                    .append("include")
                    .indent();

            for (Assertion a : cobj.getIncludes()) {
                builder.newline();
                serializer.serializeRuleElement(a.getExpression());
            }
            builder.unindent().unindent();
        }
        if (cobj.getExcludes() != null && cobj.getExcludes().size() > 0) {
            hasContent = true;
            builder.indent().newline()
                    .append("exclude")
                    .indent();
            for (Assertion a : cobj.getExcludes()) {
                builder.newline();
                serializer.serializeRuleElement(a.getExpression());
            }
            builder.unindent().unindent();
        }

        if (hasContent) {
            builder.clearMark();
            builder.newline().append("}");
        } else {
            builder.revert(mark);
        }
    }
}
