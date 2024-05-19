package org.openehr.rm.demographic;

import org.openehr.rm.support.identification.PartyRef;
import org.openehr.rm.datavalues.DvText;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 08/07/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="ACTOR", propOrder = {
        "roles",
        "languages"
})
public abstract class Actor extends Party {

    @Nullable
    private List<DvText> languages = new ArrayList<>();
    @Nullable
    private List<PartyRef> roles = new ArrayList<>();

    public List<DvText> getLanguages() {
        return languages;
    }

    public void setLanguages(List<DvText> languages) {
        this.languages = languages;
    }

    public void addLanguage(DvText language) {
        this.languages.add(language);
    }

    public List<PartyRef> getRoles() {
        return roles;
    }

    public void setRoles(List<PartyRef> roles) {
        this.roles = roles;
    }

    public void addRole(PartyRef role) {
        this.roles.add(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Actor actor = (Actor) o;
        return Objects.equals(languages, actor.languages) &&
                Objects.equals(roles, actor.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), languages, roles);
    }
}
