package org.openehr.odin.jackson;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

@SuppressWarnings("resource")
public class ODINFactory extends JsonFactory
{
	private static final long serialVersionUID = 1L;

	/**
     * Name used to identify YAML format.
     * (and returned by {@link #getFormatName()}
     */
    public final static String FORMAT_NAME_ODIN = "ODIN";


    /**
     * Bitfield (set of flags) of all generator features that are enabled
     * by default.
     */    
    protected final static int DEFAULT_ODIN_GENERATOR_FEATURE_FLAGS = ODINGenerator.Feature.collectDefaults();

    private final static byte UTF8_BOM_1 = (byte) 0xEF;
    private final static byte UTF8_BOM_2 = (byte) 0xBB;
    private final static byte UTF8_BOM_3 = (byte) 0xBF;

    /*
    /**********************************************************************
    /* Configuration
    /**********************************************************************
     */

    protected int _odinGeneratorFeatures = DEFAULT_ODIN_GENERATOR_FEATURE_FLAGS;

    /** here for parsing until we properly implement odin stream parsing */
    private MappingJsonFactory jsonFactory;

    /*
    /**********************************************************************
    /* Factory construction, configuration
    /**********************************************************************
     */
    
    /**
     * Default constructor used to create factory instances.
     * Creation of a factory instance is a light-weight operation,
     * but it is still a good idea to reuse limited number of
     * factory instances (and quite often just a single instance):
     * factories are used as context for storing some reused
     * processing objects (such as symbol tables parsers use)
     * and this reuse only works within context of a single
     * factory instance.
     */
    public ODINFactory(MappingJsonFactory jsonFactory) {
        this(jsonFactory, null);
        this.jsonFactory = jsonFactory;
    }

    public ODINFactory(MappingJsonFactory jsonFactory, ObjectCodec oc)
    {
        super(oc);
        _odinGeneratorFeatures = DEFAULT_ODIN_GENERATOR_FEATURE_FLAGS;
    }

    /**
     * @since 2.2.1
     */
    public ODINFactory(MappingJsonFactory jsonFactory, ODINFactory src, ObjectCodec oc)
    {
        super(src, oc);
        this.jsonFactory = jsonFactory;
        _odinGeneratorFeatures = src._odinGeneratorFeatures;
    }

    @Override
    public ODINFactory copy()
    {
        _checkInvalidCopy(ODINFactory.class);
        return new ODINFactory(jsonFactory,  this, null);
    }

    /*
    /**********************************************************
    /* Serializable overrides
    /**********************************************************
     */

    /**
     * Method that we need to override to actually make restoration go
     * through constructors etc.
     * Also: must be overridden by sub-classes as well.
     */
    @Override
    protected Object readResolve() {
        return new ODINFactory(jsonFactory, this, _objectCodec);
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
    /* Capability introspection
    /**********************************************************
     */

    // No, we can't make use of char[] optimizations
    @Override
    public boolean canUseCharArrays() { return false; }

    // Add these in 2.7:

    /*
    @Override
    public Class<YAMLParser.Feature> getFormatReadFeatureType() {
        return YAMLParser.Feature.class;
    }

    @Override
    public Class<YAMLGenerator.Feature> getFormatWriteFeatureType() {
        return YAMLGenerator.Feature.class;
    }
    */
    
    /*
    /**********************************************************
    /* Format detection functionality
    /**********************************************************
     */
    
    @Override
    public String getFormatName() {
        return FORMAT_NAME_ODIN;
    }
    
    /**
     * Sub-classes need to override this method (as of 1.8)
     */
    @Override
    public MatchStrength hasFormat(InputAccessor acc) throws IOException
    {
        /* Actually quite possible to do, thanks to (optional) "---"
         * indicator we may be getting...
         */
        if (!acc.hasMoreBytes()) {
            return MatchStrength.INCONCLUSIVE;
        }
        byte b = acc.nextByte();
        // Very first thing, a UTF-8 BOM?
        if (b == UTF8_BOM_1) { // yes, looks like UTF-8 BOM
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() != UTF8_BOM_2) {
                return MatchStrength.NO_MATCH;
            }
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            if (acc.nextByte() != UTF8_BOM_3) {
                return MatchStrength.NO_MATCH;
            }
            if (!acc.hasMoreBytes()) {
                return MatchStrength.INCONCLUSIVE;
            }
            b = acc.nextByte();
        }
//TODO: hard to do for odin - need to find the first 'some_key = <' I think?
        return MatchStrength.INCONCLUSIVE;
    }
    
    /*
    /**********************************************************
    /* Configuration, parser settings
    /**********************************************************
     */


    /*
    /**********************************************************
    /* Configuration, generator settings
    /**********************************************************
     */

