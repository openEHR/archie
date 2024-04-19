package org.openehr.rm.archetyped;

import com.nedap.archie.base.RMObject;
import org.openehr.rm.datavalues.DvEHRURI;
import org.openehr.rm.datavalues.DvText;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LINK")
public class Link extends RMObject {

    private DvText meaning;
    private DvText type;
    private DvEHRURI target;

    public Link() {
    }

    public Link(DvText meaning, DvText type, DvEHRURI target) {
        this.meaning = meaning;
        this.type = type;
        this.target = target;
    }

    public DvText getMeaning() {
        return meaning;
    }

    public void setMeaning(DvText meaning) {
        this.meaning = meaning;
    }

    public DvText getType() {
        return type;
    }

    public void setType(DvText type) {
        this.type = type;
    }

    public DvEHRURI getTarget() {
        return target;
    }

    public void setTarget(DvEHRURI target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        return Objects.equals(meaning, link.meaning) &&
                Objects.equals(type, link.type) &&
                Objects.equals(target, link.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meaning, type, target);
    }
}
