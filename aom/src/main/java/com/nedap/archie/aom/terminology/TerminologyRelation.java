package com.nedap.archie.aom.terminology;


import com.nedap.archie.aom.ArchetypeModelObject;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Class whose instances represent any kind of 1:N relationship between a source term and 1-N target terms.
 */
public class TerminologyRelation extends ArchetypeModelObject {

    /**
     * Code of source term of this relation.
     *
     */
    private String id;

    /**
     * List of target terms in this relation.
     *
     */
    private Set<String> members = new LinkedHashSet<>();

    /**
     * Returns code of source term of this relation.
     *
     * @return to code of the source term
     */
    public String getId() {
        return id;
    }

    /**
     * Sets code of source term of this relation.
     *
     * @param id the id code to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns list of target terms in this relation.
     *
     * @return the list of target terms
     */
    public Set<String> getMembers() {
        return members;
    }

    /**
     * Sets list of target terms in this relation.
     *
     * @param members the list of members
     */
    public void setMembers(Collection<String> members) {
        this.members = new LinkedHashSet<>(members);
    }


    /**
     * Add a member to this relation
     *
     * @param member the member to add
     */
    public void addMember(String member) {
        this.members.add(member);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TerminologyRelation that = (TerminologyRelation) obj;
        return id.equals(that.id) && members.equals(that.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, members);
    }
}
