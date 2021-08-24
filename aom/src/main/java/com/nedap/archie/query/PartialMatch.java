package com.nedap.archie.query;

import com.nedap.archie.aom.ArchetypeModelObject;

import java.util.List;

public class PartialMatch {
    private List<ArchetypeModelObject> found;
    private String pathMatched;
    private String remainingPath;

    public PartialMatch() {
    }

    public PartialMatch(List<ArchetypeModelObject> found, String pathMatched, String remainingPath) {
        this.found = found;
        this.pathMatched = pathMatched;
        this.remainingPath = remainingPath;
    }

    public List<ArchetypeModelObject> getFound() {
        return found;
    }

    public void setFound(List<ArchetypeModelObject> found) {
        this.found = found;
    }

    public String getPathMatched() {
        return pathMatched;
    }

    public void setPathMatched(String pathMatched) {
        this.pathMatched = pathMatched;
    }

    public String getRemainingPath() {
        return remainingPath;
    }

    public void setRemainingPath(String remainingPath) {
        this.remainingPath = remainingPath;
    }
}
