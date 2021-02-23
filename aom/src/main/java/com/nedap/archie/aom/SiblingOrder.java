package com.nedap.archie.aom;

import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 22/10/15.
 */
@XmlType(name="SIBLING_ORDER")
public class SiblingOrder extends ArchetypeModelObject {

    private boolean before;
    private String siblingNodeId;

    public boolean isBefore() {
        return before;
    }

    public void setBefore(boolean before) {
        this.before = before;
    }

    public String getSiblingNodeId() {
        return siblingNodeId;
    }

    public void setSiblingNodeId(String siblingNodeId) {
        this.siblingNodeId = siblingNodeId;
    }

    public static SiblingOrder createAfter(String nodeId) {
        SiblingOrder result = new SiblingOrder();
        result.setSiblingNodeId(nodeId);
        result.setBefore(false);
        return result;
    }

    public static SiblingOrder createBefore(String nodeId) {
        SiblingOrder result = new SiblingOrder();
        result.setSiblingNodeId(nodeId);
        result.setBefore(true);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiblingOrder)) return false;
        SiblingOrder that = (SiblingOrder) o;
        return before == that.before &&
                Objects.equals(siblingNodeId, that.siblingNodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(before, siblingNodeId);
    }

    @Override
    public String toString() {
        return (before? "before [" : "after [") +
                siblingNodeId + "]";
    }
}
