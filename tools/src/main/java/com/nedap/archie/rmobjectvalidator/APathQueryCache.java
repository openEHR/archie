package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.query.RMPathQuery;

import java.util.HashMap;

/**
 * APath query cache. NOT thread-safe!
 * Created by pieter.bos on 27/05/16.
 */
public class APathQueryCache {

    private final boolean matchSpecialisedNodes;
    private final HashMap<String, RMPathQuery> queryCache = new HashMap<>();
    private final HashMap<String, RMPathQuery> attributeCache = new HashMap<>();

    public APathQueryCache() {
        this(false);
    }

    public APathQueryCache(boolean matchSpecialisedNodes) {
        this.matchSpecialisedNodes = matchSpecialisedNodes;
    }

    public RMPathQuery getApathQuery(String query) {
        return queryCache.computeIfAbsent(query, q -> new RMPathQuery(q, matchSpecialisedNodes));
    }

    public RMPathQuery getForAttribute(String attributeName) {
        return attributeCache.computeIfAbsent(attributeName, att -> new RMPathQuery('/'  +att, matchSpecialisedNodes));
    }

    public RMPathQuery getForAttribute(String attributeName, String nodeId) {
        return getApathQuery('/'  +attributeName + '[' + nodeId + ']');
    }

}
