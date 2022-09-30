package com.nedap.archie.kryo;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.serializers.ImmutableSerializer;

import java.net.URI;

/**
 * A safe serializer for java.net.URI to better support cloning of a DvUri RMObject.
 * This implementation is functionally correct, but irrelevant because we only clone objects.
 * Should be removed in Kryo 6 where it will be registered by default.
 */
public class URISerializer extends ImmutableSerializer<URI> {

    @Override
    public void write(final Kryo kryo, final Output output, final URI uri) {
        output.writeString(uri.toString());
    }

    @Override
    public URI read(final Kryo kryo, final Input input, final Class<? extends URI> uriClass) {
        return URI.create(input.readString());
    }
}