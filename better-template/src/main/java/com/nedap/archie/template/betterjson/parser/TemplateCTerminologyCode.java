package com.nedap.archie.template.betterjson.parser;

import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.rm.support.identification.TerminologyId;

import java.util.List;

public class TemplateCTerminologyCode extends CTerminologyCode {

    private TerminologyId terminologyId;
    private List<String> selectedTerminologies;
    private List<TemplateTermCode> includedExternalTerminologyCodes;

    public TerminologyId getTerminologyId() {
        return terminologyId;
    }

    public void setTerminologyId(TerminologyId terminologyId) {
        this.terminologyId = terminologyId;
    }

    public List<String> getSelectedTerminologies() {
        return selectedTerminologies;
    }

    public void setSelectedTerminologies(List<String> selectedTerminologies) {
        this.selectedTerminologies = selectedTerminologies;
    }

    public List<TemplateTermCode> getIncludedExternalTerminologyCodes() {
        return includedExternalTerminologyCodes;
    }

    public void setIncludedExternalTerminologyCodes(List<TemplateTermCode> includedExternalTerminologyCodes) {
        this.includedExternalTerminologyCodes = includedExternalTerminologyCodes;
    }
}
