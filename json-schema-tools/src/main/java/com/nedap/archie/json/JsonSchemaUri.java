package com.nedap.archie.json;

import java.util.Objects;

public class JsonSchemaUri {

    // the value of the $id property
    private String baseUri;
    // the filename to be used in ref
    private String filename;

    public JsonSchemaUri(String baseUri, String filename) {
        this.baseUri = baseUri;
        this.filename = filename;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonSchemaUri that = (JsonSchemaUri) o;
        return Objects.equals(baseUri, that.baseUri) &&
                Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUri, filename);
    }

    public String getId() {
        return baseUri + filename;
    }


    @Override
    public String toString() {
        return getId();
    }
}
