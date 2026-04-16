package org.openehr.odin;

import java.util.ArrayList;
import java.util.List;

public class OdinReferenceObject extends OdinObject {
    private List<String> paths = new ArrayList<>();

    public OdinReferenceObject() {

    }

    public OdinReferenceObject(List<String> paths) {
        this.paths = paths;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPath(List<String> paths) {
        this.paths = paths;
    }
}
