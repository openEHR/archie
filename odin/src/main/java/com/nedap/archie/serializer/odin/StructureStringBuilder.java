package com.nedap.archie.serializer.odin;

import java.util.Arrays;

/**
 * @author markopi
 */
public class StructureStringBuilder implements StructuredStringAppendable {
    private static final int INDENTATION_SIZE = 4;

    private final StringBuilder builder = new StringBuilder();
    private int indentDepth = 0;
    private boolean startOfLine = true;
    private int startOfLineIndex = 0;

    int currentLineLength = 0;

    public StructureStringBuilder() {
    }

    public static String padRight(String str, int size) {
        return padRight(str, size, ' ');
    }

    private static String padRight(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        return str + padding(padChar, pads);
    }

    private static String padding(char padChar, int repeat) throws IndexOutOfBoundsException {
        final char[] buf = new char[repeat];
        Arrays.fill(buf, padChar);
        return new String(buf);
    }

    @Override
    public StructureStringBuilder append(Object str) {
        String toAppend = str.toString();
        builder.append(toAppend);
        startOfLine = false;
        currentLineLength += toAppend.length();
        return this;
    }

    @Override
    public StructureStringBuilder tryNewLine() {
        if (!startOfLine) {
            return newline();
        } else {
            resetLineIndentation();
        }
        return this;
    }

    @Override
    public StructureStringBuilder newline() {
        builder.append("\n");
        startOfLineIndex = builder.length();
        appendIndentation();
        startOfLine = true;
        currentLineLength = 0;
        return this;
    }

    @Override
    public StructureStringBuilder indent() {
        indentDepth++;
        return this;
    }

    @Override
    public StructureStringBuilder newIndentedLine() {
        return indent().newline();
    }

    @Override
    public StructureStringBuilder unindent() {
        indentDepth--;
        if (indentDepth < 0) throw new AssertionError("Already on root indentation");
        if (startOfLine) {
            resetLineIndentation();
        }

        return this;
    }

    @Override
    public int mark() {
        return builder.length();
    }

    public void clearMark() {
        //nothing to be done here
    }

    @Override
    public void revert(int previousMark) {
        builder.setLength(previousMark);
    }

    @Override
    public StructuredStringAppendable ensureSpace() {
        if (builder.length() == 0) return this;
        char lastChar = builder.charAt(builder.length() - 1);
        if (Character.isWhitespace(lastChar)) return this;
        builder.append(" ");
        currentLineLength = 1;
        return this;
    }

    private void resetLineIndentation() {
        builder.setLength(startOfLineIndex);
        appendIndentation();
    }

    private void appendIndentation() {
        String toAppend = padding(' ', indentDepth * INDENTATION_SIZE);
        builder.append(toAppend);
        currentLineLength += toAppend.length();
    }

    public int getCurrentLineLength() {
        return currentLineLength;
    }

    public int getIndentDepth() {
        return indentDepth;
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
