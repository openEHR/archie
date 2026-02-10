package com.nedap.archie.kryo;

import com.nedap.archie.rm.datavalues.DvURI;
import com.nedap.archie.rm.datavalues.encapsulated.DvMultimedia;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class URISerializerTest {
    @Test
    public void cloneUri() {
        DvMultimedia dvMultimedia = new DvMultimedia();
        
        dvMultimedia.setUri(new DvURI("https://example.com/image.jpg"));
        
        DvMultimedia clone = (DvMultimedia) dvMultimedia.clone();

        assertNotNull(clone.getUri());
        assertEquals("https://example.com/image.jpg", clone.getUri().getValue().toString());
    }
}
