package com.nedap.archie.serializer.odin;

import java.util.regex.Pattern;

public class OdinEmbeddedUriParser {

    private static Pattern commentPattern = Pattern.compile("(^|\\s)--.*$", Pattern.MULTILINE);
    private static Pattern newLinePattern = Pattern.compile("\n|\r");

    /** Parses an embedded URI, in the form &lt;scheme:something&gt;, and return just the URI part
     * trims any whitespace or comments present
     * @param embeddedUri the URI, embedded in odin syntax
     * @return the parsed URI
     */
    public static String parseEmbeddedUri(String embeddedUri) {
        if(embeddedUri.length() <= 2) {
            return "";
        }
        String uri = embeddedUri.substring(1, embeddedUri.length()-1).trim();
        uri = commentPattern.matcher(uri).replaceAll("");
        return newLinePattern.matcher(uri).replaceAll("");
    }
}
