package com.nedap.archie.rmobjectvalidator;

import com.nedap.archie.query.RMPathQuery;

import java.util.HashMap;

/**
 * APath query cache. NOT thread-safe!
 * Created by pieter.bos on 27/05/16.
 */
public class APathQueryCache {

    private HashMap<String, RMPathQuery> queryCache = new HashMap<>();

    public RMPathQuery getApathQuery(String query) {
        RMPathQuery result = queryCache.get(query);
        if (result == null) {
            result = new RMPathQuery(query);
            queryCache.put(query, result);
        }
        return result;

    }

}
