package com.nedap.archie.aom.rmoverlay;

import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.base.terminology.TerminologyCode;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Objects;

public class RmAttributeVisibility extends ArchetypeModelObject {

    @Nullable
    private VisibilityType visibility;
    @Nullable
    private TerminologyCode alias;

    public RmAttributeVisibility() {
    }

    public RmAttributeVisibility(VisibilityType visibility, TerminologyCode alias) {
        this.visibility = visibility;
        this.alias = alias;
    }

    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    public TerminologyCode getAlias() {
        return alias;
    }

    public void setAlias(TerminologyCode alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RmAttributeVisibility that = (RmAttributeVisibility) o;
        return visibility == that.visibility &&
                Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visibility, alias);
    }
}
