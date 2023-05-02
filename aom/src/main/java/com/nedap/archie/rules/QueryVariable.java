package com.nedap.archie.rules;

/**
 * Created by pieter.bos on 27/10/15.
 */
public class QueryVariable extends VariableDeclaration {

    private String context;

    private String queryId;
    private String queryArgs;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getQueryArgs() {
        return queryArgs;
    }

    public void setQueryArgs(String queryArgs) {
        this.queryArgs = queryArgs;
    }
}
