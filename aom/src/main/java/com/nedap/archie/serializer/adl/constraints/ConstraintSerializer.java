package com.nedap.archie.serializer.adl.constraints;


import com.nedap.archie.aom.CObject;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;
import com.nedap.archie.serializer.adl.ADLStringBuilder;

import static com.nedap.archie.serializer.adl.ArchetypeSerializeUtils.buildOccurrences;

/**
 * @author Marko Pipan
 */
public abstract class ConstraintSerializer<T extends CObject> {
    protected final ADLDefinitionSerializer serializer;
    protected final ADLStringBuilder builder;

    public ConstraintSerializer(ADLDefinitionSerializer serializer) {
        this.serializer = serializer;
        this.builder = serializer.getBuilder();
    }

    abstract public void serialize(T cobj);

    public String getSimpleCommentText(T cobj) {
        return null;
    }

    public boolean isEmpty(T cobj) {
        return false;
    }

    public void revert(int previousMark) {
        builder.revert(previousMark);
    }

    protected void appendOccurrences(T cobj) {
        if (cobj.getOccurrences() != null) {
            builder.ensureSpace();
            builder.append("occurrences matches {");
            buildOccurrences(builder, cobj.getOccurrences());
            builder.append("}");
        }
    }

    protected void appendSiblingOrder(T cobj) {
        if(cobj.getSiblingOrder() != null) {
            builder.ensureSpace();
            builder.append(cobj.getSiblingOrder().isBefore() ? "before" : "after");
            builder.append(" ");
            builder.append("[");
            builder.append(cobj.getSiblingOrder().getSiblingNodeId());
            builder.append("]");
            builder.newline();
        }
    }
}
