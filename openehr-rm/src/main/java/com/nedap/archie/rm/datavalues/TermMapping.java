package com.nedap.archie.rm.datavalues;

import com.nedap.archie.rm.RMObject;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.xml.adapters.TermMappingMatchAdapter;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TERM_MAPPING", propOrder = {
        "match",
        "purpose",
        "target"
})
public class TermMapping extends RMObject {
    /**
     * This is an interesting one, that could be implemented with an enum
     * //TODO: look at it
     * <dl>
     *  <dt>&lt;</dt>
     *  <dd>narrower term</dd>
     *  <dt>=</dt> <dd>equals term</dd>
     *  <dt>&gt;</dt> <dd>broader term</dd>
     * <dt>?</dt> <dd>the kind of mapping is unknown</dd>
     * </dl>
     */
    @XmlJavaTypeAdapter(TermMappingMatchAdapter.class)
    private Character match = '?';
    @Nullable
    private DvCodedText purpose;
    private CodePhrase target;

    public TermMapping() {
    }

    public TermMapping(CodePhrase target, Character match, @Nullable DvCodedText purpose) {
        this.match = match;
        this.purpose = purpose;
        this.target = target;
    }

    public char getMatch() {
        return match;
    }

    public void setMatch(char match) {
        this.match = match;
    }

    @Nullable
    public DvCodedText getPurpose() {
        return purpose;
    }

    public void setPurpose(@Nullable DvCodedText purpose) {
        this.purpose = purpose;
    }

    public CodePhrase getTarget() {
        return target;
    }

    public void setTarget(CodePhrase target) {
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermMapping that = (TermMapping) o;
        return Objects.equals(match, that.match) &&
                Objects.equals(purpose, that.purpose) &&
                Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(match, purpose, target);
    }
}
