package com.nedap.archie.aom;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nedap.archie.rules.Assertion;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="ARCHETYPE_SLOT", propOrder= {
        "includes",
        "excludes",
        "closed"
})
public class ArchetypeSlot extends CObject {

    @Nullable
    private List<Assertion> includes = new ArrayList<>();
    @Nullable
    private List<Assertion> excludes = new ArrayList<>();
    @XmlElement(name="is_closed")
    private boolean closed = false;

    public List<Assertion> getIncludes() {
        return includes;
    }

    public void setIncludes(List<Assertion> includes) {
        this.includes = includes;
    }

    public List<Assertion> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<Assertion> excludes) {
        this.excludes = excludes;
    }

    @JsonAlias("is_closed")
    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
