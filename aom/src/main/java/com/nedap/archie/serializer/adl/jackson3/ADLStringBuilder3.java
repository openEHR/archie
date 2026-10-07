package com.nedap.archie.serializer.adl.jackson3;

import com.nedap.archie.serializer.adl.ADLBuilder;
import com.nedap.archie.serializer.odin.StructureStringBuilder;
import org.openehr.odin.jackson3.ODINMapper3;
import org.openehr.odin.jackson3.ODINPrettyPrinter3;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectWriter;

import static com.nedap.archie.serializer.odin.OdinStringBuilder.quoteText;

/**
 * @author josh
 */
public class ADLStringBuilder3 implements ADLBuilder {

    private final StructureStringBuilder builder = new StructureStringBuilder();

    private final ODINMapper3 odinMapper;

    public ADLStringBuilder3() {
        odinMapper = (ODINMapper3) new ArchetypeODINMapperFactory3().createMapper();
    }

    @Override
    public ADLStringBuilder3 append(Object str) {
        String toAppend = str.toString();
        builder.append(toAppend);
        return this;
    }

    public ADLStringBuilder3 text(String str) {
        String text = quoteText(str);
        return append(text);
    }

    @Override
    public ADLStringBuilder3 tryNewLine() {
        builder.tryNewLine();
        return this;
    }

    @Override
    public ADLStringBuilder3 newline() {
        builder.newline();
        return this;
    }

    @Override
    public ADLStringBuilder3 indent() {
        builder.indent();
        return this;
    }

    public ADLStringBuilder3 odin(Object structure) {
        try {
            // Pass the current ident depth to the ODINPrettyPrinter3
            ObjectWriter objectWriter = odinMapper.writer().with(new ODINPrettyPrinter3(builder.getIndentDepth()));

            String odin = objectWriter.writeValueAsString(structure).trim();
            builder.append(odin).newline();
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public ADLStringBuilder3 newIndentedLine() {
        return indent().newline();
    }

    @Override
    public ADLStringBuilder3 unindent() {
        builder.unindent();
        return this;
    }

    public ADLStringBuilder3 newUnindentedLine() {
        return unindent().newline();
    }


    public ADLStringBuilder3 lineComment(String comment) {
        if (comment != null) {
            // Remove line breaks and surrounding spaces
            comment = comment.replaceAll("\\s*(\n\\s*)+", " ").trim();
            if (!comment.isEmpty()) {
                append(StructureStringBuilder.padRight("", 4)).append("-- ").append(comment);
            }
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


    public ADLStringBuilder3 ensureSpace() {
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

    public int getCurrentLineLength() {
        return builder.getCurrentLineLength();
    }

    public int getIndentDepth() {
        return builder.getIndentDepth();
    }

    public ODINMapper3 getOdinMapper() {
        return odinMapper;
    }
}
