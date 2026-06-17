package org.openehr.odin.jackson3;

import tools.jackson.core.*;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.JsonWriteContext;
import tools.jackson.core.util.JacksonFeatureSet;
import com.nedap.archie.serializer.odin.OdinStringBuilder;
import com.nedap.archie.serializer.odin.StructuredStringWriter;
import org.openehr.odin.jackson.OdinObjectWriteContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ODINGenerator3 extends GeneratorBase {
    public static final String WRITE_START_OBJECT_TYPE_MSG = "start an object";

    public enum Feature implements FormatFeature {
        ;

        protected final boolean _defaultState;
        protected final int _mask;

        public static int collectDefaults() {
            int flags = 0;
            for (Feature f : values()) {
                if (f.enabledByDefault()) {
                    flags |= f.getMask();
                }
            }
            return flags;
        }

        Feature(boolean defaultState) {
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

    protected final static long MIN_INT_AS_LONG = (long) Integer.MIN_VALUE;
    protected final static long MAX_INT_AS_LONG = (long) Integer.MAX_VALUE;
    protected final static Pattern PLAIN_NUMBER_P = Pattern.compile("[0-9]*(\\.[0-9]*)?");

    protected OdinObjectWriteContext typeIdContext;
    protected int _formatFeatures;
    protected final Writer _writer;

    // write context and pretty printer (not in GeneratorBase in Jackson 3)
    protected JsonWriteContext _writeContext;
    protected PrettyPrinter _prettyPrinter;

    private OdinStringBuilder builder;

    protected String _objectId;
    protected String _typeId;
    protected String _typeIdAtRoot;

    public ODINGenerator3(ObjectWriteContext writeCtxt, IOContext ioCtxt, int stdFeatures, int odinFeatures,
                          Writer out) throws JacksonException {
        super(writeCtxt, ioCtxt, stdFeatures);
        _formatFeatures = odinFeatures;
        _writer = out;
        _writeContext = JsonWriteContext.createRootContext(null);
        PrettyPrinter pp = writeCtxt.getPrettyPrinter();
        _prettyPrinter = (pp != null) ? pp : _constructDefaultPrettyPrinter();
        builder = new OdinStringBuilder(new StructuredStringWriter(out));
        typeIdContext = OdinObjectWriteContext.createRootContext();
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public TokenStreamContext streamWriteContext() {
        return _writeContext;
    }

    @Override
    public Object currentValue() {
        return _writeContext.currentValue();
    }

    @Override
    public void assignCurrentValue(Object value) {
        _writeContext.assignCurrentValue(value);
    }

    @Override
    public PrettyPrinter getPrettyPrinter() {
        return _prettyPrinter;
    }

    @Override
    public Object streamWriteOutputTarget() {
        return _writer;
    }

    @Override
    public int streamWriteOutputBuffered() {
        return -1;
    }

    public int getFormatFeatures() {
        return _formatFeatures;
    }

    @Override
    public JacksonFeatureSet<StreamWriteCapability> streamWriteCapabilities() {
        return DEFAULT_WRITE_CAPABILITIES;
    }

    public ODINGenerator3 enable(Feature f) {
        _formatFeatures |= f.getMask();
        return this;
    }

    public ODINGenerator3 disable(Feature f) {
        _formatFeatures &= ~f.getMask();
        return this;
    }

    public final boolean isEnabled(Feature f) {
        return (_formatFeatures & f.getMask()) != 0;
    }

    public ODINGenerator3 configure(Feature f, boolean state) {
        if (state) enable(f); else disable(f);
        return this;
    }

    @Override
    public JsonGenerator writePOJO(Object pojo) throws JacksonException {
        return this;
    }

    @Override
    public final JsonGenerator writeName(String name) throws JacksonException {
        int status = _writeContext.writeName(name);
        if (status == JsonWriteContext.STATUS_EXPECT_VALUE) {
            _reportError("Can not write a field name, expecting a value");
        }
        if (status == JsonWriteContext.STATUS_OK_AFTER_COMMA) {
            _prettyPrinter.writeObjectEntrySeparator(this);
        } else {
            _prettyPrinter.beforeObjectEntries(this);
        }
        builder.append(name);
        return this;
    }

    @Override
    public final JsonGenerator writeName(SerializableString name) throws JacksonException {
        return writeName(name.getValue());
    }

    @Override
    public final JsonGenerator writePropertyId(long id) throws JacksonException {
        return writeName(Long.toString(id));
    }

    @Override
    public final void flush() {
        try {
            _writer.flush();
        } catch (IOException e) {
            throw _wrapIOFailure(e);
        }
    }

    @Override
    protected void _closeInput() throws IOException {
        _writer.close();
    }

    @Override
    public final JsonGenerator writeStartArray() throws JacksonException {
        _verifyValueWrite("start an array");
        _writeContext = _writeContext.createChildArrayContext(null);
        typeIdContext = typeIdContext.createChild(null);
        _objectId = null;
        builder.append("<");
        return this;
    }

    @Override
    public JsonGenerator writeStartArray(Object forValue) throws JacksonException {
        return writeStartArray();
    }

    @Override
    public JsonGenerator writeStartArray(Object forValue, int size) throws JacksonException {
        return writeStartArray();
    }

    @Override
    public final JsonGenerator writeEndArray() throws JacksonException {
        if (!_writeContext.inArray()) {
            _reportError("Current context not Array but " + _writeContext.typeDesc());
        }
        _typeId = null;
        int index = _writeContext.getCurrentIndex();
        if (index == 0 && !this.typeIdContext.arrayHasObjects()) {
            builder.append(", ...");
        }
        if (typeIdContext.arrayHasObjects()) {
            _prettyPrinter.writeEndArray(this, _writeContext.getEntryCount());
        } else {
            builder.append(">");
        }
        _writeContext = _writeContext.getParent();
        typeIdContext = typeIdContext.getParent();
        return this;
    }

    @Override
    public final JsonGenerator writeStartObject() throws JacksonException {
        _verifyValueWrite(WRITE_START_OBJECT_TYPE_MSG);
        _objectId = null;
        typeIdContext.markHasChildObjects(true);
        typeIdContext = typeIdContext.createChild(_typeId);
        _typeId = null;

        if (_writeContext.inArray()) {
            if (_writeContext.getCurrentIndex() == 0) {
                _prettyPrinter.writeStartArray(this);
                _prettyPrinter.beforeArrayValues(this);
            }
            builder.append("[" + (_writeContext.getCurrentIndex() + 1) + "] = ");
        }

        if (typeIdContext.hasTypeId()) {
            builder.append("(").append(typeIdContext.getTypeId().toString()).append(") ");
            _prettyPrinter.writeStartObject(this);
        } else if (!_writeContext.inRoot()) {
            _prettyPrinter.writeStartObject(this);
        }

        _writeContext = _writeContext.createChildObjectContext(null);
        return this;
    }

    @Override
    public JsonGenerator writeStartObject(Object forValue) throws JacksonException {
        return writeStartObject();
    }

    @Override
    public JsonGenerator writeStartObject(Object forValue, int size) throws JacksonException {
        return writeStartObject();
    }

    @Override
    public final JsonGenerator writeEndObject() throws JacksonException {
        if (!_writeContext.inObject()) {
            _reportError("Current context not Object but " + _writeContext.typeDesc());
        }
        _writeContext = _writeContext.getParent();
        if (_writeContext.inArray()) {
            _prettyPrinter.writeEndObject(this, _writeContext.getEntryCount());
        } else if (_writeContext.inRoot() && _typeIdAtRoot != null) {
            _prettyPrinter.writeEndObject(this, _writeContext.getEntryCount());
        } else if (!_writeContext.inRoot()) {
            _prettyPrinter.writeEndObject(this, _writeContext.getEntryCount());
        }
        _typeId = null;
        typeIdContext = typeIdContext.getParent();
        return this;
    }

    @Override
    public JsonGenerator writeString(String text) throws JacksonException {
        if (text == null) {
            return writeNull();
        }
        _verifyValueWrite("write String value");
        writeFieldStart();
        builder.text(text);
        writeFieldEnd();
        return this;
    }

    @Override
    public JsonGenerator writeString(char[] text, int offset, int len) throws JacksonException {
        return writeString(new String(text, offset, len));
    }

    @Override
    public JsonGenerator writeString(Reader reader, int len) throws JacksonException {
        _reportUnsupportedOperation();
        return this;
    }

    @Override
    public final JsonGenerator writeString(SerializableString sstr) throws JacksonException {
        return writeString(sstr.toString());
    }

    @Override
    public JsonGenerator writeRawUTF8String(byte[] text, int offset, int len) throws JacksonException {
        _reportUnsupportedOperation();
        return this;
    }

    @Override
    public JsonGenerator writeUTF8String(byte[] text, int offset, int len) throws JacksonException {
        return writeString(new String(text, offset, len, StandardCharsets.UTF_8));
    }

    @Override
    public JsonGenerator writeRaw(String text) throws JacksonException {
        builder.append(text);
        return this;
    }

    @Override
    public JsonGenerator writeRaw(String text, int offset, int len) throws JacksonException {
        builder.append(new String(text.getBytes(), offset, len));
        return this;
    }

    @Override
    public JsonGenerator writeRaw(char[] text, int offset, int len) throws JacksonException {
        builder.append(new String(text, offset, len));
        return this;
    }

    @Override
    public JsonGenerator writeRaw(char c) throws JacksonException {
        builder.append(c);
        return this;
    }

    @Override
    public JsonGenerator writeRawValue(String text) throws JacksonException {
        _verifyValueWrite("write raw value");
        writeFieldStart();
        builder.append(text);
        writeFieldEnd();
        return this;
    }

    @Override
    public JsonGenerator writeRawValue(String text, int offset, int len) throws JacksonException {
        _reportUnsupportedOperation();
        return this;
    }

    @Override
    public JsonGenerator writeRawValue(char[] text, int offset, int len) throws JacksonException {
        _reportUnsupportedOperation();
        return this;
    }

    @Override
    public JsonGenerator writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws JacksonException {
        if (data == null) {
            return writeNull();
        }
        _verifyValueWrite("write Binary value");
        if (offset > 0 || (offset + len) != data.length) {
            data = Arrays.copyOfRange(data, offset, offset + len);
        }
        _writeScalarBinary(b64variant, data);
        return this;
    }

    @Override
    public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws JacksonException {
        _reportUnsupportedOperation();
        return -1;
    }

    @Override
    public JsonGenerator writeBoolean(boolean state) throws JacksonException {
        _verifyValueWrite("write boolean value");
        writeFieldStart();
        builder.append(state ? "True" : "False");
        writeFieldEnd();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(short v) throws JacksonException {
        return writeNumber((int) v);
    }

    @Override
    public JsonGenerator writeNumber(int i) throws JacksonException {
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(Integer.toString(i));
        writeFieldEnd();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(long l) throws JacksonException {
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(Long.toString(l));
        writeFieldEnd();
        return this;
    }

    private void writeFieldStart() {
        if (!_writeContext.inArray()) {
            builder.append("<");
        }
    }

    private void writeFieldEnd() {
        if (!_writeContext.inArray()) {
            builder.append(">");
        }
    }

    @Override
    public JsonGenerator writeNumber(BigInteger v) throws JacksonException {
        if (v == null) {
            return writeNull();
        }
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(v.toString());
        writeFieldEnd();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(double d) throws JacksonException {
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(Double.toString(d));
        writeFieldEnd();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(float f) throws JacksonException {
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.append(Float.toString(f));
        writeFieldEnd();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(BigDecimal dec) throws JacksonException {
        if (dec == null) {
            return writeNull();
        }
        _verifyValueWrite("write number");
        String str = isEnabled(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN)
                ? dec.toPlainString() : dec.toString();
        writeFieldStart();
        builder.append(str);
        writeFieldEnd();
        return this;
    }

    @Override
    public JsonGenerator writeNumber(String encodedValue) throws JacksonException {
        if (encodedValue == null) {
            return writeNull();
        }
        _verifyValueWrite("write number");
        writeFieldStart();
        builder.text(encodedValue);
        writeFieldEnd();
        return this;
    }

    @Override
    public JsonGenerator writeNull() throws JacksonException {
        _verifyValueWrite("write null value");
        writeFieldStart();
        writeFieldEnd();
        return this;
    }

    @Override
    public boolean canWriteObjectId() {
        return false;
    }

    @Override
    public boolean canWriteTypeId() {
        return true;
    }

    @Override
    public JsonGenerator writeTypeId(Object id) throws JacksonException {
        _typeId = String.valueOf(id);
        if (_writeContext.inRoot()) {
            _typeIdAtRoot = _typeId;
        }
        return this;
    }

    @Override
    public JsonGenerator writeObjectRef(Object id) throws JacksonException {
        _verifyValueWrite("write Object reference");
        return this;
    }

    @Override
    public JsonGenerator writeObjectId(Object id) throws JacksonException {
        _objectId = String.valueOf(id);
        return this;
    }

    @Override
    protected final void _verifyValueWrite(String typeMsg) throws JacksonException {
        int status = _writeContext.writeValue();
        switch (status) {
            case JsonWriteContext.STATUS_OK_AFTER_COMMA:
                if (_writeContext.inArray()) {
                    if (typeIdContext.arrayHasObjects()) {
                        _prettyPrinter.writeArrayValueSeparator(this);
                    } else {
                        writeRaw(", ");
                    }
                }
                break;
            case JsonWriteContext.STATUS_OK_AFTER_COLON:
                _prettyPrinter.writeObjectNameValueSeparator(this);
                break;
            case JsonWriteContext.STATUS_OK_AFTER_SPACE:
                _prettyPrinter.writeRootValueSeparator(this);
                break;
            case JsonWriteContext.STATUS_OK_AS_IS:
                if (_writeContext.inArray()) {
                    // do nothing yet; pretty printer called in writeStartObject if needed
                } else if (_writeContext.inObject()) {
                    _prettyPrinter.beforeObjectEntries(this);
                }
                break;
            case JsonWriteContext.STATUS_EXPECT_NAME:
                _reportError("Can not " + typeMsg + ", expecting field name");
                break;
            default:
                _throwInternal();
                break;
        }
    }

    @Override
    protected PrettyPrinter _constructDefaultPrettyPrinter() {
        return new ODINPrettyPrinter3();
    }

    @Override
    protected void _releaseBuffers() {
        // nothing special
    }

    private void _writeScalarBinary(Base64Variant b64variant, byte[] data) throws JacksonException {
        if (b64variant == Base64Variants.getDefaultVariant()) {
            b64variant = Base64Variants.MIME;
        }
        String encoded = b64variant.encode(data);
        builder.text(encoded);
    }
}
