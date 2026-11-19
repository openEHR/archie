package com.nedap.archie.serializer.odin;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public class StructuredStringWriter implements StructuredStringAppendable {

    private static final int INDENTATION_SIZE = 4;

    private final Writer writer;

    private int indentDepth = 0;
    private boolean startOfLine = true;
    private boolean lastCharacterWasSpace = false;

    private boolean marked = false;
    private StringBuilder markedContent = new StringBuilder();

    public StructuredStringWriter(Writer writer) {
        this.writer = writer;
    }

    private static String padding(char padChar, int repeat) throws IndexOutOfBoundsException {
        final char[] buf = new char[repeat];
        Arrays.fill(buf, padChar);
        return new String(buf);
    }

    @Override
    public StructuredStringWriter append(Object str) {
        checkIndentBeforeWrite();
        write(str.toString());
        startOfLine = false;
        return this;
    }

    @Override
    public StructuredStringWriter tryNewLine() {
        if (!startOfLine) {
            return newline();
        }
        return this;
    }

    @Override
    public StructuredStringWriter newline() {
        write("\n");

        startOfLine = true;
        return this;
    }

    @Override
    public StructuredStringWriter indent() {
        indentDepth++;
        return this;
    }

    @Override
    public StructuredStringWriter newIndentedLine() {
        return indent().newline();
    }

    @Override
    public StructuredStringWriter unindent() {
        indentDepth--;
        if (indentDepth < 0) throw new AssertionError("Already on root indentation");

        return this;
    }

    @Override
    public int mark() {
        if(marked) {
            return markedContent.length();
        }
       // return builder.length();
        marked = true;
        return 0;
    }

    @Override
    public void revert(int previousMark) {
        if(!marked) {
            throw new IllegalStateException("cannot revert without being in the marked state");
        }
        if(previousMark == 0) {
            markedContent.setLength(0);
            marked = false;
        } else {
            markedContent.setLength(previousMark);
        }
    }

    @Override
    public void clearMark() {
        marked = false;
        write(markedContent.toString());
        markedContent.setLength(0);
    }

    @Override
    public StructuredStringAppendable ensureSpace() {
        if(!lastCharacterWasSpace) {
            write(" ");
            lastCharacterWasSpace = true;
        }
        return this;
    }


    private void checkIndentBeforeWrite() {
        if(startOfLine && indentDepth > 0) {
            write(padding(' ', indentDepth * INDENTATION_SIZE));
            startOfLine = false;
        }
    }

    private void write(String s) {
        lastCharacterWasSpace = !s.isEmpty() && s.charAt(s.length() - 1) == ' ';
        if(marked) {
            markedContent.append(s);
        } else {
            try {
                writer.write(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
