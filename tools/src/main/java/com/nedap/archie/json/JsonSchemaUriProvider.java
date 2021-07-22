package com.nedap.archie.json;

import org.openehr.bmm.core.BmmClass;

/**
 * Provides a URL for a given JSON schema
 */
@FunctionalInterface
public interface JsonSchemaUriProvider {

    JsonSchemaUri provideJsonSchemaUrl(BmmClass clazz);
}
