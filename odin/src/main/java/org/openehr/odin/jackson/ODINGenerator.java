package org.openehr.odin.jackson;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.nedap.archie.serializer.odin.OdinStringBuilder;
import com.nedap.archie.serializer.odin.StructuredStringWriter;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ODINGenerator extends GeneratorBase
{
    public static final String WRITE_START_OBJECT_TYPE_MSG = "start an object";
    private OdinStringBuilder builder;

    /**
     * Enumeration that defines all togglable features for ODIN generators
     */
    public enum Feature implements FormatFeature // since 2.9
    {
        ;

        protected final boolean _defaultState;
        protected final int _mask;

        /**
         * Method that calculates bit set (flags) of all features that
         * are enabled by default.
         */
        public static int collectDefaults()
        {
            int flags = 0;
            for (Feature f : values()) {
                if (f.enabledByDefault()) {
                    flags |= f.getMask();
                }
            }
            return flags;
        }

        private Feature(boolean defaultState) {
            _defaultState = defaultState;
            _mask = (1 << ordinal());
        }

        @Override
        public boolean enabledByDefault() { return _defaultState; }
        @Override
        public boolean enabledIn(int flags) { return (flags & _mask) != 0; }
        @Override
        public int getMask() { return _mask; }
    }

    /*
    /**********************************************************
    /* Internal constants
    /**********************************************************
     */

    protected final static long MIN_INT_AS_LONG = (long) Integer.MIN_VALUE;
    protected final static long MAX_INT_AS_LONG = (long) Integer.MAX_VALUE;
    protected final static Pattern PLAIN_NUMBER_P = Pattern.compile("[0-9]*(\\.[0-9]*)?");


    /*
    /**********************************************************
    /* Configuration
    /**********************************************************
     */

    final protected IOContext _ioContext;

    protected OdinObjectWriteContext typeIdContext;

    /**
     * Bit flag composed of bits that indicate which
     * {@link ODINGenerator.Feature}s
     * are enabled.
     */
    protected int _formatFeatures;

    protected Writer _writer;

    // for field names, leave out quotes
    private final static Character STYLE_NAME = null;

    // numbers, booleans, should use implicit
    private final static Character STYLE_SCALAR = null;
    // Strings quoted for fun
    private final static Character STYLE_QUOTED = Character.valueOf('"');
    // Strings in literal (block) style
    private final static Character STYLE_LITERAL = Character.valueOf('|');

    // Which flow style to use for Base64? Maybe basic quoted?
    // 29-Nov-2017, tatu: Actually SnakeYAML uses block style so:
    private final static Character STYLE_BASE64 = STYLE_LITERAL;

    private final static Character STYLE_PLAIN = null;

    /*
    /**********************************************************
    /* Output state
    /**********************************************************
     */

    /**
     * YAML supports native Object identifiers, so databinder may indicate
     * need to output one.
     */
    protected String _objectId;

    /**
     * YAML supports native Type identifiers, so databinder may indicate
     * need to output one.
     */
    protected String _typeId;
    protected String _typeIdAtRoot;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public ODINGenerator(IOContext ctxt, int jsonFeatures, int odinFeatures,
                         ObjectCodec codec, Writer out)
        throws IOException
    {
        super(jsonFeatures, codec);
        _ioContext = ctxt;
        _formatFeatures = odinFeatures;
        _writer = out;


        builder = new OdinStringBuilder(new StructuredStringWriter(out));
        typeIdContext = OdinObjectWriteContext.createRootContext();
    }


    /*
    /**********************************************************
    /* Versioned
    /**********************************************************
     */

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    /*
    /**********************************************************
    /* Overridden methods, configuration
    /**********************************************************
     */

    /**
     * Not sure what to do here; could reset indentation to some value maybe?
     */
    @Override
    public ODINGenerator useDefaultPrettyPrinter()
    {
        return this;
    }

    /**
     * Not sure what to do here; will always indent, but uses
     * YAML-specific settings etc.
     */
    @Override
    public ODINGenerator setPrettyPrinter(PrettyPrinter pp) {
        return this;
    }

    @Override
    public Object getOutputTarget() {
        return _writer;
    }

    /**
     *  not supported for now, so -1
     */
    @Override
    public int getOutputBuffered() {
        return -1;
    }

    @Override
    public int getFormatFeatures() {
        return _formatFeatures;
    }

    @Override
    public JsonGenerator overrideFormatFeatures(int values, int mask) {
        // 14-Mar-2016, tatu: Should re-configure, but unfortunately most
        //    settings passed via options passed to constructor of Emitter
        _formatFeatures = (_formatFeatures & ~mask) | (values & mask);
        return this;
    }

    @Override
    public boolean canUseSchema(FormatSchema schema) {
        return false;
    }

    @Override
    public boolean canWriteFormattedNumbers() { return true; }

    //@Override public void setSchema(FormatSchema schema)

    /*
    /**********************************************************
    /* Extended API, configuration
    /**********************************************************
     */

    public ODINGenerator enable(Feature f) {
        _formatFeatures |= f.getMask();
        return this;
    }

    public ODINGenerator disable(Feature f) {
        _formatFeatures &= ~f.getMask();
        return this;
    }

    public final boolean isEnabled(Feature f) {
        return (_formatFeatures & f.getMask()) != 0;
    }

    public ODINGenerator configure(Feature f, boolean state) {
        if (state) {
            enable(f);
        } else {
            disable(f);
        }
        return this;
    }

    /*
    /**********************************************************************
    /* Overridden methods; writing field names
    /**********************************************************************
     */
    @Override
    public void writeObject(Object pojo) throws IOException {

    }

    /* And then methods overridden to make final, streamline some
     * aspects...
     */

    @Override
    public final void writeFieldName(String name) throws IOException
    {
        int status = _writeContext.writeFieldName(name);
        if (status == JsonWriteContext.STATUS_EXPECT_VALUE) {
            _reportError("Can not write a field name, expecting a value");
        }
        //no comma's in odin, so don't need to check status
        builder.tryNewLine().append(name).append(" = ");
    }

    @Override
    public final void writeFieldName(SerializableString name)
        throws IOException
    {
        writeFieldName(name.getValue());
    }

    @Override
    public final void writeStringField(String fieldName, String value)
        throws IOException
    {
        if (_writeContext.writeFieldName(fieldName) == JsonWriteContext.STATUS_EXPECT_VALUE) {
            _reportError("Can not write a field name, expecting a value");
        }
        writeFieldName(fieldName);
        writeFieldStart();
        writeString(value);
        writeFieldStart();
    }


    /*
    /**********************************************************
    /* Public API: low-level I/O
    /**********************************************************
     */

    @Override
    public final void flush() throws IOException
    {
        _writer.flush();
    }

    @Override
    public void close() throws IOException
    {
        if (!isClosed()) {
            _writer.close();
        }
    }

    /*
    /**********************************************************
    /* Public API: structural output
    /**********************************************************
     */

    @Override
    public final void writeStartArray() throws IOException
    {
        _verifyValueWrite("start an array");
        _writeContext = _writeContext.createChildArrayContext();
        typeIdContext = typeIdContext.createChild(null);
        String anchor = _objectId;
        if (anchor != null) {
            _objectId = null;
        }

        builder.append("<");
    }

    @Override
    public final void writeEndArray() throws IOException
    {
        if (!_writeContext.inArray()) {
            _reportError("Current context not Array but "+_writeContext.typeDesc());
        }
        // just to make sure we don't "leak" type ids
        _typeId = null;

        int index = _writeContext.getCurrentIndex();
        //ODIN marks lists with only one element with '<"ELEMENT", ...>'
        //but not for lists with more than one element
        //not sure what it does for empty lists...
        if(index == 0 && !this.typeIdContext.arrayHasObjects()) {
            builder.append(", ...");
        }
        //TODO: if this was a list of objects and not just primitives, add an unindentednewline here
        //requires a custom writeContext to do properly though
        builder.append(">");
        _writeContext = _writeContext.getParent();
        typeIdContext = typeIdContext.getParent();
    }

    @Override
    public final void writeStartObject() throws IOException
    {
        _verifyValueWrite(WRITE_START_OBJECT_TYPE_MSG);

        String anchor = _objectId;
        if (anchor != null) {
            _objectId = null;
        }
        //set that this thing has at least one object as child. needed to determine whether we need to add
        //... to an array, or if it's a map in case of a single element or even empty array
        typeIdContext.markHasChildObjects(true);
        typeIdContext = typeIdContext.createChild(_typeId);
        _typeId = null;


        if(_writeContext.inArray()) {
            //ODIN REQUIRES keyed lists when these are objects
            //which is really annoying because it is no longer possible to distinguish between maps and lists
            //so if we encounter a list of objects, just prepend the array index
            //it's not specified if this should be 0-based or 1-based, but the examples are 1-based
            //so that's what we do here.
            //on the parsing side this WILL require a custom map to list converter
            if(_writeContext.getCurrentIndex() == 0) {
                //we need an extra indent because of the keyed list, but only on the first member
                //can't do it on array start because there is no way to know then what the type of the array is
                builder.indent();
            }
            builder.newline().append("[" + (_writeContext.getCurrentIndex() + 1) + "] = ");
        }

        if(typeIdContext.hasTypeId()) {
            builder.append("(").append(typeIdContext.getTypeId().toString()).append(") <").indent();
        } else if(!_writeContext.inRoot()) {
            builder.append("<").indent();
        }

        _writeContext = _writeContext.createChildObjectContext();


    }

    @Override
    public final void writeEndObject() throws IOException
    {
        if (!_writeContext.inObject()) {
            _reportError("Current context not Object but "+_writeContext.typeDesc());
        }

        _writeContext = _writeContext.getParent();

        if(_writeContext.inArray()) {
            builder.newUnindentedLine().append(">");
        } else if (_writeContext.inRoot() && _typeIdAtRoot != null) {
            builder.newUnindentedLine().append(">");
        } else if(!_writeContext.inRoot())  {
            builder.newUnindentedLine().append(">");
        }
        // just to make sure we don't "leak" type ids
        _typeId = null;
        typeIdContext = typeIdContext.getParent();


    }

    /*
    /**********************************************************
    /* Output method implementations, textual
    /**********************************************************
     */

    @Override
    public void writeString(String text) throws IOException,JsonGenerationException
    {
        if (text == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write String value");
        writeFieldStart();
        builder.text(text);
        writeFieldEnd();

    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException
    {
        writeString(new String(text, offset, len));
    }

    @Override
    public final void writeString(SerializableString sstr)
        throws IOException
    {
        writeString(sstr.toString());
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int len)
        throws IOException
    {
        _reportUnsupportedOperation();
    }

    @Override
    public final void writeUTF8String(byte[] text, int offset, int len)
        throws IOException
    {
        writeString(new String(text, offset, len, "UTF-8"));
    }

    /*
    /**********************************************************
    /* Output method implementations, unprocessed ("raw")
    /**********************************************************
     */

    @Override
    public void writeRaw(String text) throws IOException {
        builder.append(text);
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException {
        builder.append(new String(text.getBytes(), offset, len));
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        builder.append(new String(text, offset, len));
    }

    @Override
    public void writeRaw(char c) throws IOException {
        builder.append(c);
    }

    @Override
    public void writeRawValue(String text) throws IOException {
        _verifyValueWrite("write String value");
        writeFieldStart();
        builder.append(text);
        writeFieldEnd();
    }

    @Override
    public void writeRawValue(String text, int offset, int len) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override
    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        _reportUnsupportedOperation();
    }

    /*
    /**********************************************************
    /* Output method implementations, base64-encoded binary
    /**********************************************************
     */

    @Override
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException
    {
        if (data == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write Binary value");
        if (offset > 0 || (offset+len) != data.length) {
            data = Arrays.copyOfRange(data, offset, offset+len);
        }
        _writeScalarBinary(b64variant, data);
    }

    /*
    /**********************************************************
    /* Output method implementations, scalars
    /**********************************************************
     */

    @Override
    public void writeBoolean(boolean state) throws IOException
    {
        _verifyValueWrite("write boolean value");
        writeFieldStart();
        builder.append(state ? "True": "False");
        writeFieldEnd();
    }

    @Override
    public void writeNumber(int i) throws IOException
    {
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(Integer.toString(i));
        writeFieldEnd();
    }

    @Override
    public void writeNumber(long l) throws IOException
    {
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(Long.toString(l));
        writeFieldEnd();
    }



    private void writeFieldStart() {
        if(!_writeContext.inArray()) {
            builder.append("<");
        }
    }

    private void writeFieldEnd() {
        if(!_writeContext.inArray()) {
            builder.append(">");
        }
    }

    @Override
    public void writeNumber(BigInteger v) throws IOException
    {
        if (v == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(String.valueOf(v.toString()));
        writeFieldEnd();
    }

    @Override
    public void writeNumber(double d) throws IOException
    {
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(Double.toString(d));
        writeFieldEnd();
    }

    @Override
    public void writeNumber(float f) throws IOException
    {
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(Float.toString(f));
        writeFieldEnd();
    }

    @Override
    public void writeNumber(BigDecimal dec) throws IOException
    {
        if (dec == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write number");
        String str = isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) ? dec.toPlainString() : dec.toString();
        writeFieldStart();
        builder.append(str);
        writeFieldEnd();
    }

    @Override
    public void writeNumber(String encodedValue) throws IOException,JsonGenerationException, UnsupportedOperationException
    {
        if (encodedValue == null) {
            writeNull();
            return;
        }
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.text(encodedValue);//TODO: check
        writeFieldEnd();
    }

    @Override
    public void writeNull() throws IOException
    {
        _verifyValueWrite("write null value");
        //TODO! no null support in ODIN. Just don't serialize the field
        writeFieldStart();
        //nothing to append - there is no explicit null
        //and nothing in the standard to address this!
        writeFieldEnd();
    }

    /*
    /**********************************************************
    /* Public API, write methods, Native Ids
    /**********************************************************
     */

    @Override
    public boolean canWriteObjectId() {
        //nope. Well, not sure what it is :)
        return false;
    }

    @Override
    public boolean canWriteTypeId() {
        //yup, odin does this
        return true;
    }

    @Override
    public void writeTypeId(Object id)
        throws IOException
    {
        // should we verify there's no preceding type id?
        _typeId = String.valueOf(id);
        if(_writeContext.inRoot()) {
            //TODO: make _typeId a proper stack so we always know the current type id at end object?
            _typeIdAtRoot = _typeId;
        }
        //don't write yet, this will happen when the object is written
    }

    @Override
    public void writeObjectRef(Object id)
        throws IOException
    {
        _verifyValueWrite("write Object reference");
        //TODO!!!
    }

    @Override
    public void writeObjectId(Object id)
        throws IOException
    {
        // should we verify there's no preceding id?
        _objectId = String.valueOf(id);
    }

    /*
    /**********************************************************
    /* Implementations for methods from base class
    /**********************************************************
     */

    @Override
    protected final void _verifyValueWrite(String typeMsg)
        throws IOException
    {
        int status = _writeContext.writeValue();
        if (status == JsonWriteContext.STATUS_EXPECT_NAME) {
            _reportError("Can not "+typeMsg+", expecting field name");
        }
        if(status == JsonWriteContext.STATUS_OK_AFTER_COMMA && _writeContext.inArray() && !typeIdContext.arrayHasObjects()) {
            builder.append(", ");
        }
    }

    @Override
    protected void _releaseBuffers() {
        // nothing special to do...
    }

    /*
    /**********************************************************
    /* Internal methods
    /**********************************************************
     */



    private void _writeScalarBinary(Base64Variant b64variant,
            byte[] data) throws IOException
    {
        if (b64variant == Base64Variants.getDefaultVariant()) {
            b64variant = Base64Variants.MIME;
        }
        String encoded = b64variant.encode(data);
        builder.text(encoded);
    }
}
