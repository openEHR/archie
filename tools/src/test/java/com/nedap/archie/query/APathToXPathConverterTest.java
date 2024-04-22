package com.nedap.archie.query;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class APathToXPathConverterTest {

    @Test
    public void rootPath() {
        assertConvertedQuery("/composition", "/");
        assertConvertedQuery("//", "//");
    }

    @Test
    public void relativePath() {
        assertConvertedQuery(
                "/composition/content/data[@archetype_node_id = 'id2']/events[@archetype_node_id = 'id3' and name/value = 'literal']" +
                        "/items[@archetype_node_id = 'id4' or @archetype_node_id = 'id5' and name/value = 'literal' and name/value = 'other']",
                "/content/data[id2]/events[id3, 'literal']/items[id4 or id5, 'literal' and 'other']");
    }

    @Test
    public void absolutePath() {
        assertConvertedQuery("/composition/content/data[@archetype_node_id=id2]/events[@archetype_node_id = 'id3' and items/value/value='literal']" +
                        "/items[@name='id4' or @archetype_node_id = 'id5' and name/value = 'literal' and name/value='other']",
                "/content/data[@archetype_node_id=id2]/events[id3, items/value/value='literal']/items[@name='id4' or id5, 'literal' and name/value='other']");
    }

    @Test
    public void indexedPath() {
        assertConvertedQuery("/composition/content/data[@archetype_node_id = 'id5.1' and position() = 1]/items[@archetype_node_id='id6.0.1' and position() = 42]",
                "/content/data[id5.1,1]/items[@archetype_node_id='id6.0.1', 42]");
    }

    private void assertConvertedQuery(String expected, String query) {
        assertEquals(expected, APathToXPathConverter.convertQueryToXPath(query, "composition"));
    }
}
