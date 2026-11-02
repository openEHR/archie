package org.openehr.odin.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.NopIndenter;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.core.util.Separators;

import java.io.IOException;

/**
 * PrettyPrinter for ODIN with the ability to set an initial indent level.
 * <p>
 * Based on {@link com.fasterxml.jackson.core.util.DefaultPrettyPrinter}
 */
public class ODINPrettyPrinter
        implements PrettyPrinter, Instantiatable<ODINPrettyPrinter>,
        java.io.Serializable {
    private static final long serialVersionUID = 1;

    // // // Config, indentation

    /**
     * By default, let's use linefeed-adding indenter for separate
     * object entries. We'll further configure indenter to use
     * system-specific linefeeds, and 4 spaces per level (as opposed to,
     * say, single tabs)
     */
    protected Indenter _objectIndenter = new DefaultIndenter("    ", DefaultIndenter.SYS_LF);

    /**
     * String printed between root-level values, if any.
     */
    protected SerializableString _rootSeparator;

    // // // State:

    /**
     * Number of open levels of nesting. Used to determine amount of
     * indentation to use.
     */
    protected transient int _nesting;

    protected Separators _separators;

    protected String _objectFieldValueSeparatorWithSpaces;

    /**
     * String to use in empty Object to separate start and end markers.
     * Default is single space, resulting in output of {@code < >}.
     */
    protected String _objectEmptySeparator;

    protected String _arrayValueSeparator;

    /**
     * String to use in empty Array to separate start and end markers.
     * Default is single space, resulting in output of {@code < >}.
     */
    protected String _arrayEmptySeparator;

    /*
    /**********************************************************
    /* Life-cycle (construct, configure)
    /**********************************************************
    */

    public ODINPrettyPrinter() {
        this(0);
    }

    public ODINPrettyPrinter(int nesting) {
        this(new Separators('=', ' ' /* unused */, ','), nesting);
    }

    public ODINPrettyPrinter(Separators separators, int nesting) {
        _separators = separators;

        _rootSeparator = separators.getRootSeparator() == null ? null : new SerializedString(separators.getRootSeparator());
        _objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSpacing().apply(
                separators.getObjectFieldValueSeparator());
        _objectEmptySeparator = separators.getObjectEmptySeparator();
        _arrayValueSeparator = separators.getArrayValueSpacing().apply(separators.getArrayValueSeparator());
        _arrayEmptySeparator = separators.getArrayEmptySeparator();

        _nesting = nesting;
    }

    /**
     * Copy constructor
     */
    public ODINPrettyPrinter(ODINPrettyPrinter base) {
        _rootSeparator = base._rootSeparator;

        _objectIndenter = base._objectIndenter;
        _nesting = base._nesting;

        _separators = base._separators;
        _objectFieldValueSeparatorWithSpaces = base._objectFieldValueSeparatorWithSpaces;
        _objectEmptySeparator = base._objectEmptySeparator;
        _arrayValueSeparator = base._arrayValueSeparator;
        _arrayEmptySeparator = base._arrayEmptySeparator;
    }

    public void indentObjectsWith(Indenter i) {
        _objectIndenter = (i == null) ? NopIndenter.instance : i;
    }

    public ODINPrettyPrinter withObjectIndenter(Indenter i) {
        if (i == null) {
            i = NopIndenter.instance;
        }
        if (_objectIndenter == i) {
            return this;
        }
        ODINPrettyPrinter pp = new ODINPrettyPrinter(this);
        pp._objectIndenter = i;
        return pp;
    }

    /**
     * Method for configuring separators
     *
     * @param separators Separator definitions to use
     * @return Copy of this pretty-printer instance with given separators
     */
    public ODINPrettyPrinter withSeparators(Separators separators) {
        ODINPrettyPrinter result = new ODINPrettyPrinter(this);
        result._separators = separators;

        result._rootSeparator = separators.getRootSeparator() == null ? null : new SerializedString(separators.getRootSeparator());
        result._objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSpacing().apply(
                separators.getObjectFieldValueSeparator());
        result._objectEmptySeparator = separators.getObjectEmptySeparator();
        result._arrayValueSeparator = separators.getArrayValueSpacing().apply(separators.getArrayValueSeparator());
        result._arrayEmptySeparator = separators.getArrayEmptySeparator();

        return result;
    }

    /*
    /**********************************************************
    /* Instantiatable impl
    /**********************************************************
     */

    @Override
    public ODINPrettyPrinter createInstance() {
        if (getClass() != ODINPrettyPrinter.class) { // since 2.10
            throw new IllegalStateException("Failed `createInstance()`: " + getClass().getName()
                    + " does not override method; it has to");
        }
        return new ODINPrettyPrinter(this);
    }

    /*
    /**********************************************************
    /* PrettyPrinter impl
    /**********************************************************
     */

    @Override
    public void writeRootValueSeparator(JsonGenerator g) throws IOException {
        if (_rootSeparator != null) {
            g.writeRaw(_rootSeparator);
        }
    }

    @Override
    public void writeStartObject(JsonGenerator g) throws IOException {
        g.writeRaw('<');
        if (!_objectIndenter.isInline()) {
            ++_nesting;
        }
    }

    @Override
    public void beforeObjectEntries(JsonGenerator g) throws IOException {
        // No indent and newline on first level
        if(_nesting > 0) {
            _objectIndenter.writeIndentation(g, _nesting);
        }
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(_objectFieldValueSeparatorWithSpaces);
    }

    @Override
    public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
        _objectIndenter.writeIndentation(g, _nesting);
    }

    @Override
    public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
        if (!_objectIndenter.isInline()) {
            --_nesting;
        }
        if (nrOfEntries > 0) {
            _objectIndenter.writeIndentation(g, _nesting);
        } else {
            g.writeRaw(_objectEmptySeparator);
        }
        g.writeRaw('>');
    }

    @Override
    public void writeStartArray(JsonGenerator g) throws IOException {
        // g.writeRaw('<') is already done by ODINGeneator.writeStartObject
        if (!_objectIndenter.isInline()) {
            ++_nesting;
        }
    }

    @Override
    public void beforeArrayValues(JsonGenerator g) throws IOException {
        _objectIndenter.writeIndentation(g, _nesting);
    }

    @Override
    public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
        _objectIndenter.writeIndentation(g, _nesting);
    }

    @Override
    public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
        if (!_objectIndenter.isInline()) {
            --_nesting;
        }
        if (nrOfValues > 0) {
            _objectIndenter.writeIndentation(g, _nesting);
        } else {
            g.writeRaw(_arrayEmptySeparator);
        }
        g.writeRaw('>');
    }
}
