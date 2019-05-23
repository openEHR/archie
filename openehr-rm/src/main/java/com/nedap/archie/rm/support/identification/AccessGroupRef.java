package com.nedap.archie.rm.support.identification;

/**
 * Created by pieter.bos on 08/07/16.
 */
public class AccessGroupRef extends ObjectRef {

    public AccessGroupRef() {
    }

    public AccessGroupRef(ObjectId id, String namespace, String type) {
        super(id, namespace, type);
    }
}
