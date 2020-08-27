package com.nedap.archie.aom;


import com.nedap.archie.base.OpenEHRBase;

/**
 * A default value container class, that contains the ODIN or JSON in the archetype. This class is ONLY
 * to be used in case a parser for the RMObjects is not available or if parsing fails, as a fallback, to ensure that
 * the text of the default value is not lost and can be serialized. In all other cases, just put a RM Object instance in
 * the default value.
 *
 * Note that this class is NOT present in the OpenEHR specifiation
 */
public class DefaultValueContainer extends OpenEHRBase {

    public static final String JSON = "json";
    public static final String ODIN = "odin";

    private String format;
    private String content;

    public DefaultValueContainer(String format, String content) {
        this.format = format;
        this.content = content;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
