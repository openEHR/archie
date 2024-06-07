package com.nedap.archie.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.apath.PathSegment;

import java.util.List;

public abstract class DontSerializePathMixin {

    @JsonIgnore
    public abstract List<PathSegment> getPathSegments();

    @JsonIgnore
    public abstract List<PathSegment> getPath();
}
