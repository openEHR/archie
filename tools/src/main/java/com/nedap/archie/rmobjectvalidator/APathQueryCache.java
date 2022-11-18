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

    public APathQueryCache() {
        this(false);
    }

    public APathQueryCache(boolean matchSpecialisedNodes) {
        this.matchSpecialisedNodes = matchSpecialisedNodes;
    }

    public RMPathQuery getApathQuery(String query) {
        RMPathQuery result = queryCache.get(query);
        if (result == null) {
            result = new RMPathQuery(query, matchSpecialisedNodes);
            queryCache.put(query, result);
        }
        return result;

    }

}
