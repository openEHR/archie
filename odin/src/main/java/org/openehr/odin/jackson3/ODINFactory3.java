package org.openehr.odin.jackson3;

import tools.jackson.core.*;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.JsonFactory;

import java.io.OutputStream;
import java.io.Writer;

public class ODINFactory3 extends JsonFactory {
    private static final long serialVersionUID = 1L;

    public final static String FORMAT_NAME_ODIN = "ODIN";

    protected final static int DEFAULT_ODIN_GENERATOR_FEATURE_FLAGS = ODINGenerator3.Feature.collectDefaults();

    protected int _odinGeneratorFeatures = DEFAULT_ODIN_GENERATOR_FEATURE_FLAGS;

    public ODINFactory3() {
        super();
        _odinGeneratorFeatures = DEFAULT_ODIN_GENERATOR_FEATURE_FLAGS;
    }

    public ODINFactory3(ODINFactory3 src) {
        super(src);
        _odinGeneratorFeatures = src._odinGeneratorFeatures;
    }

    @Override
    public ODINFactory3 copy() {
        return new ODINFactory3(this);
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME_ODIN;
    }

    @Override
    public boolean canUseCharArrays() {
        return false;
    }

    public final ODINFactory3 configure(ODINGenerator3.Feature f, boolean state) {
        if (state) enable(f); else disable(f);
        return this;
    }

    public ODINFactory3 enable(ODINGenerator3.Feature f) {
        _odinGeneratorFeatures |= f.getMask();
        return this;
    }

    public ODINFactory3 disable(ODINGenerator3.Feature f) {
        _odinGeneratorFeatures &= ~f.getMask();
        return this;
    }

    public final boolean isEnabled(ODINGenerator3.Feature f) {
        return (_odinGeneratorFeatures & f.getMask()) != 0;
    }

    @Override
    protected ODINGenerator3 _createGenerator(ObjectWriteContext writeCtxt, IOContext ioCtxt, Writer out)
            throws JacksonException {
        return new ODINGenerator3(writeCtxt, ioCtxt, _streamWriteFeatures, _odinGeneratorFeatures, out);
    }

    @Override
    protected JsonGenerator _createUTF8Generator(ObjectWriteContext writeCtxt, IOContext ioCtxt, OutputStream out)
            throws JacksonException {
        throw new UnsupportedOperationException("UTF-8 byte stream generator not supported for ODIN");
    }
}
