package com.nedap.archie.json;

import com.nedap.archie.odin3.ItemMapToListConverter;
import com.nedap.archie.odin3.TermMappingMapToListConverter;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datastructures.Item;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.TermMapping;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.TypeFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Jackson 3 ODIN-parsing converters, which convert ODIN-style maps-as-lists
 * to proper Java lists for apps that register the OdinParsing*Mixin interfaces on a Jackson 3 mapper.
 */
public class OdinParsingConverterTest {

    @Test
    public void itemMapToListConverter_convertsMapToList() {
        ItemMapToListConverter converter = new ItemMapToListConverter();
        Map<Integer, Item> map = new LinkedHashMap<>();
        Element elem = new Element("id1", new DvText("test"), null);
        map.put(1, elem);
        List<Item> result = converter.convert(map);
        assertEquals(1, result.size());
        assertSame(elem, result.get(0));
    }

    @Test
    public void itemMapToListConverter_nullReturnsNull() {
        assertNull(new ItemMapToListConverter().convert(null));
    }

    @Test
    public void itemMapToListConverter_typeMetadata() {
        ItemMapToListConverter converter = new ItemMapToListConverter();
        TypeFactory tf = JsonMapper.builder().build().getTypeFactory();
        assertNotNull(converter.getInputType(tf));
        assertNotNull(converter.getOutputType(tf));
    }

    @Test
    public void termMappingMapToListConverter_convertsMapToList() {
        TermMappingMapToListConverter converter = new TermMappingMapToListConverter();
        Map<Integer, TermMapping> map = new LinkedHashMap<>();
        TermMapping mapping = new TermMapping();
        map.put(1, mapping);
        List<TermMapping> result = converter.convert(map);
        assertEquals(1, result.size());
        assertSame(mapping, result.get(0));
    }

    @Test
    public void termMappingMapToListConverter_nullReturnsNull() {
        assertNull(new TermMappingMapToListConverter().convert(null));
    }

    @Test
    public void termMappingMapToListConverter_typeMetadata() {
        TermMappingMapToListConverter converter = new TermMappingMapToListConverter();
        TypeFactory tf = JsonMapper.builder().build().getTypeFactory();
        assertNotNull(converter.getInputType(tf));
        assertNotNull(converter.getOutputType(tf));
    }
}
