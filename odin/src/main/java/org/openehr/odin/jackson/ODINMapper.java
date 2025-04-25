package org.openehr.odin.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nedap.archie.base.Interval;
import com.nedap.archie.base.terminology.TerminologyCode;
import org.openehr.odin.jackson.serializers.*;

import java.net.URI;
import java.net.URL;

//import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Convenience version of {@link ObjectMapper} which is configured
 *
 */
public class ODINMapper extends ObjectMapper
{
    private static final long serialVersionUID = 1L;

    public ODINMapper() {
        this(new ODINFactory(new MappingJsonFactory()));
        getFactory().setCodec(this);
    }

    public ODINMapper(ODINFactory f) {
        super(f);
        setup();
    }

    /**
     * @since 2.5
     */
    public ODINMapper(ODINMapper base) {
        super(base);
        setup();
    }

    private void setup() {

        setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        enable(SerializationFeature.INDENT_OUTPUT);
        setDefaultPrettyPrinter(new ODINPrettyPrinter());
        setSerializationInclusion(JsonInclude.Include.NON_NULL);

        enableDefaultTyping(DefaultTyping.JAVA_LANG_OBJECT);

        //custom serializer for Maps and Intervals
        SimpleModule odinModule = new SimpleModule();
        odinModule.addKeySerializer(String.class, new OdinStringMapKeySerializer());
        odinModule.addKeySerializer(Integer.class, new OdinIntegerMapKeySerializer());
        odinModule.addKeySerializer(Long.class, new OdinLongMapKeySerializer());
        odinModule.addSerializer(Interval.class, new OdinIntervalSerializer());
        odinModule.addSerializer(URI.class, new OdinURISerializer());
        odinModule.addSerializer(URL.class, new OdinURLSerializer());
        odinModule.addSerializer(TerminologyCode.class, new TerminologyCodeSerializer());
        registerModule(odinModule);
    }

    /**
     * @since 2.5
     */
    @Override
    public ODINMapper copy()
    {
        _checkInvalidCopy(ODINMapper.class);
        return new ODINMapper(this);
    }

    /*
    /**********************************************************************
    /* Configuration
    /**********************************************************************
     */

    public ODINMapper configure(ODINGenerator.Feature f, boolean state) {
        return state ? enable(f) : disable(f);
    }


    public ODINMapper enable(ODINGenerator.Feature f) {
        ((ODINFactory)_jsonFactory).enable(f);
        return this;
    }

    public ODINMapper disable(ODINGenerator.Feature f) {
        ((ODINFactory)_jsonFactory).disable(f);
        return this;
    }

    public ODINMapper disable(JsonParser.Feature f) {
        ((ODINFactory)_jsonFactory).disable(f);
        return this;
    }

    /*
    /**********************************************************************
    /* Additional typed accessors
    /**********************************************************************
     */

    /**
     * Overridden with more specific type, since factory we have
     * is always of type {@link ODINFactory}
     */
    @Override
    public final ODINFactory getFactory() {
        return (ODINFactory) _jsonFactory;
    }
}
