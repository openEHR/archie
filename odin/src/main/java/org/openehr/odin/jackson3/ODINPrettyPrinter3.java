package org.openehr.odin.jackson3;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.SerializableString;
import tools.jackson.core.io.SerializedString;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter.Indenter;
import tools.jackson.core.util.DefaultPrettyPrinter.NopIndenter;
import tools.jackson.core.util.Instantiatable;
import tools.jackson.core.util.Separators;
import tools.jackson.core.PrettyPrinter;

/**
 * PrettyPrinter for ODIN with the ability to set an initial indent level.
 * Jackson 3 port of {@link org.openehr.odin.jackson.ODINPrettyPrinter}.
 */
public class ODINPrettyPrinter3
        implements PrettyPrinter, Instantiatable<ODINPrettyPrinter3>,
        java.io.Serializable {
    private static final long serialVersionUID = 1;

    protected Indenter _objectIndenter = new DefaultIndenter("    ", DefaultIndenter.SYS_LF);
    protected SerializableString _rootSeparator;
    protected transient int _nesting;
    protected Separators _separators;
    protected String _objectNameValueSeparatorWithSpaces;
    protected String _objectEmptySeparator;
    protected String _arrayValueSeparator;
    protected String _arrayEmptySeparator;

    public ODINPrettyPrinter3() {
        this(0);
    }

    public ODINPrettyPrinter3(int nesting) {
        this(Separators.createDefaultInstance()
                .withObjectNameValueSeparator('=')
                .withArrayElementSeparator(','), nesting);
    }

    public ODINPrettyPrinter3(Separators separators, int nesting) {
        _separators = separators;
        _rootSeparator = separators.getRootSeparator() == null ? null
                : new SerializedString(separators.getRootSeparator());
        _objectNameValueSeparatorWithSpaces = separators.getObjectNameValueSpacing().apply(
                separators.getObjectNameValueSeparator());
        _objectEmptySeparator = separators.getObjectEmptySeparator();
        _arrayValueSeparator = separators.getArrayElementSpacing().apply(separators.getArrayElementSeparator());
        _arrayEmptySeparator = separators.getArrayEmptySeparator();
        _nesting = nesting;
    }

    public ODINPrettyPrinter3(ODINPrettyPrinter3 base) {
        _rootSeparator = base._rootSeparator;
        _objectIndenter = base._objectIndenter;
        _nesting = base._nesting;
        _separators = base._separators;
        _objectNameValueSeparatorWithSpaces = base._objectNameValueSeparatorWithSpaces;
        _objectEmptySeparator = base._objectEmptySeparator;
        _arrayValueSeparator = base._arrayValueSeparator;
        _arrayEmptySeparator = base._arrayEmptySeparator;
    }

    public void indentObjectsWith(Indenter i) {
        _objectIndenter = (i == null) ? NopIndenter.instance() : i;
    }

    public ODINPrettyPrinter3 withObjectIndenter(Indenter i) {
        if (i == null) {
            i = NopIndenter.instance();
        }
        if (_objectIndenter == i) {
            return this;
        }
        ODINPrettyPrinter3 pp = new ODINPrettyPrinter3(this);
        pp._objectIndenter = i;
        return pp;
    }

    public ODINPrettyPrinter3 withSeparators(Separators separators) {
        ODINPrettyPrinter3 result = new ODINPrettyPrinter3(this);
        result._separators = separators;
        result._rootSeparator = separators.getRootSeparator() == null ? null
                : new SerializedString(separators.getRootSeparator());
        result._objectNameValueSeparatorWithSpaces = separators.getObjectNameValueSpacing().apply(
                separators.getObjectNameValueSeparator());
        result._objectEmptySeparator = separators.getObjectEmptySeparator();
        result._arrayValueSeparator = separators.getArrayElementSpacing().apply(separators.getArrayElementSeparator());
        result._arrayEmptySeparator = separators.getArrayEmptySeparator();
        return result;
    }

    @Override
    public ODINPrettyPrinter3 createInstance() {
        if (getClass() != ODINPrettyPrinter3.class) {
            throw new IllegalStateException("Failed `createInstance()`: " + getClass().getName()
                    + " does not override method; it has to");
        }
        return new ODINPrettyPrinter3(this);
    }

    @Override
    public void writeRootValueSeparator(JsonGenerator g) throws JacksonException {
        if (_rootSeparator != null) {
            g.writeRaw(_rootSeparator);
        }
    }

    @Override
    public void writeStartObject(JsonGenerator g) throws JacksonException {
        g.writeRaw('<');
        if (!_objectIndenter.isInline()) {
            ++_nesting;
        }
    }

    @Override
    public void beforeObjectEntries(JsonGenerator g) throws JacksonException {
        if (_nesting > 0) {
            _objectIndenter.writeIndentation(g, _nesting);
        }
    }

    @Override
    public void writeObjectNameValueSeparator(JsonGenerator g) throws JacksonException {
        g.writeRaw(_objectNameValueSeparatorWithSpaces);
    }

    @Override
    public void writeObjectEntrySeparator(JsonGenerator g) throws JacksonException {
        _objectIndenter.writeIndentation(g, _nesting);
    }

    @Override
    public void writeEndObject(JsonGenerator g, int nrOfEntries) throws JacksonException {
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
    public void writeStartArray(JsonGenerator g) throws JacksonException {
        if (!_objectIndenter.isInline()) {
            ++_nesting;
        }
    }

    @Override
    public void beforeArrayValues(JsonGenerator g) throws JacksonException {
        _objectIndenter.writeIndentation(g, _nesting);
    }

    @Override
    public void writeArrayValueSeparator(JsonGenerator g) throws JacksonException {
        _objectIndenter.writeIndentation(g, _nesting);
    }

    @Override
    public void writeEndArray(JsonGenerator g, int nrOfValues) throws JacksonException {
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
