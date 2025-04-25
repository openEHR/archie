package com.nedap.archie.adl14.aom14;

import java.net.URI;
import java.util.Map;

public class ConstraintBindingsList {

    //might need to change to a string?
    private Map<String, URI> items;

    public Map<String, URI> getItems() {
        return items;
    }

    public void setItems(Map<String, URI> items) {
        this.items = items;
    }
}
