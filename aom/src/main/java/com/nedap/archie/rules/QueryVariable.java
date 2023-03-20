package com.nedap.archie.rules;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryVariable)) return false;
        if (!super.equals(o)) return false;
        QueryVariable that = (QueryVariable) o;
        return Objects.equals(context, that.context) &&
                Objects.equals(queryId, that.queryId) &&
                Objects.equals(queryArgs, that.queryArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                context,
                queryId,
                queryArgs);
    }
}
