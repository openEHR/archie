package com.nedap.archie.serializer.adl;

import com.nedap.archie.serializer.odin.StructuredStringAppendable;

/** ADL formatting operations shared by the Jackson 2 and Jackson 3 builders. */
public interface ADLBuilder extends StructuredStringAppendable {
    ADLBuilder append(Object value);
    ADLBuilder text(String value);
    ADLBuilder tryNewLine();
    ADLBuilder newline();
    ADLBuilder indent();
    ADLBuilder odin(Object value);
    ADLBuilder newIndentedLine();
    ADLBuilder unindent();
    ADLBuilder newUnindentedLine();
    ADLBuilder lineComment(String comment);
    ADLBuilder ensureSpace();
    void appendMultipleLines(String lines);
    int getCurrentLineLength();
    int getIndentDepth();
}
