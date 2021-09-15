package com.nedap.archie.query;

import com.nedap.archie.rminfo.ModelInfoLookup;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

class NodeNamePredicate implements Predicate<Object> {

    private final ModelInfoLookup lookup;
    private final String nodeName;

    public NodeNamePredicate(ModelInfoLookup lookup, String nodeName) {
        this.lookup = lookup;
        this.nodeName = nodeName;
    }

    @Override
    public boolean test(Object input) {
        if(input == null) {
            return false;
        }
        String nameFromRMObject = lookup.getNameFromRMObject(input);
        if(nameFromRMObject == null) {
            return false;
        }
        return equalsName(nameFromRMObject, nodeName);

    }

    private boolean equalsName(String name, String nameFromQuery) {
        //the grammar throws away whitespace. And it should, because it's kind of tricky otherwise. So match names without whitespace
        //TODO: should this be case sensitive?
        if(name == null) {
            return false;
        }
        name = name.replaceAll("( |\\t|\\n|\\r)+", "");
        nameFromQuery = nameFromQuery.replaceAll("( |\\t|\\n|\\r)+", "");
        return name.equalsIgnoreCase(nameFromQuery);

    }
}
