package com.nedap.archie.serializer.odin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class URIParserTest {

    @Test
    public void parseEmbeddedURI() {
        assertEquals("", OdinEmbeddedUriParser.parseEmbeddedUri("<>"));
        assertEquals("http://something.com", OdinEmbeddedUriParser.parseEmbeddedUri("<http://something.com>"));
        assertEquals("http://something.com", OdinEmbeddedUriParser.parseEmbeddedUri("<  http://something.com\t>"));
        assertEquals("http://something.com", OdinEmbeddedUriParser.parseEmbeddedUri("<  -- comment here\n" +
                "http://something.com\t>"));

        assertEquals("http://something.com", OdinEmbeddedUriParser.parseEmbeddedUri("<  \n" +
                "http://something.com -- comment here\t>"));
        assertEquals("http://something.com/a--notcomment", OdinEmbeddedUriParser.parseEmbeddedUri("<  \n" +
                "http://something.com/a--notcomment\t>"));
    }

}