    /**
     * Method for enabling or disabling specified generator feature
     * (check {@link ODINGenerator.Feature} for list of features)
     */
    public final ODINFactory configure(ODINGenerator.Feature f, boolean state) {
        if (state) {
            enable(f);
        } else {
            disable(f);
        }
        return this;
    }


    /**
     * Method for enabling specified generator features
     * (check {@link ODINGenerator.Feature} for list of features)
     */
    public ODINFactory enable(ODINGenerator.Feature f) {
        _odinGeneratorFeatures |= f.getMask();
        return this;
    }

    /**
     * Method for disabling specified generator feature
     * (check {@link ODINGenerator.Feature} for list of features)
     */
    public ODINFactory disable(ODINGenerator.Feature f) {
        _odinGeneratorFeatures &= ~f.getMask();
        return this;
    }

    /**
     * Check whether specified generator feature is enabled.
     */
    public final boolean isEnabled(ODINGenerator.Feature f) {
        return (_odinGeneratorFeatures & f.getMask()) != 0;
    }

    /*
    /**********************************************************
    /* Overridden parser factory methods (for 2.1)
    /**********************************************************
     */

    @Override
    public JsonParser createParser(String content) throws IOException {
        return null;
    }

    @Override
    public JsonParser createParser(File f) throws IOException {
        return jsonFactory.createParser(f);
    }

    @Override
    public JsonParser createParser(URL url) throws IOException
    {
        return jsonFactory.createParser(url);
    }

    @Override
    public JsonParser createParser(InputStream in) throws IOException
    {
        return jsonFactory.createParser(in);
    }

    @Override
    public JsonParser createParser(Reader r) throws IOException
    {
        return jsonFactory.createParser(r);
    }

    @Override // since 2.4
    public JsonParser createParser(char[] data) throws IOException {
        return jsonFactory.createParser(data);
    }
    
    @Override // since 2.4
    public JsonParser createParser(char[] data, int offset, int len) throws IOException {
        return jsonFactory.createParser(data, offset, len);
    }

    @Override
    public JsonParser createParser(byte[] data) throws IOException
    {
        return jsonFactory.createParser(data);
    }

    @Override
    public JsonParser createParser(byte[] data, int offset, int len) throws IOException
    {
        return jsonFactory.createParser(data, offset, len);
    }
    
    /*
    /**********************************************************
    /* Overridden generator factory methods (2.1)
    /**********************************************************
     */

    @Override
    public ODINGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException
    {
        // false -> we won't manage the stream unless explicitly directed to
        IOContext ctxt = _createContext(out, false);
        ctxt.setEncoding(enc);
        return _createGenerator(_createWriter(_decorate(out, ctxt), enc, ctxt), ctxt);
    }

    @Override
    public ODINGenerator createGenerator(OutputStream out) throws IOException
    {
        // false -> we won't manage the stream unless explicitly directed to
        IOContext ctxt = _createContext(out, false);
        return _createGenerator(_createWriter(_decorate(out, ctxt),
                JsonEncoding.UTF8, ctxt), ctxt);
    }

    @Override
    public ODINGenerator createGenerator(Writer out) throws IOException
    {
        IOContext ctxt = _createContext(out, false);
        return _createGenerator(_decorate(out, ctxt), ctxt);
    }

    @Override
    public JsonGenerator createGenerator(File f, JsonEncoding enc) throws IOException
    {
        OutputStream out = new FileOutputStream(f);
        // true -> yes, we have to manage the stream since we created it
        IOContext ctxt = _createContext(f, true);
        ctxt.setEncoding(enc);
        return _createGenerator(_createWriter(_decorate(out, ctxt), enc, ctxt), ctxt);
    }    

    /*
    /******************************************************
    /* Overridden internal factory methods
    /******************************************************
     */

    //protected IOContext _createContext(Object srcRef, boolean resourceManaged)

    @Override
    protected JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
        return super._createParser(in, ctxt);
    }

    @Override
    protected JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
        return super._createParser(r, ctxt);
    }

    // since 2.4
    @Override
    protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt,
                                       boolean recyclable) throws IOException {
        return super._createParser(data, offset, len, ctxt, recyclable);
    }

    @Override
    protected JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
        return super._createParser(data, offset, len, ctxt);
    }

    @Override
    protected ODINGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        int feats = _odinGeneratorFeatures;
        ODINGenerator gen = new ODINGenerator(ctxt, _generatorFeatures, feats,
                _objectCodec, out);
        // any other initializations? No?
        return gen;
    }

    @Override
    protected ODINGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
        // should never get called; ensure
        throw new IllegalStateException();
    }

    /*
    /**********************************************************
    /* Internal methods
    /**********************************************************
     */

    protected final Charset UTF8 = Charset.forName("UTF-8");

}
