package com.nedap.archie.adl14.aom14;

import com.nedap.archie.base.terminology.TerminologyCode;

import java.util.Map;

public class TermBindingsList {

    private Map<String, TerminologyCode> items;

    public Map<String, TerminologyCode> getItems() {
        return items;
    }

    public void setItems(Map<String, TerminologyCode> items) {
        this.items = items;
    }
}
