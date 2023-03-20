package com.nedap.archie.aom;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name="C_SECOND_ORDER")
public class CSecondOrder<T extends ArchetypeConstraint> extends ArchetypeModelObject {
    @Nullable
    private List<T> members = new ArrayList<>();

    public T getMember(int i) {
        return members.get(i);
    }

    public List<T> getMembers() {
        return members;
    }

    public void setMembers(List<T> members) {
        this.members = members;
        for(T member:members) {
            setThisAsSocParent(member);
        }
    }

    private void setThisAsSocParent(T member) {
        if(member != null) {
            member.setSocParent(this);
        }
    }

    public void addMember(T member) {
        members.add(member);
        setThisAsSocParent(member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CSecondOrder)) return false;
        CSecondOrder<?> that = (CSecondOrder<?>) o;
        return Objects.equals(members, that.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(members);
    }
}
