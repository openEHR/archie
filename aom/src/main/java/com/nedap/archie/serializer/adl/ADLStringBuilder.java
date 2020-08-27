package com.nedap.archie.serializer.adl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nedap.archie.serializer.adl.jackson.ArchetypeODINMapperFactory;
import com.nedap.archie.serializer.odin.StructureStringBuilder;
import com.nedap.archie.serializer.odin.StructuredStringAppendable;
import org.openehr.odin.jackson.ODINMapper;

import static com.nedap.archie.serializer.odin.OdinStringBuilder.quoteText;

/**
 * @author markopi
 */
public class ADLStringBuilder implements StructuredStringAppendable {

    private final StructureStringBuilder builder = new StructureStringBuilder();

    private ODINMapper odinMapper = new ArchetypeODINMapperFactory().createMapper();

    @Override
    public ADLStringBuilder append(Object str) {
        builder.append(str);
        return this;
    }

    public ADLStringBuilder text(String str) {
        String text = quoteText(str);
        return append(text);
    }

    @Override
    public ADLStringBuilder tryNewLine() {
        builder.tryNewLine();
        return this;
    }

    @Override
    public ADLStringBuilder newline() {
        builder.newline();
        return this;
    }

    @Override
    public ADLStringBuilder indent() {
        builder.indent();
        return this;
    }

    public ADLStringBuilder odin(Object structure) {
        try {
            String odin = odinMapper.writeValueAsString(structure);
            appendMultipleLines(odin);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public ADLStringBuilder newIndentedLine() {
        return indent().newline();
    }

    @Override
    public ADLStringBuilder unindent() {
        builder.unindent();
        return this;
    }

    public ADLStringBuilder newUnindentedLine() {
        return unindent().newline();
    }


    public ADLStringBuilder lineComment(String comment) {
        if (comment != null) {
            append(StructureStringBuilder.padRight("", 4)).append("-- ").append(comment);
        }
        return this;
    }

    public int mark() {
        return builder.mark();
    }

    @Override
    public void revert(int previousMark) {
        builder.revert(previousMark);
    }

    @Override
    public void clearMark() {
        builder.clearMark();
    }

    @Override
    public String toString() {
        return builder.toString();
    }


    public ADLStringBuilder ensureSpace() {
        builder.ensureSpace();
        return this;
    }

    /**
     * Append multiple lines, adding indentation before each line
     * @param lines
     */
    public void appendMultipleLines(String lines) {
        for(String line:lines.split("\n")) {
            //append per line to get indentation
            builder.append(line);
            builder.newline();
        }
    }
}

