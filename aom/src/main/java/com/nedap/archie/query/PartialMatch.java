package com.nedap.archie.query;

import com.nedap.archie.aom.ArchetypeModelObject;

import java.util.List;

/**
 * A query result of AOM Path queries that can be a partial match. Used to return query results halfway a query, so
 * it returns the point where the query no longer found anything.
 */
public class PartialMatch {
    private List<ArchetypeModelObject> foundObjects;
    private String pathMatched;
    private String remainingPath;

    public PartialMatch() {
    }

    public PartialMatch(List<ArchetypeModelObject> objectFound, String pathMatched, String remainingPath) {
        this.foundObjects = objectFound;
        this.pathMatched = pathMatched;
        this.remainingPath = remainingPath;
    }

    /**
     * The found objects as result of the query. Contains the root node of the document if nothing was found.
     * @return The found objects as result of the query.
     */
    public List<ArchetypeModelObject> getFoundObjects() {
        return foundObjects;
    }

    public void setFoundObjects(List<ArchetypeModelObject> foundObjects) {
        this.foundObjects = foundObjects;
    }

    /**
     * returns whether the entire query was matched
     *
     * @return true if the entire query was matched, false if part of it is remaining
     */
    public boolean isFullMatch() {
        return remainingPath.isEmpty() || remainingPath.equals("/");
    }

    /**
     * The part of the query that was matched. "/" if nothing was found.
     * @return The part of the query that was matched. "/" if nothing was found.
     */
    public String getPathMatched() {
        return pathMatched;
    }

    public void setPathMatched(String pathMatched) {
        this.pathMatched = pathMatched;
    }

    /**
     * The remaining path in the query, that no objects matched
     * @return The remaining path in the query, that no objects matched. "/" if fully found
     */
    public String getRemainingPath() {
        return remainingPath;
    }

    public void setRemainingPath(String remainingPath) {
        this.remainingPath = remainingPath;
    }
}
